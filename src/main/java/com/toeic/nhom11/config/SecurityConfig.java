package com.toeic.nhom11.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean UserDetailsService userDetailsService(JdbcTemplate jdbc) {
        return email -> jdbc.query("SELECT email,password_hash,role,status FROM users WHERE email=?",
            (rs, row) -> User.withUsername(rs.getString("email"))
                .password(rs.getString("password_hash")).roles(rs.getString("role"))
                .disabled(!"ACTIVE".equals(rs.getString("status"))).build(), email.trim().toLowerCase(java.util.Locale.ROOT))
            .stream().findFirst().orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy tài khoản"));
    }

    @Bean SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/login", "/register", "/forgot-password", "/reset-password",
                "/assets/**", "/css/**", "/js/**",
                "/images/**", "/webjars/**", "/favicon.ico").permitAll()
            .requestMatchers("/admin/**").hasRole("ADMIN")
            .requestMatchers("/vocabulary/preferences", "/vocabulary/*/placement/**",
                "/vocabulary/*/lessons/*/quiz/**").authenticated()
            .requestMatchers("/vocabulary/flashcard/*/bookmark").authenticated()
            .requestMatchers("/vocabulary", "/vocabulary/*", "/vocabulary/flashcard/next").permitAll()
            .requestMatchers("/vocabulary/*/submit", "/vocabulary/*/progress", "/vocabulary/*/favorite",
                "/vocabulary/attempts/**", "/vocabulary/history").authenticated()
            .requestMatchers("/listening/**", "/reading/**", "/exams/**",
                "/roadmap/**", "/progress/**", "/profile", "/grammar/*/submit",
                "/grammar/attempts/**", "/grammar/history").authenticated()
            .anyRequest().permitAll())
            .formLogin(login -> login.loginPage("/login").usernameParameter("email")
                .defaultSuccessUrl("/grammar", false).failureUrl("/login?error").permitAll())
            .logout(logout -> logout.logoutSuccessUrl("/grammar"))
            .build();
    }
}
