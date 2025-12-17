package com.example.demo.service;

import com.example.demo.entity.Category;
import com.example.demo.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CategoryService {
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }
    
    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }
    
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByName(name);
    }
    
    public Category saveCategory(@Valid Category category) {
        if (categoryRepository.existsByName(category.getName())) {
            throw new RuntimeException("Category with name '" + category.getName() + "' already exists");
        }
        category.setCreatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }
    
    public Category updateCategory(String id, @Valid Category categoryDetails) {
        return categoryRepository.findById(id)
            .map(category -> {
                if (!category.getName().equals(categoryDetails.getName()) && 
                    categoryRepository.existsByName(categoryDetails.getName())) {
                    throw new RuntimeException("Category with name '" + categoryDetails.getName() + "' already exists");
                }
                category.setName(categoryDetails.getName());
                category.setDescription(categoryDetails.getDescription());
                category.setIconUrl(categoryDetails.getIconUrl());
                category.setColor(categoryDetails.getColor());
                return categoryRepository.save(category);
            })
            .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
    }
    
    public void deleteCategory(String id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }
    
    public List<Category> searchCategories(String name) {
        return categoryRepository.findByNameContaining(name);
    }
    
    public boolean categoryExists(String id) {
        return categoryRepository.existsById(id);
    }
    
    public void deleteAllCategories() {
        // Then delete all categories
        categoryRepository.deleteAll();
    }
}