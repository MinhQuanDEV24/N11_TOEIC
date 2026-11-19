package com.toeic.nhom11.dao;

import java.time.LocalDateTime;
import java.util.Optional;

public interface PasswordResetDao {
    Optional<Long> activeUserIdByEmail(String email);
    boolean hasRecentRequest(long userId, LocalDateTime since);
    void deleteExpiredTokens(LocalDateTime now);
    void deleteTokensForUser(long userId);
    void insertToken(long userId, String tokenHash, LocalDateTime expiresAt, LocalDateTime createdAt);
    boolean hasValidToken(String tokenHash, LocalDateTime now);
    boolean consumeTokenAndUpdatePassword(String tokenHash, String passwordHash, LocalDateTime now);
}
