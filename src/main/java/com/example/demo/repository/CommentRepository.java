package com.example.demo.repository;

import com.example.demo.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    
    // 根据日期范围查询评论列表（用于月度视图）
    List<Comment> findByDateBetween(LocalDate startDate, LocalDate endDate);
    
    // 根据日期查找单个评论（用于检查同一天是否已存在评论）
    Optional<Comment> findByDate(LocalDate date);
}
