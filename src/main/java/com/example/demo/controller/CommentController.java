package com.example.demo.controller;

import com.example.demo.entity.Comment;
import com.example.demo.service.CommentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/expenses/comments")
public class CommentController {

    private final CommentService commentService;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * 保存或更新评论
     * 前端通过POST请求调用此接口，发送JSON数据：{"date": "yyyy-MM-dd", "content": "评论内容"}
     */
    @PostMapping
    public ResponseEntity<?> saveComment(@RequestBody Map<String, String> requestBody) {
        try {
            String dateString = requestBody.get("date");
            String content = requestBody.get("content");

            if (dateString == null || dateString.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Date is required"
                ));
            }

            if (content == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "status", "error",
                        "message", "Content is required"
                ));
            }

            LocalDate date = LocalDate.parse(dateString, DATE_FORMATTER);
            Comment savedComment = commentService.saveComment(date, content);

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "commentId", savedComment.getId(),
                    "date", savedComment.getDate().format(DATE_FORMATTER),
                    "content", savedComment.getContent()
            ));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid date format. Please use yyyy-MM-dd"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Failed to save comment: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据ID删除评论
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteCommentById(@PathVariable String commentId) {
        try {
            commentService.deleteCommentById(commentId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Comment deleted successfully"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Failed to delete comment: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据日期删除评论
     */
    @DeleteMapping("/date/{date}")
    public ResponseEntity<?> deleteCommentByDate(@PathVariable String date) {
        try {
            LocalDate commentDate = LocalDate.parse(date, DATE_FORMATTER);
            commentService.deleteCommentByDate(commentDate);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Comment deleted successfully"
            ));
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid date format. Please use yyyy-MM-dd"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Failed to delete comment: " + e.getMessage()
            ));
        }
    }

    /**
     * 根据日期获取评论
     */
    @GetMapping("/date/{date}")
    public ResponseEntity<?> getCommentByDate(@PathVariable String date) {
        try {
            LocalDate commentDate = LocalDate.parse(date, DATE_FORMATTER);
            Optional<Comment> comment = commentService.getCommentByDate(commentDate);

            if (comment.isPresent()) {
                Comment foundComment = comment.get();
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "id", foundComment.getId(),
                        "date", foundComment.getDate().format(DATE_FORMATTER),
                        "content", foundComment.getContent()
                ));
            } else {
                return ResponseEntity.ok(Map.of(
                        "status", "error",
                        "message", "Comment not found"
                ));
            }
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid date format. Please use yyyy-MM-dd"
            ));
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of(
                    "status", "error",
                    "message", "Failed to get comment: " + e.getMessage()
            ));
        }
    }
}
