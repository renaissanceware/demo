package com.example.demo.controller;

import com.example.demo.service.VisitLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/visitlogs")
public class VisitLogController {
    
    @Autowired
    private VisitLogService visitLogService;
    
    /**
     * 切换指定日期的访问记录状态
     */
    @PostMapping("/toggle")
    public ResponseEntity<Map<String, Boolean>> toggleLog(@RequestBody Map<String, String> request) {
        String dateString = request.get("date");
        LocalDate date = LocalDate.parse(dateString);
        
        boolean hasVisitLog = visitLogService.toggleVisitLog(date);
        
        return ResponseEntity.ok(Map.of("hasVisitLog", hasVisitLog));
    }
    
    /**
     * 检查指定日期是否有访问记录
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Boolean>> checkLog(@RequestParam String date) {
        LocalDate visitLogDate = LocalDate.parse(date);
        boolean hasVisitLog = visitLogService.hasVisitLog(visitLogDate);
        
        return ResponseEntity.ok(Map.of("hasVisitLog", hasVisitLog));
    }
}