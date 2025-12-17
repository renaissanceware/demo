package com.example.demo.controller;

import com.example.demo.entity.Channel;
import com.example.demo.service.ChannelService;
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
@RequestMapping("/channels")
public class ChannelController {
    
    @Autowired
    private ChannelService channelService;
    
    @GetMapping
    public String listChannels(Model model) {
        List<Channel> channels = channelService.getAllChannels();
        model.addAttribute("channels", channels);
        model.addAttribute("pageTitle", "Channel Management");
        return "channels/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("channel", new Channel());
        model.addAttribute("pageTitle", "Create New Channel");
        model.addAttribute("action", "Create");
        return "channels/form";
    }
    
    @PostMapping("/save")
    public String saveChannel(@Valid @ModelAttribute Channel channel, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", channel.getId() != null ? "Edit Channel" : "Create New Channel");
            model.addAttribute("action", channel.getId() != null ? "Update" : "Create");
            return "channels/form";
        }
        
        try {
            if (channel.getId() != null) {
                channelService.updateChannel(channel.getId(), channel);
                redirectAttributes.addFlashAttribute("successMessage", "Channel updated successfully!");
            } else {
                channelService.saveChannel(channel);
                redirectAttributes.addFlashAttribute("successMessage", "Channel created successfully!");
            }
            return "redirect:/channels";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", channel.getId() != null ? "Edit Channel" : "Create New Channel");
            model.addAttribute("action", channel.getId() != null ? "Update" : "Create");
            return "channels/form";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Channel> channel = channelService.getChannelById(id);
        if (channel.isPresent()) {
            model.addAttribute("channel", channel.get());
            model.addAttribute("pageTitle", "Edit Channel");
            model.addAttribute("action", "Update");
            return "channels/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Channel not found!");
            return "redirect:/channels";
        }
    }

    
    @GetMapping("/delete/{id}")
    public String deleteChannel(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            channelService.deleteChannel(id);
            redirectAttributes.addFlashAttribute("successMessage", "Channel deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/channels";
    }
    
    @PostMapping("/clear")
    public String clearAllChannels(RedirectAttributes redirectAttributes) {
        try {
            channelService.deleteAllChannels();
            redirectAttributes.addFlashAttribute("successMessage", "All channels have been cleared successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Failed to clear channels: " + e.getMessage());
        }
        return "redirect:/channels";
    }
}