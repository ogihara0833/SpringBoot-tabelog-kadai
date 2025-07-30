package com.example.nagoyameshi.controller;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.UserEditForm;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.UserService;

@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;

    public UserController(UserService userService, UserRepository userRepository) {
        this.userService = userService;
        this.userRepository = userRepository;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());
        model.addAttribute("user", user);
        return "user/index";
    }

    @GetMapping("/edit")
    public String edit(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl, Model model) {
        User user = userRepository.getReferenceById(userDetailsImpl.getUser().getId());

        UserEditForm form = new UserEditForm();
        form.setId(user.getId());
        form.setName(user.getName());
        form.setPhoneNumber(user.getPhoneNumber());
        form.setEmail(user.getEmail());

        model.addAttribute("userEditForm", form);
        return "user/edit";
    }

    @PostMapping("/update")
    public String update(
        @ModelAttribute @Validated UserEditForm userEditForm,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        @AuthenticationPrincipal UserDetailsImpl currentUser 
    ) {
        if (userService.isEmailChanged(userEditForm) && userService.isEmailRegistered(userEditForm.getEmail())) {
            FieldError fieldError = new FieldError(
                bindingResult.getObjectName(),
                "email",
                "すでに登録済みのメールアドレスです。"
            );
            bindingResult.addError(fieldError);
        }

        if (bindingResult.hasErrors()) {
            return "user/edit";
        }

        userService.update(userEditForm);

        User updatedUser = userRepository.findById(userEditForm.getId()).orElseThrow();
        UserDetailsImpl newUserDetails = new UserDetailsImpl(updatedUser);
        UsernamePasswordAuthenticationToken newAuth =
            new UsernamePasswordAuthenticationToken(
                newUserDetails,
                null,
                newUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute("successMessage", "会員情報を編集しました。");
        return "redirect:/user";
    }

}
