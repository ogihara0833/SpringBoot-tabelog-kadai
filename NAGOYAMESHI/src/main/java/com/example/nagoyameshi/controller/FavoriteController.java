package com.example.nagoyameshi.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Favorite;
import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.entity.User;
import com.example.nagoyameshi.security.UserDetailsImpl;
import com.example.nagoyameshi.service.FavoriteService;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/favorites")
public class FavoriteController {

    private final RestaurantService restaurantService;
    private final FavoriteService favoriteService;

    public FavoriteController(RestaurantService restaurantService, FavoriteService favoriteService) {
        this.restaurantService = restaurantService;
        this.favoriteService = favoriteService;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                        @PageableDefault(size = 10) Pageable pageable,
                        Model model) {
        User user = userDetailsImpl.getUser();
        Page<Favorite> favorites = favoriteService.findFavoritesByUser(user, pageable);
        model.addAttribute("favoritePage", favorites);
        return "favorites/index";
    }

    @PostMapping("/create/{restaurantId}")
    public String create(@PathVariable Integer restaurantId,
                         @AuthenticationPrincipal UserDetailsImpl userDetailsImpl,
                         RedirectAttributes redirectAttributes) {

        User user = userDetailsImpl.getUser();
        Optional<Restaurant> restaurantOpt = restaurantService.findById(restaurantId);
        if (restaurantOpt.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "店舗が見つかりません。");
            return "redirect:/";
        }

        Restaurant restaurant = restaurantOpt.get();

        if (favoriteService.isFavorite(user, restaurant)) {
            redirectAttributes.addFlashAttribute("infoMessage", "すでにお気に入り登録済みです。");
        } else {
            favoriteService.createFavorite(user, restaurant);
            redirectAttributes.addFlashAttribute("successMessage", "お気に入りに追加しました。");
        }

        return "redirect:/restaurants/" + restaurantId;
    }

    @PostMapping("/ajax/delete/{restaurantId}/{favoriteId}")
    @ResponseBody
    public ResponseEntity<String> ajaxDelete(@PathVariable Integer restaurantId,
                                             @PathVariable Integer favoriteId,
                                             @AuthenticationPrincipal UserDetailsImpl userDetailsImpl) {

        Optional<Favorite> favoriteOpt = favoriteService.findFavoriteById(favoriteId);
        if (favoriteOpt.isEmpty()) {
            return ResponseEntity.status(404).body("NOT_FOUND");
        }

        Favorite favorite = favoriteOpt.get();
        User user = userDetailsImpl.getUser();

        if (!favorite.getUser().getId().equals(user.getId()) ||
            !favorite.getRestaurant().getId().equals(restaurantId)) {
            return ResponseEntity.status(403).body("UNAUTHORIZED");
        }

        favoriteService.deleteFavorite(favorite);
        return ResponseEntity.ok("OK");
    }


}
