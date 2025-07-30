package com.example.nagoyameshi.controller;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.UserRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.MembershipService;
import com.example.nagoyameshi.service.MembershipStripeService; // ⭐️ Stripe連携用のサービス

@Controller
@RequestMapping("/membership")
public class MembershipController {

    private final MembershipService membershipService;
    private final MembershipStripeService stripeService; 
    private final UserRepository userRepository; 
    
    public MembershipController(MembershipService membershipService,
            MembershipStripeService stripeService,
            UserRepository userRepository) { 
    	this.membershipService = membershipService;
    	this.stripeService = stripeService;
    	this.userRepository = userRepository;
}

    @GetMapping("/upgrade")
    public String showUpgradePage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        if (!userDetails.getUser().getRole().getName().equals("FREE")) {
            return "redirect:/"; 
        }
        model.addAttribute("user", userDetails.getUser());
        return "membership/upgrade";
    }

    @PostMapping("/upgrade")
    public String upgradeToPremium(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   HttpServletRequest request,
                                   Model model) {
        User user = userDetails.getUser();
        String sessionId = stripeService.createMembershipSession(user, request); 
        model.addAttribute("sessionId", sessionId);
        return "membership/checkout";
    }
    
    @GetMapping("/complete")
    public String showCompletePage(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   HttpServletRequest request) {
        String sessionId = request.getParameter("session_id");

        String subscriptionId = stripeService.retrieveSubscriptionId(sessionId);

        User dbUser = userRepository.findById(userDetails.getUser().getId()).orElseThrow();

        membershipService.upgradeToPremium(dbUser, subscriptionId);

        User refreshedUser = userRepository.findById(dbUser.getId()).orElseThrow();

        UserDetailsImpl newUserDetails = new UserDetailsImpl(refreshedUser);
        SecurityContextHolder.getContext().setAuthentication(
            new UsernamePasswordAuthenticationToken(newUserDetails, null, newUserDetails.getAuthorities())
        );

        return "membership/complete";
    }

    
    @GetMapping("/cancel")
    public String showCancelPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "membership/cancel";
    }

    @PostMapping("/cancel")
    public String cancelMembership(@AuthenticationPrincipal UserDetailsImpl userDetails,
                                   RedirectAttributes redirectAttributes) {
        User updatedUser = userDetails.getUser();

        String subscriptionId = updatedUser.getStripeSubscriptionId();
        if (subscriptionId != null && !subscriptionId.isEmpty()) {
            stripeService.cancelSubscription(subscriptionId);
        }

        membershipService.downgradeToFree(updatedUser);

        UserDetailsImpl newUserDetails = new UserDetailsImpl(updatedUser);
        UsernamePasswordAuthenticationToken newAuth =
            new UsernamePasswordAuthenticationToken(newUserDetails, null, newUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(newAuth);

        redirectAttributes.addFlashAttribute("successMessage", "退会手続きが完了しました。");

        return "redirect:/user?canceled";
    }

    @GetMapping("/portal")
    public String showPortalPage(@AuthenticationPrincipal UserDetailsImpl userDetails, Model model) {
        model.addAttribute("user", userDetails.getUser());
        return "membership/portal"; 
    }
}
