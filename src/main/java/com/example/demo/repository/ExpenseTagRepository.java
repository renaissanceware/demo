package com.example.demo.repository;

import com.example.demo.entity.ExpenseTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExpenseTagRepository extends JpaRepository<ExpenseTag, String> {
    
    List<ExpenseTag> findByExpenseId(String expenseId);
    
    List<ExpenseTag> findByTagId(String tagId);
    
    void deleteByExpenseId(String expenseId);
    
    void deleteByTagId(String tagId);
    
    boolean existsByExpenseIdAndTagId(String expenseId, String tagId);
}
