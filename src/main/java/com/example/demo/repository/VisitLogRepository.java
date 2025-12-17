package com.example.demo.repository;

import com.example.demo.entity.VisitLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface VisitLogRepository extends JpaRepository<VisitLog, String> {
    Optional<VisitLog> findByDate(LocalDate date);
}