package com.toeic.nhom11.controller;

import com.toeic.nhom11.model.PasswordResetForm;
import com.toeic.nhom11.model.PasswordResetRequestForm;
import com.toeic.nhom11.service.PasswordResetService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Controller
public class PasswordResetController {
    private static final Logger logger = LoggerFactory.getLogger(PasswordResetController.class);

    private final PasswordResetService service;

    public PasswordResetController(PasswordResetService service) {
        this.service = service;
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {
        model.addAttribute("activePage", "login");
        model.addAttribute("pageTitle", "Quên mật khẩu");
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String requestReset(
        @Valid @ModelAttribute("requestForm") PasswordResetRequestForm form,
        BindingResult result,
        Model model,
        RedirectAttributes redirect
    ) {
        model.addAttribute("activePage", "login");
        model.addAttribute("pageTitle", "Quên mật khẩu");
        if (result.hasErrors()) {
            addFieldErrors(model, result);
            return "auth/forgot-password";
        }

        try {
            service.requestReset(form.getEmail());
        } catch (MailException exception) {
            logger.error("Password reset email delivery failed ({})", exception.getClass().getSimpleName());
        }
        redirect.addFlashAttribute("resetRequestSent", true);
        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String resetPassword(
        @RequestParam(required = false) String token,
        Model model,
        HttpServletResponse response
    ) {
        setNoStoreHeaders(response);
        model.addAttribute("activePage", "login");
        model.addAttribute("pageTitle", "Tạo mật khẩu mới");
        PasswordResetForm form = new PasswordResetForm();
        form.setToken(token);
        model.addAttribute("resetForm", form);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String updatePassword(
        @Valid @ModelAttribute("resetForm") PasswordResetForm form,
        BindingResult result,
        Model model,
        HttpServletResponse response,
        RedirectAttributes redirect
    ) {
        setNoStoreHeaders(response);
        model.addAttribute("activePage", "login");
        model.addAttribute("pageTitle", "Tạo mật khẩu mới");

        if (!Objects.equals(form.getPassword(), form.getConfirmPassword())) {
            result.rejectValue("confirmPassword", "mismatch", "Mật khẩu xác nhận không khớp.");
        }
        if (form.getPassword() != null
            && form.getPassword().getBytes(StandardCharsets.UTF_8).length > 72) {
            result.rejectValue("password", "tooLongBytes", "Mật khẩu vượt quá giới hạn 72 byte của BCrypt.");
        }
        if (result.hasErrors()) {
            addFieldErrors(model, result);
            return "auth/reset-password";
        }

        if (!service.resetPassword(form.getToken(), form.getPassword())) {
            model.addAttribute("formError", "Liên kết không hợp lệ, đã hết hạn hoặc đã được sử dụng. Hãy yêu cầu liên kết mới.");
            return "auth/reset-password";
        }

        redirect.addAttribute("reset", "success");
        return "redirect:/login";
    }

    private void addFieldErrors(Model model, BindingResult result) {
        Map<String, String> errors = new HashMap<>();
        result.getFieldErrors().forEach(error ->
            errors.putIfAbsent(error.getField(), error.getDefaultMessage()));
        model.addAttribute("fieldErrors", errors);
    }

    private void setNoStoreHeaders(HttpServletResponse response) {
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Referrer-Policy", "no-referrer");
    }
}
