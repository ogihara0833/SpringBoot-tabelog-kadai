package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.constant.MembershipType;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.entity.VerificationToken;
import com.example.nagoyameshi.event.SignupEventPublisher;
import com.example.nagoyameshi.form.SignupForm;
import com.example.nagoyameshi.service.UserService;
import com.example.nagoyameshi.service.VerificationTokenService;

@Controller
public class AuthController {

    private final UserService userService;
    private final SignupEventPublisher signupEventPublisher;
    private final VerificationTokenService verificationTokenService;

    public AuthController(
        UserService userService,
        SignupEventPublisher signupEventPublisher,
        VerificationTokenService verificationTokenService
    ) {
        this.userService = userService;
        this.signupEventPublisher = signupEventPublisher;
        this.verificationTokenService = verificationTokenService;
    }


    // ログインページ
    @GetMapping("/login")
    public String login() {
        return "auth/login";
    }


    // 会員登録フォーム表示
    @GetMapping("/signup")
    public String signup(Model model) {
        SignupForm signupForm = new SignupForm();
        signupForm.setMembershipType(MembershipType.FREE);
        model.addAttribute("signupForm", signupForm);
        return "auth/signup";
    }

    // 会員登録処理（メール送信あり）
    @PostMapping("/signup")
    public String signup(
        @ModelAttribute @Validated SignupForm signupForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        HttpServletRequest request
    ) {
        
        if (userService.isEmailRegistered(signupForm.getEmail())) {
            bindingResult.addError(new FieldError("signupForm", "email", "すでに登録済みのメールアドレスです。"));
        }

        if (!userService.isSamePassword(signupForm.getPassword(), signupForm.getPasswordConfirmation())) {
            bindingResult.addError(new FieldError("signupForm", "password", "パスワードが一致しません。"));
        }

        if (bindingResult.hasErrors()) {
            return "auth/signup";
        }

        User createdUser = userService.create(signupForm);

        String requestUrl = request.getRequestURL().toString().replace(request.getRequestURI(), "");

        signupEventPublisher.publishSignupEvent(createdUser, requestUrl);

        redirectAttributes.addFlashAttribute("successMessage", 
            "ご入力いただいたメールアドレスに認証メールを送信しました。メールに記載されているリンクをクリックし、会員登録を完了してください。");

        return "redirect:/";
    }


    @GetMapping("/signup/verify")
    public String verify(@RequestParam(name = "token") String token, Model model) {
        VerificationToken verificationToken = verificationTokenService.getByToken(token);

        if (verificationToken != null) {
            User user = verificationToken.getUser();
            userService.enable(user);
            model.addAttribute("successMessage", "会員登録が完了しました。");
        } else {
            model.addAttribute("errorMessage", "トークンが無効です。");
        }

        return "auth/verify";
    }
}
