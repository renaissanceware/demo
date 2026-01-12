package com.example.demo.repository;

import java.math.BigDecimal;

import com.example.demo.entity.Expense;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExpenseRepository extends JpaRepository<Expense, String>, JpaSpecificationExecutor<Expense> {
    
    List<Expense> findByTitleContaining(String title);
    
    List<Expense> findAllByOrderByDateAscSortAsc();
    
    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.expenseTags et LEFT JOIN FETCH et.tag ORDER BY e.date ASC, e.sort ASC")
    List<Expense> findAllWithTags();
    
    @Query("SELECT e FROM Expense e LEFT JOIN FETCH e.expenseTags et LEFT JOIN FETCH et.tag WHERE e.id = :id")
    Optional<Expense> findByIdWithTags(String id);
    
    List<Expense> findAllByOrderBySortAsc();
    
    List<Expense> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByCategoryIdAndDateBetweenAndAmountBetween(String categoryId, LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount);
    
    List<Expense> findByDateBetweenAndAmountBetween(LocalDate startDate, LocalDate endDate, BigDecimal minAmount, BigDecimal maxAmount);
    
    List<Expense> findByCategoryIdAndAmountBetween(String categoryId, BigDecimal minAmount, BigDecimal maxAmount);
    
    List<Expense> findByCategoryIdAndDateBetween(String categoryId, LocalDate startDate, LocalDate endDate);
    
    List<Expense> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);
    
    List<Expense> findByCategoryId(String categoryId);
}
