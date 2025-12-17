package com.example.demo.service;

import com.example.demo.entity.Payment;
import com.example.demo.repository.PaymentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PaymentService {
    
    @Autowired
    private PaymentRepository paymentRepository;
    
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
    
    public Optional<Payment> getPaymentById(String id) {
        return paymentRepository.findById(id);
    }
    
    public Optional<Payment> getPaymentByName(String name) {
        return paymentRepository.findByName(name);
    }
    
    public Payment savePayment(@Valid Payment payment) {
        if (paymentRepository.existsByName(payment.getName())) {
            throw new RuntimeException("Payment method with name '" + payment.getName() + "' already exists");
        }
        payment.setCreatedAt(LocalDateTime.now());
        return paymentRepository.save(payment);
    }
    
    public Payment updatePayment(String id, @Valid Payment paymentDetails) {
        return paymentRepository.findById(id)
            .map(payment -> {
                if (!payment.getName().equals(paymentDetails.getName()) && 
                    paymentRepository.existsByName(paymentDetails.getName())) {
                    throw new RuntimeException("Payment method with name '" + paymentDetails.getName() + "' already exists");
                }
                payment.setName(paymentDetails.getName());
                payment.setDescription(paymentDetails.getDescription());
                payment.setCssClass(paymentDetails.getCssClass());
                return paymentRepository.save(payment);
            })
            .orElseThrow(() -> new RuntimeException("Payment method not found with id: " + id));
    }
    
    public void deletePayment(String id) {
        if (!paymentRepository.existsById(id)) {
            throw new RuntimeException("Payment method not found with id: " + id);
        }
        paymentRepository.deleteById(id);
    }
    
    public List<Payment> searchPayments(String name) {
        return paymentRepository.findByNameContaining(name);
    }
    
    public boolean paymentExists(String id) {
        return paymentRepository.existsById(id);
    }
    
    public void deleteAllPayments() {
        paymentRepository.deleteAll();
    }
}