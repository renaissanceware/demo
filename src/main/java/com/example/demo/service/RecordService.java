package com.example.demo.service;

import com.example.demo.entity.Record;
import com.example.demo.repository.RecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
public class RecordService {

    @Autowired
    private RecordRepository recordRepository;

    public List<Record> getAllRecords() {
        return recordRepository.findAllByOrderByCreatedAtAsc();
    }

    public Optional<Record> getRecordById(String id) {
        return recordRepository.findById(id);
    }

    public Record addRecord(@Valid Record record) {
        return recordRepository.save(record);
    }

    public Record updateRecord(String id, @Valid Record recordDetails) {
        return recordRepository.findById(id)
                .map(record -> {
                    record.setTitle(recordDetails.getTitle());
                    record.setAmount(recordDetails.getAmount());
                    record.setType(recordDetails.getType());
                    return recordRepository.save(record);
                })
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));
    }

    public void deleteRecord(String id) {
        if (!recordRepository.existsById(id)) {
            throw new RuntimeException("Record not found with id: " + id);
        }
        recordRepository.deleteById(id);
    }
    
    public void updateRecordField(String id, String field, String value) {
        Record record = recordRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Record not found with id: " + id));
        
        switch (field) {
            case "title":
                record.setTitle(value);
                break;
            case "amount":
                record.setAmount(new BigDecimal(value));
                break;
            case "type":
                record.setType(value);
                break;
            default:
                throw new IllegalArgumentException("Invalid field: " + field);
        }
        
        recordRepository.save(record);
    }
    
    public void deleteRecords(String[] ids) {
        for (String id : ids) {
            if (recordRepository.existsById(id)) {
                recordRepository.deleteById(id);
            }
        }
    }

    public List<Record> getRecordsByType(String type) {
        return recordRepository.findByTypeOrderByCreatedAtAsc(type);
    }

    public BigDecimal getTotalIncome() {
        BigDecimal totalIncome = recordRepository.getTotalIncome();
        return totalIncome != null ? totalIncome : BigDecimal.ZERO;
    }

    public BigDecimal getTotalExpense() {
        BigDecimal totalExpense = recordRepository.getTotalExpense();
        return totalExpense != null ? totalExpense : BigDecimal.ZERO;
    }

    public BigDecimal getBalance() {
        BigDecimal balance = recordRepository.getBalance();
        return balance != null ? balance : BigDecimal.ZERO;
    }
}
