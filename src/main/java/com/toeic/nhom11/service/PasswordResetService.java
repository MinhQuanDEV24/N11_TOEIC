package com.toeic.nhom11.service;

public interface PasswordResetService {
    void requestReset(String email);
    boolean resetPassword(String token, String newPassword);
}
