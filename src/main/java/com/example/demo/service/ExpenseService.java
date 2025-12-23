package com.example.demo.service;

import com.example.demo.entity.Expense;
import com.example.demo.entity.ExpenseFilterParams;
import com.example.demo.repository.ExpenseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class ExpenseService {
    
    @Autowired
    private ExpenseRepository expenseRepository;
    
    public List<Expense> getAllExpenses() {
        return expenseRepository.findAllByOrderByDateAscSortAsc();
    }
    
    public Optional<Expense> getExpenseById(String id) {
        return expenseRepository.findById(id);
    }
    
    public Expense saveExpense(@Valid Expense expense) {
        return expenseRepository.save(expense);
    }
    
    public Expense updateExpense(String id, @Valid Expense expenseDetails) {
        return expenseRepository.findById(id)
            .map(expense -> {
                expense.setTitle(expenseDetails.getTitle());
                expense.setAmount(expenseDetails.getAmount());
                expense.setCategory(expenseDetails.getCategory());
                expense.setDate(expenseDetails.getDate());
                expense.setNote(expenseDetails.getNote());
                expense.setSort(expenseDetails.getSort());
                expense.setChannel(expenseDetails.getChannel());
                expense.setPayment(expenseDetails.getPayment());
                expense.setConfirmed(expenseDetails.getConfirmed());
                return expenseRepository.save(expense);
            })
            .orElseThrow(() -> new RuntimeException("Expense not found with id: " + id));
    }
    
    public void deleteExpense(String id) {
        if (!expenseRepository.existsById(id)) {
            throw new RuntimeException("Expense not found with id: " + id);
        }
        expenseRepository.deleteById(id);
    }
    
    public List<Expense> searchExpenses(String title) {
        return expenseRepository.findAll(
            (root, query, cb) -> cb.like(root.get("title"), "%" + title + "%"),
            Sort.by(Sort.Direction.ASC, "date", "sort")
        );
    }
    
    public boolean expenseExists(String id) {
        return expenseRepository.existsById(id);
    }
    
    public List<Expense> getExpensesByMonth(int year, int month) {
        LocalDate startDate = YearMonth.of(year, month).atDay(1);
        LocalDate endDate = YearMonth.of(year, month).atEndOfMonth();
        return expenseRepository.findAll(
            (root, query, cb) -> cb.between(root.get("date"), startDate, endDate),
            Sort.by(Sort.Direction.ASC, "date", "sort")
        );
    }
    
    // Old filter method kept for backward compatibility
    public List<Expense> filterExpenses(String categoryId, LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount) {
        ExpenseFilterParams filterParams = new ExpenseFilterParams();
        filterParams.setCategoryId(categoryId);
        filterParams.setStartDate(startDate);
        filterParams.setEndDate(endDate);
        filterParams.setMinAmount(minAmount);
        filterParams.setMaxAmount(maxAmount);
        
        return getFilteredExpenses(filterParams);
    }
    
    // New flexible filter method using Specifications
    public List<Expense> getFilteredExpenses(ExpenseFilterParams filterParams) {
        Specification<Expense> spec = Specification.where(null);
        
        // Apply filters
        spec = applyFilters(spec, filterParams);
        
        // Apply sorting: always first by date ascending, then by sort ascending
        // User-specified sorting is applied after these two default sorts
        String sortBy = filterParams.getSortBy();
        boolean sortDescending = filterParams.isSortDescending();
        
        // Base sorting: date asc, then sort asc
        Sort sort = Sort.by(Sort.Direction.ASC, "date", "sort");
        
        // If user specified a sort field (other than date), add it to the sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            String lowerSortBy = sortBy.toLowerCase();
            if (Set.of("amount", "title").contains(lowerSortBy)) {
                Sort.Direction direction = sortDescending ? Sort.Direction.DESC : Sort.Direction.ASC;
                sort = sort.and(Sort.by(direction, lowerSortBy));
            }
        }
        
        return expenseRepository.findAll(spec, sort);
    }
    
    private Specification<Expense> applyFilters(Specification<Expense> spec, ExpenseFilterParams filterParams) {
        // Category filter
        String categoryId = filterParams.getCategoryId();
        if (categoryId != null && !categoryId.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("category").get("id"), categoryId));
        }
        
        // Date range filter
        LocalDate startDate = filterParams.getStartDate();
        LocalDate endDate = filterParams.getEndDate();
        if (startDate != null && endDate != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("date"), startDate, endDate));
        } else if (startDate != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("date"), startDate));
        } else if (endDate != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("date"), endDate));
        }
        
        // Amount range filter
        BigDecimal minAmount = filterParams.getMinAmount();
        BigDecimal maxAmount = filterParams.getMaxAmount();
        if (minAmount != null && maxAmount != null) {
            spec = spec.and((root, query, cb) -> cb.between(root.get("amount"), minAmount, maxAmount));
        } else if (minAmount != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("amount"), minAmount));
        } else if (maxAmount != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("amount"), maxAmount));
        }
        
        // Keyword search (title and note)
        String keyword = filterParams.getKeyword();
        if (keyword != null && !keyword.isEmpty()) {
            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            spec = spec.and((root, query, cb) -> 
                    cb.or(
                            cb.like(cb.lower(root.get("title")), likeKeyword),
                            cb.like(cb.lower(root.get("note")), likeKeyword)
                    ));
        }
        
        return spec;
    }
}
