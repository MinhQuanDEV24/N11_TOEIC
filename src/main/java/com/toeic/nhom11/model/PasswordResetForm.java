package com.toeic.nhom11.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class PasswordResetForm {
    @NotBlank(message = "Liên kết đặt lại mật khẩu không hợp lệ.")
    @Size(max = 128, message = "Liên kết đặt lại mật khẩu không hợp lệ.")
    private String token;

    @NotBlank(message = "Vui lòng nhập mật khẩu mới.")
    @Size(min = 8, max = 64, message = "Mật khẩu phải có từ 8 đến 64 ký tự.")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[\\p{P}\\p{S}])\\S{8,64}$",
            message = "Mật khẩu cần chữ hoa, chữ thường, ký tự đặc biệt và không có khoảng trắng.")
    private String password;

    @NotBlank(message = "Vui lòng xác nhận lại mật khẩu.")
    private String confirmPassword;

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }
}
