package com.example.demo.controller;

import com.example.demo.entity.Category;
import com.example.demo.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/categories")
public class CategoryController {
    
    @Autowired
    private CategoryService categoryService;
    
    @GetMapping
    public String listCategories(Model model) {
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("pageTitle", "Category Management");
        return "categories/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new Category());
        model.addAttribute("pageTitle", "Create New Category");
        model.addAttribute("action", "Create");
        return "categories/form";
    }
    
    @PostMapping("/save")
    public String saveCategory(@Valid @ModelAttribute Category category, 
                               BindingResult result, 
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", category.getId() != null ? "Edit Category" : "Create New Category");
            model.addAttribute("action", category.getId() != null ? "Update" : "Create");
            return "categories/form";
        }
        
        try {
            if (category.getId() != null) {
                categoryService.updateCategory(category.getId(), category);
                redirectAttributes.addFlashAttribute("successMessage", "Category updated successfully!");
            } else {
                categoryService.saveCategory(category);
                redirectAttributes.addFlashAttribute("successMessage", "Category created successfully!");
            }
            return "redirect:/categories";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", category.getId() != null ? "Edit Category" : "Create New Category");
            model.addAttribute("action", category.getId() != null ? "Update" : "Create");
            return "categories/form";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Category> category = categoryService.getCategoryById(id);
        if (category.isPresent()) {
            model.addAttribute("category", category.get());
            model.addAttribute("pageTitle", "Edit Category");
            model.addAttribute("action", "Update");
            return "categories/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Category not found!");
            return "redirect:/categories";
        }
    }

    
    @GetMapping("/delete/{id}")
    public String deleteCategory(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteCategory(id);
            redirectAttributes.addFlashAttribute("successMessage", "Category deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/categories";
    }
    
    @GetMapping("/search")
    public String searchCategories(@RequestParam String name, Model model) {
        List<Category> categories = categoryService.searchCategories(name);
        model.addAttribute("categories", categories);
        model.addAttribute("searchTerm", name);
        model.addAttribute("pageTitle", "Category Management - Search Results");
        return "categories/list";
    }
    
    @PostMapping("/clear")
    public String clearAllCategories(RedirectAttributes redirectAttributes) {
        try {
            categoryService.deleteAllCategories();
            redirectAttributes.addFlashAttribute("successMessage", "All categories have been cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to clear categories: " + e.getMessage());
        }
        return "redirect:/categories";
    }
}