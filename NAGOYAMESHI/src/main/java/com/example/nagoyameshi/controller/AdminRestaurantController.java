package com.example.nagoyameshi.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Restaurant;
import com.example.nagoyameshi.form.RestaurantEditForm;
import com.example.nagoyameshi.form.RestaurantRegisterForm;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;
import com.example.nagoyameshi.service.RestaurantService;

@Controller
@RequestMapping("/admin/restaurants")
public class AdminRestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    @GetMapping
    public String index(
        @RequestParam(name = "keyword", required = false) String keyword,
        @PageableDefault(size = 10) Pageable pageable,
        Model model
    ) {
        model.addAttribute("restaurantPage", 
            keyword == null || keyword.isBlank()
                ? restaurantService.findAll(pageable)
                : restaurantService.searchByName(keyword, pageable));
        model.addAttribute("keyword", keyword);
        return "admin/restaurants/index";
    }

    @GetMapping("/register")
    public String registerForm(Model model) {
        model.addAttribute("restaurantRegisterForm", new RestaurantRegisterForm());
        model.addAttribute("categoryList", categoryRepository.findAll());
        return "admin/restaurants/register";
    }

    @PostMapping("/register")
    public String registerSubmit(
        @ModelAttribute("restaurantRegisterForm") @Valid RestaurantRegisterForm form,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        if (bindingResult.hasErrors()) {
        	model.addAttribute("restaurantRegisterForm", form);
            model.addAttribute("categoryList", categoryRepository.findAll());
            return "admin/restaurants/register";
        }

        try {
            restaurantService.create(form); 
            redirectAttributes.addFlashAttribute("successMessage", "店舗を登録しました！");
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("errorMessage", "画像の保存に失敗しました。");
        }

        return "redirect:/admin/restaurants";
    }
    
    @GetMapping("/{id}/edit")
    public String edit(@PathVariable("id") Integer id, Model model) { 

        Restaurant restaurant = restaurantRepository.getReferenceById(id);

        RestaurantEditForm form = new RestaurantEditForm(
            restaurant.getId(),
            restaurant.getName(),
            restaurant.getDescription(),
            restaurant.getCategory().getId(),
            restaurant.getLunchStart(),
            restaurant.getLunchEnd(),
            restaurant.getDinnerStart(),
            restaurant.getDinnerEnd(),
            restaurant.getLunchPriceMin(),     
            restaurant.getLunchPriceMax(),     
            restaurant.getDinnerPriceMin(),    
            restaurant.getDinnerPriceMax(),    
            restaurant.getPostalCode(),
            restaurant.getAddress(),
            restaurant.getPhoneNumber(),
            restaurant.getHoliday(),
            null,
            null,
            restaurant.getImageName(),
            restaurant.getMenuImageName(),
            restaurant.getIsFeatured()
        );

        model.addAttribute("restaurantEditForm", form);
        model.addAttribute("categoryList", categoryRepository.findAll());
        
        return "admin/restaurants/edit";
    }

    
    @PostMapping("/{id}/update")
    public String update(
        @PathVariable("id") Integer id,
        @ModelAttribute("restaurantEditForm") @Validated RestaurantEditForm form,
        BindingResult bindingResult,
        RedirectAttributes redirectAttributes,
        Model model
    ) {
        Restaurant existing = restaurantService.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("対象店舗が存在しません"));

        if (form.getMainImageFile() == null || form.getMainImageFile().isEmpty()) {
            form.setImageName(existing.getImageName());
        }
        if (form.getMenuImageFile() == null || form.getMenuImageFile().isEmpty()) {
            form.setMenuImageName(existing.getMenuImageName());
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("restaurantEditForm", form);
            model.addAttribute("categoryList", categoryRepository.findAll());
            return "admin/restaurants/edit";
        }
      
        existing.setIsFeatured(Boolean.TRUE.equals(form.getIsFeatured()));
        
        restaurantService.update(form);
        redirectAttributes.addFlashAttribute("successMessage", "店舗情報を更新しました！");
        return "redirect:/admin/restaurants";
    }

    @GetMapping("/features")
    public String showFeaturedRestaurants(Model model) {
        List<Restaurant> featured = restaurantRepository.findByIsFeaturedTrueOrderByCreatedAtDesc();
        model.addAttribute("featuredRestaurants", featured);
        return "admin/restaurants/features";
    }

    @PostMapping("/{id}/feature-toggle")
    public String toggleFeatured(
        @PathVariable("id") Integer id,
        RedirectAttributes redirectAttributes
    ) {
        Restaurant restaurant = restaurantRepository.getReferenceById(id);
        if (restaurant.getIsFeatured()) {
        	restaurant.setIsFeatured(false);
        } else {
        	restaurant.setIsFeatured(true);
        }
        restaurantRepository.save(restaurant);

        redirectAttributes.addFlashAttribute("successMessage", 
        		restaurant.getIsFeatured() ? "店舗を注目に設定しました！" : "店舗の注目を解除しました！");
        
        return "redirect:/admin/restaurants/" + id + "/edit";
    }

    
}
