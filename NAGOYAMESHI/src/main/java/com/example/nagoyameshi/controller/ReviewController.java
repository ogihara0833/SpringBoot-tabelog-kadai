package com.example.nagoyameshi.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.Review;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.form.ReviewEditForm;
import com.example.nagoyameshi.form.ReviewRegisterForm;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.RestaurantService;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/restaurants/{restaurantId}/reviews")
public class ReviewController {

    private final RestaurantService restaurantService;
    private final ReviewService reviewService;

    public ReviewController(RestaurantService restaurantService, ReviewService reviewService) {
        this.restaurantService = restaurantService;
        this.reviewService = reviewService;
    }

    @GetMapping
    public String index(@PathVariable Integer restaurantId,
                        @PageableDefault(page = 0, size = 10) Pageable pageable,
                        RedirectAttributes redirectAttributes,
                        Model model) {
        Optional<Restaurant> optRestaurant = restaurantService.findById(restaurantId);
        if (optRestaurant.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            return "redirect:/restaurants";
        }

        Restaurant restaurant = optRestaurant.get();
        Page<Review> reviewPage = reviewService.findReviewsByRestaurantOrderByCreatedAtDesc(restaurant, pageable);

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("reviewPage", reviewPage);

        return "reviews/index";
    }

    @GetMapping("/register")
    public String register(@PathVariable Integer restaurantId,
                           @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        Optional<Restaurant> optRestaurant = restaurantService.findById(restaurantId);
        if (optRestaurant.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            return "redirect:/restaurants";
        }

        User user = userDetailsImpl.getUser();
        if (!user.getRole().getName().equals("PREMIUM")) {
            redirectAttributes.addFlashAttribute("errorMessage", "レビュー投稿は有料会員限定です。");
            return "redirect:/restaurants/" + restaurantId;
        }

        model.addAttribute("restaurant", optRestaurant.get());
        model.addAttribute("reviewRegisterForm", new ReviewRegisterForm());

        return "reviews/register";
    }

    @PostMapping("/create")
    public String create(@PathVariable Integer restaurantId,
                         @Validated @ModelAttribute ReviewRegisterForm reviewRegisterForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model) {
        Optional<Restaurant> optRestaurant = restaurantService.findById(restaurantId);
        if (optRestaurant.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が存在しません。");
            return "redirect:/restaurants";
        }

        Restaurant restaurant = optRestaurant.get();
        User user = userDetailsImpl.getUser();

        if (!user.getRole().getName().equals("PREMIUM")) {
            redirectAttributes.addFlashAttribute("errorMessage", "有料会員のみレビューを投稿できます。");
            return "redirect:/restaurants/" + restaurantId;
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("reviewRegisterForm", reviewRegisterForm);
            return "reviews/register";
        }

        reviewService.createReview(reviewRegisterForm, restaurant, user);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを投稿しました。");

        return "redirect:/restaurants/" + restaurantId;
    }

    @GetMapping("/{reviewId}/edit")
    public String edit(@PathVariable Integer restaurantId,
                       @PathVariable Integer reviewId,
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                       RedirectAttributes redirectAttributes,
                       Model model) {

        Optional<Restaurant> optRestaurant = restaurantService.findById(restaurantId);
        Optional<Review> optReview = reviewService.findReviewById(reviewId);

        if (optRestaurant.isEmpty() || optReview.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定されたページが見つかりません。");
            return "redirect:/restaurants";
        }

        Restaurant restaurant = optRestaurant.get();
        Review review = optReview.get();
        User user = userDetailsImpl.getUser();

        if (!user.getRole().getName().equals("PREMIUM") ||
        	    !review.getUser().getId().equals(user.getId()) ||
        	    !review.getRestaurant().getId().equals(restaurant.getId())) {
        	    redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
        	    return "redirect:/restaurants/" + restaurantId;
        	}

        ReviewEditForm reviewEditForm = new ReviewEditForm(review.getScore(), review.getContent());

        model.addAttribute("restaurant", restaurant);
        model.addAttribute("review", review);
        model.addAttribute("reviewEditForm", reviewEditForm);

        return "reviews/edit";
    }

    @PostMapping("/{reviewId}/update")
    public String update(@PathVariable Integer restaurantId,
                         @PathVariable Integer reviewId,
                         @Validated @ModelAttribute ReviewEditForm reviewEditForm,
                         BindingResult bindingResult,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes,
                         Model model) {

        Optional<Restaurant> optRestaurant = restaurantService.findById(restaurantId);
        Optional<Review> optReview = reviewService.findReviewById(reviewId);

        if (optRestaurant.isEmpty() || optReview.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定されたページが見つかりません。");
            return "redirect:/restaurants";
        }

        Restaurant restaurant = optRestaurant.get();
        Review review = optReview.get();
        User user = userDetailsImpl.getUser();

        if (!user.getRole().getName().equals("PREMIUM") ||
        	    !review.getUser().getId().equals(user.getId()) ||
        	    !review.getRestaurant().getId().equals(restaurant.getId())) {
        	    redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
        	    return "redirect:/restaurants/" + restaurantId;
        	}

        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurant", restaurant);
            model.addAttribute("review", review);
            model.addAttribute("reviewEditForm", reviewEditForm);
            return "reviews/edit";
        }

        reviewService.updateReview(reviewEditForm, review);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを更新しました。");

        return "redirect:/restaurants/" + restaurantId;
    }

    @PostMapping("/{reviewId}/delete")
    public String delete(@PathVariable Integer restaurantId,
                         @PathVariable Integer reviewId,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes) {

        Optional<Restaurant> optRestaurant = restaurantService.findById(restaurantId);
        Optional<Review> optReview = reviewService.findReviewById(reviewId);

        if (optRestaurant.isEmpty() || optReview.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "指定されたページが見つかりません。");
            return "redirect:/restaurants";
        }

        Restaurant restaurant = optRestaurant.get();
        Review review = optReview.get();
        User user = userDetailsImpl.getUser();

        if (!user.getRole().getName().equals("PREMIUM") ||
        	    !review.getUser().getId().equals(user.getId()) ||
        	    !review.getRestaurant().getId().equals(restaurant.getId())) {
        	    redirectAttributes.addFlashAttribute("errorMessage", "不正なアクセスです。");
        	    return "redirect:/restaurants/" + restaurantId;
        	}

        reviewService.deleteReview(review);
        redirectAttributes.addFlashAttribute("successMessage", "レビューを削除しました。");

        return "redirect:/restaurants/" + restaurantId;
    }
}
