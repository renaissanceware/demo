package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/users")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserService userService;

    @GetMapping
    public String listUsers(Model model) {
        logger.info("List all users");
        try {
            List<User> users = userService.getAllUsers();
            model.addAttribute("users", users);
            model.addAttribute("pageTitle", "User Management");
            logger.info("Successfully retrieved users");
            return "users/list";
        } catch (Exception e) {
            logger.error("Error retrieving users: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("pageTitle", "Create New User");
        model.addAttribute("action", "Create");
        return "users/form";
    }

    @PostMapping("/save")
    public String saveUser(@Valid @ModelAttribute User user, 
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", user.getId() != null ? "Edit User" : "Create New User");
            model.addAttribute("action", user.getId() != null ? "Update" : "Create");
            return "users/form";
        }

        try {
            if (user.getId() != null) {
                userService.updateUser(user.getId(), user);
                redirectAttributes.addFlashAttribute("successMessage", "User updated successfully!");
            } else {
                userService.saveUser(user);
                redirectAttributes.addFlashAttribute("successMessage", "User created successfully!");
            }
            return "redirect:/users";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", user.getId() != null ? "Edit User" : "Create New User");
            model.addAttribute("action", user.getId() != null ? "Update" : "Create");
            return "users/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        logger.info("Attempting to edit user with id: {}", id);
        try {
            Optional<User> userOptional = userService.getUserById(id);
            logger.info("UserService.getUserById returned Optional: {}", userOptional);
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                logger.info("Retrieved user: {} with class: {}", user, user.getClass());
                model.addAttribute("user", user);
                model.addAttribute("pageTitle", "Edit User");
                model.addAttribute("action", "Update");
                return "users/form";
            } else {
                logger.warn("User not found with id: {}", id);
                redirectAttributes.addFlashAttribute("errorMessage", "User not found!");
                return "redirect:/users";
            }
        } catch (Exception e) {
            logger.error("Error in showEditForm: {} - {}", e.getClass().getName(), e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteUser(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            userService.deleteUser(id);
            redirectAttributes.addFlashAttribute("successMessage", "User deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/users";
    }

    @PostMapping("/clear")
    public String clearAllUsers(RedirectAttributes redirectAttributes) {
        try {
            userService.deleteAllUsers();
            redirectAttributes.addFlashAttribute("successMessage", "All users have been cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to clear users: " + e.getMessage());
        }
        return "redirect:/users";
    }
}