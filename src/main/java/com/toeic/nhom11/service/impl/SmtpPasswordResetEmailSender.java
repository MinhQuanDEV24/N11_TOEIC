package com.toeic.nhom11.service.impl;

import com.toeic.nhom11.service.PasswordResetEmailSender;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class SmtpPasswordResetEmailSender implements PasswordResetEmailSender {
    private final JavaMailSender mailSender;
    private final String from;

    public SmtpPasswordResetEmailSender(
        JavaMailSender mailSender,
        @Value("${app.password-reset.from}") String from
    ) {
        this.mailSender = mailSender;
        this.from = from;
    }

    @Override
    public void sendResetLink(String email, String resetUrl) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(email);
        message.setSubject("Đặt lại mật khẩu N11 TOEIC");
        message.setText("""
            Bạn nhận được email này vì đã yêu cầu đặt lại mật khẩu cho N11 TOEIC.

            Mở liên kết sau để tạo mật khẩu mới (liên kết có hiệu lực trong 30 phút và chỉ dùng một lần):
            %s

            Nếu bạn không yêu cầu thao tác này, hãy bỏ qua email.
            """.formatted(resetUrl));
        mailSender.send(message);
    }
}
