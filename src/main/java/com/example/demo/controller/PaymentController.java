package com.example.demo.controller;

import com.example.demo.entity.Payment;
import com.example.demo.service.PaymentService;
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
@RequestMapping("/payments")
public class PaymentController {
    
    @Autowired
    private PaymentService paymentService;
    
    @GetMapping
    public String listPayments(Model model) {
        List<Payment> payments = paymentService.getAllPayments();
        model.addAttribute("payments", payments);
        model.addAttribute("pageTitle", "Payment Method Management");
        return "payments/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("payment", new Payment());
        model.addAttribute("pageTitle", "Create New Payment Method");
        model.addAttribute("action", "Create");
        return "payments/form";
    }
    
    @PostMapping("/save")
    public String savePayment(@Valid @ModelAttribute Payment payment, 
                               BindingResult result, 
                               Model model,
                               RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("pageTitle", payment.getId() != null ? "Edit Payment Method" : "Create New Payment Method");
            model.addAttribute("action", payment.getId() != null ? "Update" : "Create");
            return "payments/form";
        }
        
        try {
            if (payment.getId() != null) {
                paymentService.updatePayment(payment.getId(), payment);
                redirectAttributes.addFlashAttribute("successMessage", "Payment method updated successfully!");
            } else {
                paymentService.savePayment(payment);
                redirectAttributes.addFlashAttribute("successMessage", "Payment method created successfully!");
            }
            return "redirect:/payments";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", payment.getId() != null ? "Edit Payment Method" : "Create New Payment Method");
            model.addAttribute("action", payment.getId() != null ? "Update" : "Create");
            return "payments/form";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Payment> payment = paymentService.getPaymentById(id);
        if (payment.isPresent()) {
            model.addAttribute("payment", payment.get());
            model.addAttribute("pageTitle", "Edit Payment Method");
            model.addAttribute("action", "Update");
            return "payments/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Payment method not found!");
            return "redirect:/payments";
        }
    }

    
    @GetMapping("/delete/{id}")
    public String deletePayment(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            paymentService.deletePayment(id);
            redirectAttributes.addFlashAttribute("successMessage", "Payment method deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/payments";
    }
    
    @GetMapping("/search")
    public String searchPayments(@RequestParam String name, Model model) {
        List<Payment> payments = paymentService.searchPayments(name);
        model.addAttribute("payments", payments);
        model.addAttribute("searchTerm", name);
        model.addAttribute("pageTitle", "Payment Method Management - Search Results");
        return "payments/list";
    }
}