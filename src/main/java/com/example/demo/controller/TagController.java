package com.example.demo.controller;

import com.example.demo.entity.Tag;
import com.example.demo.service.TagService;
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
@RequestMapping("/tags")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @GetMapping
    public String listTags(Model model) {
        List<Tag> tags = tagService.getAllTags();
        model.addAttribute("tags", tags);
        model.addAttribute("pageTitle", "Tag Management");
        return "tags/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("tag", new Tag());
        model.addAttribute("pageTitle", "Create New Tag");
        model.addAttribute("action", "Create");
        return "tags/form";
    }
    
    @PostMapping("/save")
    public String saveTag(@Valid @ModelAttribute Tag tag, 
                           BindingResult result, 
                           Model model,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", tag.getId() != null ? "Edit Tag" : "Create New Tag");
            model.addAttribute("action", tag.getId() != null ? "Update" : "Create");
            return "tags/form";
        }
        
        try {
            if (tag.getId() != null) {
                tagService.updateTag(tag.getId(), tag);
                redirectAttributes.addFlashAttribute("successMessage", "Tag updated successfully!");
            } else {
                tagService.saveTag(tag);
                redirectAttributes.addFlashAttribute("successMessage", "Tag created successfully!");
            }
            return "redirect:/tags";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", tag.getId() != null ? "Edit Tag" : "Create New Tag");
            model.addAttribute("action", tag.getId() != null ? "Update" : "Create");
            return "tags/form";
        }
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Tag> tag = tagService.getTagById(id);
        if (tag.isPresent()) {
            model.addAttribute("tag", tag.get());
            model.addAttribute("pageTitle", "Edit Tag");
            model.addAttribute("action", "Update");
            return "tags/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Tag not found!");
            return "redirect:/tags";
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteTag(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            tagService.deleteTag(id);
            redirectAttributes.addFlashAttribute("successMessage", "Tag deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/tags";
    }

    @GetMapping("/search")
    public String searchTags(@RequestParam String name, Model model) {
        List<Tag> tags = tagService.searchTags(name);
        model.addAttribute("tags", tags);
        model.addAttribute("searchTerm", name);
        model.addAttribute("pageTitle", "Tag Management - Search Results");
        return "tags/list";
    }

    @PostMapping("/clear")
    public String clearAllTags(RedirectAttributes redirectAttributes) {
        try {
            tagService.deleteAllTags();
            redirectAttributes.addFlashAttribute("successMessage", "All tags have been cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to clear tags: " + e.getMessage());
        }
        return "redirect:/tags";
    }
}