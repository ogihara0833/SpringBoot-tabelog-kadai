package com.example.nagoyameshi.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;


@Controller
public class HomeController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("categoryList", categoryRepository.findAll());
        model.addAttribute("featuredRestaurants", restaurantRepository.findByIsFeaturedTrueOrderByCreatedAtDesc());
        return "index";
    }
}
