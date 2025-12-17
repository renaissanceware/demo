package com.example.demo.controller;

import com.example.demo.entity.Record;
import com.example.demo.service.RecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Controller
public class HomeController {
    
    @Autowired
    private RecordService recordService;
    
    @GetMapping("/")
    public String home(Model model) {
        // 设置默认Locale为中文（中国）
        Locale.setDefault(Locale.SIMPLIFIED_CHINESE);
        
        // 获取所有记录
        List<Record> records = recordService.getAllRecords();
        
        // 获取统计数据
        BigDecimal totalIncome = recordService.getTotalIncome();
        BigDecimal totalExpense = recordService.getTotalExpense();
        BigDecimal balance = recordService.getBalance();
        
        // 添加到模型
        model.addAttribute("records", records);
        model.addAttribute("totalIncome", totalIncome);
        model.addAttribute("totalExpense", totalExpense);
        model.addAttribute("balance", balance);
        model.addAttribute("newRecord", new Record());
        
        return "index";
    }
    

    
    @GetMapping("/record/delete/{id}")
    public String deleteRecord(@PathVariable String id) {
        recordService.deleteRecord(id);
        return "redirect:/";
    }
    
    @GetMapping("/record/edit/{id}")
    @ResponseBody
    public Record getRecordForEdit(@PathVariable String id) {
        return recordService.getRecordById(id).orElse(null);
    }
    
    @PostMapping("/record/add")
    public String addRecord(@ModelAttribute Record record) {
        recordService.addRecord(record);
        return "redirect:/";
    }
    
    @PostMapping("/record/update")
    public String updateRecord(@ModelAttribute Record record) {
        recordService.updateRecord(record.getId(), record);
        return "redirect:/";
    }
    
    @PostMapping("/update-record")
    @ResponseBody
    public String updateRecordField(@RequestParam String id, 
                                   @RequestParam String field, 
                                   @RequestParam String value) {
        System.out.println("Received update-record request: id=" + id + ", field=" + field + ", value=" + value);
        try {
            recordService.updateRecordField(id, field, value);
            System.out.println("Update successful for id=" + id);
            return "success";
        } catch (Exception e) {
            System.out.println("Update failed for id=" + id + ", error: " + e.getMessage());
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }
    
    @PostMapping("/delete-records")
    @ResponseBody
    public String deleteRecords(@RequestParam String ids, 
                               @RequestParam String type) {
        System.out.println("Received delete-records request: ids=" + ids + ", type=" + type);
        try {
            String[] idArray = ids.split(",");
            recordService.deleteRecords(idArray);
            System.out.println("Delete successful for ids=" + ids);
            return "success";
        } catch (Exception e) {
            System.out.println("Delete failed for ids=" + ids + ", error: " + e.getMessage());
            e.printStackTrace();
            return "error: " + e.getMessage();
        }
    }
    
    @GetMapping("/stats")
    @ResponseBody
    public StatsDTO getStats() {
        StatsDTO stats = new StatsDTO();
        stats.setTotalIncome(recordService.getTotalIncome());
        stats.setTotalExpense(recordService.getTotalExpense());
        stats.setBalance(recordService.getBalance());
        return stats;
    }
    
    // 内部类用于统计数据传输
    private static class StatsDTO {
        private BigDecimal totalIncome;
        private BigDecimal totalExpense;
        private BigDecimal balance;
        
        // Getters and setters
        public BigDecimal getTotalIncome() {
            return totalIncome;
        }
        
        public void setTotalIncome(BigDecimal totalIncome) {
            this.totalIncome = totalIncome;
        }
        
        public BigDecimal getTotalExpense() {
            return totalExpense;
        }
        
        public void setTotalExpense(BigDecimal totalExpense) {
            this.totalExpense = totalExpense;
        }
        
        public BigDecimal getBalance() {
            return balance;
        }
        
        public void setBalance(BigDecimal balance) {
            this.balance = balance;
        }
    }
}