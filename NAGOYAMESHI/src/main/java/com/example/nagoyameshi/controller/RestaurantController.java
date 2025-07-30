package com.example.nagoyameshi.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.ReviewService;

@Controller
@RequestMapping("/restaurants")
public class RestaurantController {

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private ReviewService reviewService;

    @GetMapping
    public String index(
        @RequestParam(name = "keyword", required = false) String keyword,
        @RequestParam(name = "categoryId", required = false) Integer categoryId,
        @RequestParam(name = "maxBudget", required = false) Integer maxBudget,
        @RequestParam(name = "area", required = false) String area,
        @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.ASC) Pageable pageable,
        Model model
    ) {
        Page<Restaurant> restaurantPage = restaurantRepository.searchAll(keyword, categoryId, maxBudget, area, pageable);

        model.addAttribute("restaurantPage", restaurantPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("maxBudget", maxBudget);
        model.addAttribute("area", area);
        model.addAttribute("categoryList", categoryRepository.findAll());

        return "restaurants/index";
    }

    @GetMapping("/{id}")
    public String show(@PathVariable("id") Integer id,
                       @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                       Model model) {

        Restaurant restaurant = restaurantRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("存在しない店舗ID: " + id));
        model.addAttribute("restaurant", restaurant);

        User user = null;
        boolean hasUserAlreadyReviewed = false;

        if (userDetailsImpl != null) {
            user = userDetailsImpl.getUser();
            model.addAttribute("user", user);

            Optional<Favorite> favoriteOpt = favoriteService.findFavoriteByUserAndRestaurant(user, restaurant);
            model.addAttribute("isFavorite", favoriteOpt.isPresent());
            model.addAttribute("favorite", favoriteOpt.orElse(null));

            hasUserAlreadyReviewed = reviewService.hasUserAlreadyReviewed(restaurant, user);
            model.addAttribute("hasUserAlreadyReviewed", hasUserAlreadyReviewed);
        }

        List<com.example.nagoyameshi.entity.Review> newReviews =
            reviewService.findTop6ReviewsByRestaurantOrderByCreatedAtDesc(restaurant);

        long totalReviewCount = reviewService.countReviewsByRestaurant(restaurant);

        model.addAttribute("newReviews", newReviews);
        model.addAttribute("totalReviewCount", totalReviewCount);

        return "restaurants/show";
    }
}
