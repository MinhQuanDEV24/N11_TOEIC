package com.toeic.nhom11.service.impl;

import com.toeic.nhom11.dao.PasswordResetDao;
import com.toeic.nhom11.service.PasswordResetEmailSender;
import com.toeic.nhom11.service.PasswordResetService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Base64;
import java.util.HexFormat;
import java.util.Locale;

@Service
public class PasswordResetServiceImpl implements PasswordResetService {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int TOKEN_BYTES = 32;
    private static final int TOKEN_LIFETIME_MINUTES = 30;
    private static final int REQUEST_COOLDOWN_SECONDS = 60;

    private final PasswordResetDao dao;
    private final PasswordResetEmailSender emailSender;
    private final PasswordEncoder passwordEncoder;
    private final String publicBaseUrl;

    public PasswordResetServiceImpl(
        PasswordResetDao dao,
        PasswordResetEmailSender emailSender,
        PasswordEncoder passwordEncoder,
        @Value("${app.password-reset.base-url}") String publicBaseUrl
    ) {
        this.dao = dao;
        this.emailSender = emailSender;
        this.passwordEncoder = passwordEncoder;
        this.publicBaseUrl = publicBaseUrl;
    }

    @Override
    @Transactional(noRollbackFor = MailException.class)
    public void requestReset(String submittedEmail) {
        String email = submittedEmail.trim().toLowerCase(Locale.ROOT);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        dao.deleteExpiredTokens(now);

        var userId = dao.activeUserIdByEmail(email);
        if (userId.isEmpty() || dao.hasRecentRequest(userId.get(), now.minusSeconds(REQUEST_COOLDOWN_SECONDS))) {
            return;
        }

        byte[] tokenBytes = new byte[TOKEN_BYTES];
        RANDOM.nextBytes(tokenBytes);
        String token = Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
        dao.deleteTokensForUser(userId.get());
        dao.insertToken(userId.get(), hashToken(token), now.plusMinutes(TOKEN_LIFETIME_MINUTES), now);
        emailSender.sendResetLink(email, resetUrl(token));
    }

    @Override
    @Transactional
    public boolean resetPassword(String token, String newPassword) {
        if (token == null || token.isBlank()) return false;
        if (newPassword.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException("Mật khẩu vượt quá giới hạn 72 byte của BCrypt.");
        }

        String tokenHash = hashToken(token);
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        if (!dao.hasValidToken(tokenHash, now)) return false;

        String passwordHash = passwordEncoder.encode(newPassword);
        return dao.consumeTokenAndUpdatePassword(tokenHash, passwordHash, now);
    }

    private String resetUrl(String token) {
        URI base = URI.create(publicBaseUrl);
        String scheme = base.getScheme();
        if (base.getHost() == null || scheme == null
            || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))
            || base.getUserInfo() != null || base.getQuery() != null || base.getFragment() != null) {
            throw new IllegalStateException("APP_PUBLIC_BASE_URL must be an absolute HTTP(S) URL without credentials or query.");
        }
        return publicBaseUrl.replaceAll("/+$", "") + "/reset-password?token=" + token;
    }

    private String hashToken(String token) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                .digest(token.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is unavailable.", exception);
        }
    }
}
