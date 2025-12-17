package com.example.demo.service;

import com.example.demo.entity.CalendarDay;
import com.example.demo.entity.Category;
import com.example.demo.entity.Comment;
import com.example.demo.entity.Expense;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CalendarService {

    @Autowired
    private ExpenseService expenseService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private VisitLogService visitLogService;

    /**
     * 生成日历日期数据
     */
    public List<CalendarDay> generateCalendarDays(int year, int month) {
        List<CalendarDay> days = new ArrayList<>();
        YearMonth yearMonth = YearMonth.of(year, month);
        
        // 获取当月第一天
        LocalDate firstDay = yearMonth.atDay(1);
        // 获取当月第一天是星期几
        DayOfWeek firstDayOfWeek = firstDay.getDayOfWeek();
        int startOffset = firstDayOfWeek.getValue(); // Sunday is 7 in some implementations, need to adjust
        if (startOffset == 7) startOffset = 0; // Make Sunday 0 for easier calculation
        
        // 获取当月最后一天
        LocalDate lastDay = yearMonth.atEndOfMonth();
        int daysInMonth = lastDay.getDayOfMonth();
        
        // 计算需要显示的前一个月的天数
        YearMonth previousMonth = yearMonth.minusMonths(1);
        int daysInPreviousMonth = previousMonth.lengthOfMonth();
        
        // 计算需要显示的后一个月的天数
        int daysToShow = 42; // 6 rows * 7 days
        
        // 生成前一个月需要显示的日期
        for (int i = startOffset - 1; i >= 0; i--) {
            LocalDate date = previousMonth.atDay(daysInPreviousMonth - i);
            days.add(new CalendarDay(date, true));
        }
        
        // 生成当月的日期
        for (int i = 1; i <= daysInMonth; i++) {
            LocalDate date = yearMonth.atDay(i);
            days.add(new CalendarDay(date, false));
        }
        
        // 生成后一个月需要显示的日期
        YearMonth nextMonth = yearMonth.plusMonths(1);
        int remainingDays = daysToShow - days.size();
        for (int i = 1; i <= remainingDays; i++) {
            LocalDate date = nextMonth.atDay(i);
            days.add(new CalendarDay(date, true));
        }
        
        return days;
    }

    /**
     * 获取指定月份的日历数据，包括支出、评论和访问记录
     */
    public Map<String, Object> getCalendarData(int year, int month) {
        Map<String, Object> calendarData = new HashMap<>();
        
        // 获取当月的所有支出
        List<Expense> expenses = expenseService.getExpensesByMonth(year, month);
        
        // 创建日历数据
        List<CalendarDay> calendarDays = generateCalendarDays(year, month);
        
        // 按日期分组支出
        Map<LocalDate, List<Expense>> expensesByDate = new HashMap<>();
        for (Expense expense : expenses) {
            LocalDate date = expense.getDate();
            expensesByDate.computeIfAbsent(date, k -> new ArrayList<>()).add(expense);
        }
        
        // 获取当月的所有评论
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.withDayOfMonth(startDate.lengthOfMonth());
        List<Comment> comments = commentService.getCommentsByDateRange(startDate, endDate);
        
        // 按日期分组评论
        Map<LocalDate, Comment> commentsByDate = new HashMap<>();
        for (Comment comment : comments) {
            commentsByDate.put(comment.getDate(), comment);
        }
        
        // 为每个日历日设置支出数据、访问记录状态和评论数据
        for (CalendarDay day : calendarDays) {
            LocalDate date = day.getDate();
            // 设置当天的支出列表
            List<Expense> dayExpenses = expensesByDate.getOrDefault(date, new ArrayList<>());
            day.setExpenses(dayExpenses);
            
            // 计算当天按类别分组的支出总额
            Map<Category, BigDecimal> dayExpensesByCategory = new HashMap<>();
            for (Expense expense : dayExpenses) {
                Category category = expense.getCategory();
                dayExpensesByCategory.put(category, dayExpensesByCategory.getOrDefault(category, BigDecimal.ZERO)
                    .add(expense.getAmount()));
            }
            day.setExpensesByCategory(dayExpensesByCategory);
            
            // 设置当天的访问记录状态
            boolean hasVisitLog = visitLogService.hasVisitLog(date);
            day.setHasVisitLog(hasVisitLog);
            
            // 设置当天的评论数据
            Comment comment = commentsByDate.get(date);
            if (comment != null) {
                day.setCommentCount(1);
                day.setCommentsText(comment.getContent());
                
                // 构建评论JSON数据
                String commentJson = String.format("[{\"id\":\"%s\",\"content\":\"%s\"}]", 
                    comment.getId(), comment.getContent().replace("\"", "\\\""));
                day.setCommentsJson(commentJson);
            } else {
                day.setCommentCount(0);
                day.setCommentsText("");
                day.setCommentsJson("[]");
            }
        }
        
        // 按类别分组当月所有支出并计算总额
        Map<Category, BigDecimal> expensesByCategory = new HashMap<>();
        for (Expense expense : expenses) {
            Category category = expense.getCategory();
            expensesByCategory.put(category, expensesByCategory.getOrDefault(category, BigDecimal.ZERO)
                .add(expense.getAmount()));
        }
        
        // 准备返回数据
        calendarData.put("currentYear", year);
        calendarData.put("currentMonth", month);
        calendarData.put("calendarDays", calendarDays);
        calendarData.put("expenses", expenses);
        calendarData.put("expensesByDate", expensesByDate);
        calendarData.put("expensesByCategory", expensesByCategory);
        
        return calendarData;
    }
}