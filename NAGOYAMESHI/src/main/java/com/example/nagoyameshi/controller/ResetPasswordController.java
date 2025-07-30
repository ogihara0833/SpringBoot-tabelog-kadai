package com.example.nagoyameshi.controller;

import java.util.Optional;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.service.PasswordResetService;
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/reset-password")
public class ResetPasswordController {

    private final UserService userService;
    private final PasswordResetService passwordResetService;

    @Value("${app.url}") 
    private String appUrl;

    public ResetPasswordController(UserService userService, PasswordResetService passwordResetService) {
        this.userService = userService;
        this.passwordResetService = passwordResetService;
    }

    @GetMapping
    public String showRequestForm() {
        return "auth/resetRequest";
    }

    @PostMapping("/request")
    public String handleRequest(@RequestParam("email") String email,
                                RedirectAttributes redirectAttributes,
                                HttpServletRequest request) {

        Optional<User> optUser = userService.findByEmail(email);

        if (optUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "メールアドレスが見つかりませんでした。");
            return "redirect:/reset-password";
        }

        User user = optUser.get();
        passwordResetService.sendResetLink(user, appUrl);

        redirectAttributes.addFlashAttribute("successMessage", "パスワード再設定用リンクを送信しました。メールをご確認ください。");
        return "redirect:/reset-password";
    }

    @GetMapping("/change")
    public String showChangeForm(@RequestParam("token") String token, Model model) {
        if (!passwordResetService.isTokenValid(token)) {
            model.addAttribute("errorMessage", "無効または期限切れのトークンです。");
            return "auth/resetRequest";
        }

        model.addAttribute("token", token);
        return "auth/resetPassword";
    }

    @PostMapping("/change")
    public String handleChange(@RequestParam("token") String token,
                               @RequestParam("newPassword") String newPassword,
                               RedirectAttributes redirectAttributes) {
    	if (newPassword.length() < 4) {
    	    redirectAttributes.addFlashAttribute("errorMessage", "パスワードは4文字以上で入力してください。");
    	    return "redirect:/reset-password/change?token=" + token;
    	}

        boolean success = passwordResetService.resetPassword(token, newPassword);

        if (!success) {
            redirectAttributes.addFlashAttribute("errorMessage", "パスワード更新に失敗しました。");
            return "redirect:/reset-password/change?token=" + token;
        }

        redirectAttributes.addFlashAttribute("successMessage", "パスワードを更新しました。ログインしてください。");
        return "redirect:/login";
    }
}
