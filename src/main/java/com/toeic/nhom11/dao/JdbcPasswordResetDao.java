package com.toeic.nhom11.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public class JdbcPasswordResetDao implements PasswordResetDao {
    private final JdbcTemplate jdbc;

    public JdbcPasswordResetDao(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<Long> activeUserIdByEmail(String email) {
        return jdbc.query("SELECT id FROM users WHERE email=? AND status='ACTIVE'",
                (rs, row) -> rs.getLong("id"), email)
            .stream()
            .findFirst();
    }

    @Override
    public boolean hasRecentRequest(long userId, LocalDateTime since) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM password_reset_tokens WHERE user_id=? AND created_at>=?",
            Integer.class, userId, since);
        return count != null && count > 0;
    }

    @Override
    public void deleteExpiredTokens(LocalDateTime now) {
        jdbc.update("DELETE FROM password_reset_tokens WHERE expires_at<=?", now);
    }

    @Override
    public void deleteTokensForUser(long userId) {
        jdbc.update("DELETE FROM password_reset_tokens WHERE user_id=?", userId);
    }

    @Override
    public void insertToken(long userId, String tokenHash, LocalDateTime expiresAt, LocalDateTime createdAt) {
        jdbc.update(
            "INSERT INTO password_reset_tokens (user_id, token_hash, expires_at, created_at) VALUES (?, ?, ?, ?)",
            userId, tokenHash, expiresAt, createdAt);
    }

    @Override
    public boolean hasValidToken(String tokenHash, LocalDateTime now) {
        Integer count = jdbc.queryForObject(
            "SELECT COUNT(*) FROM password_reset_tokens WHERE token_hash=? AND used_at IS NULL AND expires_at>?",
            Integer.class, tokenHash, now);
        return count != null && count > 0;
    }

    @Override
    public boolean consumeTokenAndUpdatePassword(String tokenHash, String passwordHash, LocalDateTime now) {
        var userIds = jdbc.query(
            "SELECT user_id FROM password_reset_tokens WHERE token_hash=? AND used_at IS NULL AND expires_at>? FOR UPDATE",
            (rs, row) -> rs.getLong("user_id"), tokenHash, now);
        if (userIds.isEmpty()) return false;

        long userId = userIds.get(0);
        int updated = jdbc.update(
            "UPDATE users SET password_hash=?, updated_at=? WHERE id=?",
            passwordHash, now, userId);
        if (updated != 1) return false;

        jdbc.update(
            "UPDATE password_reset_tokens SET used_at=? WHERE user_id=? AND used_at IS NULL",
            now, userId);
        return true;
    }
}
