package com.example.demo.service;

import com.example.demo.entity.Comment;
import com.example.demo.repository.CommentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class CommentService {

    private final CommentRepository commentRepository;

    public CommentService(CommentRepository commentRepository) {
        this.commentRepository = commentRepository;
    }

    /**
     * 保存评论（如果已存在则更新，否则创建）
     */
    @Transactional
    public Comment saveComment(LocalDate date, String content) {
        Optional<Comment> existingComment = commentRepository.findByDate(date);
        Comment comment;

        if (existingComment.isPresent()) {
            // 更新已有评论
            comment = existingComment.get();
            comment.setContent(content);
        } else {
            // 创建新评论
            comment = new Comment(content, date);
        }

        return commentRepository.save(comment);
    }

    /**
     * 根据ID删除评论
     */
    @Transactional
    public void deleteCommentById(String id) {
        commentRepository.deleteById(id);
    }

    /**
     * 根据日期删除评论
     */
    @Transactional
    public void deleteCommentByDate(LocalDate date) {
        Optional<Comment> comment = commentRepository.findByDate(date);
        comment.ifPresent(commentRepository::delete);
    }

    /**
     * 根据日期获取评论
     */
    public Optional<Comment> getCommentByDate(LocalDate date) {
        return commentRepository.findByDate(date);
    }

    /**
     * 根据日期范围获取评论列表
     */
    public List<Comment> getCommentsByDateRange(LocalDate startDate, LocalDate endDate) {
        return commentRepository.findByDateBetween(startDate, endDate);
    }
}
