package com.example.demo.service;

import com.example.demo.entity.VisitLog;
import com.example.demo.repository.VisitLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class VisitLogService {
    
    @Autowired
    private VisitLogRepository visitLogRepository;
    
    /**
     * 检查指定日期是否有访问记录
     */
    public boolean hasVisitLog(LocalDate date) {
        return visitLogRepository.findByDate(date).isPresent();
    }
    
    /**
     * 添加访问记录
     */
    public VisitLog addVisitLog(LocalDate date) {
        VisitLog visitLog = new VisitLog(date);
        return visitLogRepository.save(visitLog);
    }
    
    /**
     * 删除访问记录
     */
    public void removeVisitLog(LocalDate date) {
        Optional<VisitLog> visitLog = visitLogRepository.findByDate(date);
        visitLog.ifPresent(visitLogRepository::delete);
    }
    
    /**
     * 切换访问记录状态（有则删除，无则添加）
     */
    public boolean toggleVisitLog(LocalDate date) {
        if (hasVisitLog(date)) {
            removeVisitLog(date);
            return false; // 返回false表示现在没有记录
        } else {
            addVisitLog(date);
            return true; // 返回true表示现在有记录
        }
    }
}