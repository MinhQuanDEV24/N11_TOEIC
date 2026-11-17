package com.toeic.nhom11;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.HexFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class PasswordResetIntegrationTests {
    @Autowired MockMvc mvc;
    @Autowired JdbcTemplate jdbc;
    @Autowired PasswordEncoder passwordEncoder;
    @MockitoBean JavaMailSender mailSender;

    @Test
    void guestCanOpenPasswordResetPagesAndTokenPageIsNotCached() throws Exception {
        mvc.perform(get("/forgot-password"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/forgot-password"));
        mvc.perform(get("/reset-password").param("token", "sample-token"))
            .andExpect(status().isOk())
            .andExpect(header().string("Cache-Control", "no-store"))
            .andExpect(header().string("Referrer-Policy", "no-referrer"))
            .andExpect(view().name("auth/reset-password"));
    }

    @Test
    void unknownAndKnownEmailsReceiveTheSamePublicResponse() throws Exception {
        var known = mvc.perform(post("/forgot-password").with(csrf())
                .param("email", "student@n11.local"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/forgot-password"))
            .andReturn();
        var unknown = mvc.perform(post("/forgot-password").with(csrf())
                .param("email", "unknown@n11.local"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/forgot-password"))
            .andReturn();

        assertEquals(known.getFlashMap().get("resetRequestSent"), unknown.getFlashMap().get("resetRequestSent"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void emailDeliveryFailureStillReturnsGenericResponse() throws Exception {
        doThrow(new MailSendException("SMTP unavailable"))
            .when(mailSender).send(any(SimpleMailMessage.class));

        mvc.perform(post("/forgot-password").with(csrf())
                .param("email", "student@n11.local"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/forgot-password"))
            .andExpect(flash().attribute("resetRequestSent", true));
        assertEquals(1, jdbc.queryForObject(
            "SELECT COUNT(*) FROM password_reset_tokens "
                + "WHERE user_id=(SELECT id FROM users WHERE email=?)",
            Integer.class, "student@n11.local"));
        verify(mailSender).send(any(SimpleMailMessage.class));
    }

    @Test
    void tokenIsHashedStoredSingleUseAndChangesThePassword() throws Exception {
        String oldPasswordHash = jdbc.queryForObject(
            "SELECT password_hash FROM users WHERE email=?", String.class, "student@n11.local");
        String token = requestResetAndGetToken();
        String tokenHash = sha256(token);
        assertEquals(tokenHash, jdbc.queryForObject(
            "SELECT token_hash FROM password_reset_tokens "
                + "WHERE user_id=(SELECT id FROM users WHERE email=?)",
            String.class, "student@n11.local"));
        assertNotEquals(token, tokenHash);

        mvc.perform(post("/reset-password").with(csrf())
                .param("token", token)
                .param("password", "NewPass123!")
                .param("confirmPassword", "NewPass123!"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/login?reset=success"));

        String newPasswordHash = jdbc.queryForObject(
            "SELECT password_hash FROM users WHERE email=?", String.class, "student@n11.local");
        assertNotEquals(oldPasswordHash, newPasswordHash);
        assertTrue(passwordEncoder.matches("NewPass123!", newPasswordHash));
        assertNotNull(jdbc.queryForObject(
            "SELECT used_at FROM password_reset_tokens WHERE token_hash=?", LocalDateTime.class, tokenHash));

        mvc.perform(post("/reset-password").with(csrf())
                .param("token", token)
                .param("password", "AnotherPass123!")
                .param("confirmPassword", "AnotherPass123!"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/reset-password"))
            .andExpect(model().attributeExists("formError"));
        assertTrue(passwordEncoder.matches("NewPass123!", newPasswordHash));
    }

    @Test
    void expiredTokenCannotChangeThePassword() throws Exception {
        String token = requestResetAndGetToken();
        String tokenHash = sha256(token);
        jdbc.update("UPDATE password_reset_tokens SET expires_at=? WHERE token_hash=?",
            LocalDateTime.now(ZoneOffset.UTC).minusMinutes(1), tokenHash);

        mvc.perform(post("/reset-password").with(csrf())
                .param("token", token)
                .param("password", "NewPass123!")
                .param("confirmPassword", "NewPass123!"))
            .andExpect(status().isOk())
            .andExpect(view().name("auth/reset-password"))
            .andExpect(model().attributeExists("formError"));
        assertFalse(passwordEncoder.matches("NewPass123!",
            jdbc.queryForObject("SELECT password_hash FROM users WHERE email=?", String.class, "student@n11.local")));
    }

    @Test
    void resetRequestCooldownAvoidsRepeatedEmails() throws Exception {
        mvc.perform(post("/forgot-password").with(csrf()).param("email", "student@n11.local"))
            .andExpect(redirectedUrl("/forgot-password"));
        mvc.perform(post("/forgot-password").with(csrf()).param("email", "student@n11.local"))
            .andExpect(redirectedUrl("/forgot-password"));
        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    private String requestResetAndGetToken() throws Exception {
        mvc.perform(post("/forgot-password").with(csrf()).param("email", "student@n11.local"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/forgot-password"));

        ArgumentCaptor<SimpleMailMessage> message = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(message.capture());
        Matcher matcher = Pattern.compile("token=([A-Za-z0-9_-]+)").matcher(message.getValue().getText());
        assertTrue(matcher.find(), "Reset email should contain the one-time token URL.");
        return matcher.group(1);
    }

    private String sha256(String token) throws NoSuchAlgorithmException {
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(token.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(digest);
    }
}
