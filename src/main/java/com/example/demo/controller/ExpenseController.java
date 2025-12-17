package com.example.demo.controller;

import com.example.demo.entity.Expense;
import com.example.demo.entity.ExpenseFilterParams;
import com.example.demo.service.CategoryService;
import com.example.demo.service.ChannelService;
import com.example.demo.service.ExpenseService;
import com.example.demo.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("/expenses")
public class ExpenseController {
    
    @Autowired
    private ExpenseService expenseService;
    
    @Autowired
    private CategoryService categoryService;
    
    @Autowired
    private ChannelService channelService;
    
    @Autowired
    private PaymentService paymentService;
    

    
    @GetMapping
    public String listExpenses(Model model) {
        List<Expense> expenses = expenseService.getAllExpenses();
        model.addAttribute("expenses", expenses);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategory", null);
        model.addAttribute("pageTitle", "Expense Management");
        return "expenses/list";
    }
    
    @GetMapping("/filter")
    public String filterExpenses(@ModelAttribute ExpenseFilterParams filterParams, Model model) {
        try {
            // 参数验证和转换已经由Spring自动完成
        } catch (Exception e) {
            model.addAttribute("errorMessage", "Invalid filter parameters");
            model.addAttribute("expenses", expenseService.getAllExpenses());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("selectedCategory", null);
            return "expenses/list";
        }
        
        // 调用过滤服务
        List<Expense> filteredExpenses = expenseService.getFilteredExpenses(filterParams);
        
        // 添加模型属性
        model.addAttribute("expenses", filteredExpenses);
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("selectedCategory", filterParams.getCategoryId());
        model.addAttribute("startDate", filterParams.getStartDate() != null ? filterParams.getStartDate().toString() : "");
        model.addAttribute("endDate", filterParams.getEndDate() != null ? filterParams.getEndDate().toString() : "");
        model.addAttribute("minAmount", filterParams.getMinAmount() != null ? filterParams.getMinAmount().toString() : "");
        model.addAttribute("maxAmount", filterParams.getMaxAmount() != null ? filterParams.getMaxAmount().toString() : "");
        model.addAttribute("keyword", filterParams.getKeyword());
        model.addAttribute("sortBy", filterParams.getSortBy());
        model.addAttribute("sortDescending", filterParams.isSortDescending());
        model.addAttribute("pageTitle", "Expense Management - Filtered Results");
        
        return "expenses/list";
    }
    
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("expense", new Expense());
        model.addAttribute("categories", categoryService.getAllCategories());
        model.addAttribute("channels", channelService.getAllChannels());
        model.addAttribute("payments", paymentService.getAllPayments());
        model.addAttribute("pageTitle", "Create New Expense");
        model.addAttribute("action", "Create");
        return "expenses/form";
    }
    
    @PostMapping("/save")
    public String saveExpense(@Valid @ModelAttribute Expense expense, 
                              BindingResult result, 
                              Model model,
                              RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("channels", channelService.getAllChannels());
            model.addAttribute("payments", paymentService.getAllPayments());
            model.addAttribute("pageTitle", expense.getId() != null ? "Edit Expense" : "Create New Expense");
            model.addAttribute("action", expense.getId() != null ? "Update" : "Create");
            return "expenses/form";
        }
        
        try {
            if (expense.getId() != null) {
                expenseService.updateExpense(expense.getId(), expense);
                redirectAttributes.addFlashAttribute("successMessage", "Expense updated successfully!");
            } else {
                expenseService.saveExpense(expense);
                redirectAttributes.addFlashAttribute("successMessage", "Expense created successfully!");
            }
            return "redirect:/expenses";
        } catch (RuntimeException e) {
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("errorMessage", e.getMessage());
            model.addAttribute("pageTitle", expense.getId() != null ? "Edit Expense" : "Create New Expense");
            model.addAttribute("action", expense.getId() != null ? "Update" : "Create");
            return "expenses/form";
        }
    }
    
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model, RedirectAttributes redirectAttributes) {
        Optional<Expense> expense = expenseService.getExpenseById(id);
        if (expense.isPresent()) {
            model.addAttribute("expense", expense.get());
            model.addAttribute("categories", categoryService.getAllCategories());
            model.addAttribute("channels", channelService.getAllChannels());
            model.addAttribute("payments", paymentService.getAllPayments());
            model.addAttribute("pageTitle", "Edit Expense");
            model.addAttribute("action", "Update");
            return "expenses/form";
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Expense not found!");
            return "redirect:/expenses";
        }
    }

    
    @GetMapping("/delete/{id}")
    public String deleteExpense(@PathVariable String id, RedirectAttributes redirectAttributes) {
        try {
            expenseService.deleteExpense(id);
            redirectAttributes.addFlashAttribute("successMessage", "Expense deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/expenses";
    }
    
    // 切换支出确认状态
    @PutMapping("/toggle-confirmation/{id}")
    @ResponseBody
    public Map<String, Object> toggleExpenseConfirmation(@PathVariable String id) {
        Optional<Expense> optionalExpense = expenseService.getExpenseById(id);
        if (optionalExpense.isPresent()) {
            Expense expense = optionalExpense.get();
            expense.setConfirmed(!expense.getConfirmed());
            expenseService.updateExpense(id, expense);
            
            Map<String, Object> response = new HashMap<>();
            response.put("status", "success");
            response.put("confirmed", expense.getConfirmed());
            return response;
        } else {
            Map<String, Object> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Expense not found");
            return response;
        }
    }
    
    @GetMapping("/search")
    public String searchExpenses(@RequestParam String title, Model model) {
        List<Expense> expenses = expenseService.searchExpenses(title);
        model.addAttribute("expenses", expenses);
        model.addAttribute("searchTerm", title);
        model.addAttribute("pageTitle", "Expense Management - Search Results");
        return "expenses/list";
    }
    

}
