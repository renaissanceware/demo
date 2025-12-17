package com.example.demo.controller;

import com.example.demo.service.CalendarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Map;

@Controller
@RequestMapping("/calendar")
public class CalendarController {

    @Autowired
    private CalendarService calendarService;

    @GetMapping
    public String showCalendar(@RequestParam(required = false) Integer year,
                               @RequestParam(required = false) Integer month,
                               Model model) {
        // 设置当前年月，如果没有提供则使用当前日期
        LocalDate currentDate = LocalDate.now();
        int currentYear = year != null ? year : currentDate.getYear();
        int currentMonth = month != null ? month : currentDate.getMonthValue();
        
        // 获取日历数据
        Map<String, Object> calendarData = calendarService.getCalendarData(currentYear, currentMonth);
        
        // 添加模型属性
        model.addAttribute("currentYear", calendarData.get("currentYear"));
        model.addAttribute("currentMonth", calendarData.get("currentMonth"));
        model.addAttribute("calendarDays", calendarData.get("calendarDays"));
        model.addAttribute("expenses", calendarData.get("expenses"));
        model.addAttribute("expensesByDate", calendarData.get("expensesByDate"));
        model.addAttribute("expensesByCategory", calendarData.get("expensesByCategory"));
        model.addAttribute("pageTitle", "Expense Calendar");
        
        return "calendar/list";
    }
}