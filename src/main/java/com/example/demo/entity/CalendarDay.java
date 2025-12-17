package com.example.demo.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalendarDay {
    private LocalDate date;
    private int day;
    private boolean otherMonth;
    private boolean today;
    private String fullDate;
    private List<Expense> expenses;
    private Map<Category, BigDecimal> expensesByCategory;
    private int commentCount;
    private String commentsText;
    private String commentsJson;
    private boolean hasVisitLog;
    
    public CalendarDay(LocalDate date, boolean otherMonth) {
        this.date = date;
        this.day = date.getDayOfMonth();
        this.otherMonth = otherMonth;
        this.today = date.isEqual(LocalDate.now());
        this.fullDate = date.toString();
        this.expenses = new ArrayList<>();
        this.expensesByCategory = new HashMap<>();
        this.commentCount = 0;
        this.commentsText = "";
        this.commentsJson = "[]";
        this.hasVisitLog = false;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public int getDay() {
        return day;
    }
    
    public void setDay(int day) {
        this.day = day;
    }
    
    public boolean isOtherMonth() {
        return otherMonth;
    }
    
    public void setOtherMonth(boolean otherMonth) {
        this.otherMonth = otherMonth;
    }
    
    public boolean isToday() {
        return today;
    }
    
    public void setToday(boolean today) {
        this.today = today;
    }
    
    public String getFullDate() {
        return fullDate;
    }
    
    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }
    
    public List<Expense> getExpenses() {
        return expenses;
    }
    
    public void setExpenses(List<Expense> expenses) {
        this.expenses = expenses;
    }
    
    public Map<Category, BigDecimal> getExpensesByCategory() {
        return expensesByCategory;
    }
    
    public void setExpensesByCategory(Map<Category, BigDecimal> expensesByCategory) {
        this.expensesByCategory = expensesByCategory;
    }
    
    public int getCommentCount() {
        return commentCount;
    }
    
    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }
    
    public String getCommentsText() {
        return commentsText;
    }
    
    public void setCommentsText(String commentsText) {
        this.commentsText = commentsText;
    }
    
    public String getCommentsJson() {
        return commentsJson;
    }
    
    public void setCommentsJson(String commentsJson) {
        this.commentsJson = commentsJson;
    }
    
    public boolean isHasVisitLog() {
        return hasVisitLog;
    }
    
    public void setHasVisitLog(boolean hasVisitLog) {
        this.hasVisitLog = hasVisitLog;
    }
}