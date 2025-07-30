package com.example.nagoyameshi.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.nagoyameshi.entity.Category;
import com.example.nagoyameshi.repository.CategoryRepository;
import com.example.nagoyameshi.repository.RestaurantRepository;

@Controller
@RequestMapping("/admin/categories")
public class AdminCategoryController {

    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private RestaurantRepository restaurantRepository;

    // ✅ 一覧（検索付き）
    @GetMapping
    public String index(@RequestParam(required = false) String keyword,
                        @RequestParam(required = false) String sort,
                        Model model) {

        List<Category> categories;

        if (keyword != null && !keyword.isBlank()) {
            categories = categoryRepository.findByNameContainingIgnoreCaseOrderByIdAsc(keyword);
        } else if ("name".equals(sort)) {
            categories = categoryRepository.findAllByOrderByNameAsc();
        } else {
            categories = categoryRepository.findAllByOrderByIdAsc();
        }

        model.addAttribute("categoryList", categories);
        model.addAttribute("keyword", keyword);
        return "admin/categories/index";
    }


    @GetMapping("/new")
    public String newCategory(Model model) {
        model.addAttribute("category", new Category());
        return "admin/categories/new";
    }

       @PostMapping
    public String create(@ModelAttribute Category category, RedirectAttributes redirectAttributes) {
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            redirectAttributes.addFlashAttribute("errorMessage", "すでに登録されているカテゴリ名です。");
            return "redirect:/admin/categories/new";
        }

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを登録しました！");
        return "redirect:/admin/categories";
    }

    @GetMapping("/{id}/edit")
    public String edit(@PathVariable Integer id, Model model) {
        var category = categoryRepository.findById(id).orElseThrow();
        model.addAttribute("category", category);
        return "admin/categories/edit";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Integer id,
                         @ModelAttribute Category category,
                         RedirectAttributes redirectAttributes) {

        category.setId(id);

        boolean duplicate = categoryRepository.existsByNameIgnoreCase(category.getName()) &&
            !categoryRepository.findById(id).orElseThrow().getName().equalsIgnoreCase(category.getName());

        if (duplicate) {
            redirectAttributes.addFlashAttribute("errorMessage", "すでに登録されているカテゴリ名です。");
            return "redirect:/admin/categories/" + id + "/edit";
        }

        categoryRepository.save(category);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを更新しました！");
        return "redirect:/admin/categories";
    }


    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Integer id, RedirectAttributes redirectAttributes) {

        if (restaurantRepository.existsByCategoryId(id)) {
            redirectAttributes.addFlashAttribute("errorMessage", "このカテゴリは店舗で使用中のため削除できません。");
            return "redirect:/admin/categories";
        }

        categoryRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("successMessage", "カテゴリを削除しました！");
        return "redirect:/admin/categories";
    }
}
