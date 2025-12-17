package com.example.demo.repository;

import java.math.BigDecimal;

import com.example.demo.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String>, JpaSpecificationExecutor<Expense> {
    
    List<Expense> findByTitleContaining(String title);
    
    List<Expense> findAllByOrderByDateAscSortAsc();
    
    List<Expense> findAllByOrderBySortAsc();
    
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByCategoryIdAndDateBetweenAndAmountBetween(String categoryId, LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount);
    
    List<Expense> findByDateBetweenAndAmountBetween(LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount);
    
    List<Expense> findByCategoryIdAndAmountBetween(String categoryId, BigDecimal minAmount, BigDecimal maxAmount);
    
    List<Expense> findByCategoryIdAndDateBetween(String categoryId, LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    List<Expense> findByCategoryId(String categoryId);
}
