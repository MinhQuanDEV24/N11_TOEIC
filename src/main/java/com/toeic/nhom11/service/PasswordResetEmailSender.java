package com.toeic.nhom11.service;

public interface PasswordResetEmailSender {
    void sendResetLink(String email, String resetUrl);
}
