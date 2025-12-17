package com.example.demo.repository;

import com.example.demo.entity.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface RecordRepository extends JpaRepository<Record, String> {
    
    // 按创建时间正序查询所有记录
    List<Record> findAllByOrderByCreatedAtAsc();
    
    // 查询所有收入记录
    List<Record> findByTypeOrderByCreatedAtAsc(String type);
    
    // 统计总收入
    @Query("SELECT SUM(r.amount) FROM Record r WHERE r.type = 'income'")
    BigDecimal getTotalIncome();
    
    // 统计总支出
    @Query("SELECT SUM(r.amount) FROM Record r WHERE r.type = 'expense'")
    BigDecimal getTotalExpense();
    
    // 统计差额（收入-支出）
    @Query("SELECT (SELECT COALESCE(SUM(r.amount), 0) FROM Record r WHERE r.type = 'income') - (SELECT COALESCE(SUM(r.amount), 0) FROM Record r WHERE r.type = 'expense')")
    BigDecimal getBalance();
}
