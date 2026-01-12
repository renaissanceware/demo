package com.example.demo.service;

import com.example.demo.entity.Tag;
import com.example.demo.repository.TagRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TagService {
    
    @Autowired
    private TagRepository tagRepository;
    
    public List<Tag> getAllTags() {
        return tagRepository.findAllByOrderByNameAsc();
    }
    
    public Optional<Tag> getTagById(String id) {
        return tagRepository.findById(id);
    }
    
    public Optional<Tag> getTagByName(String name) {
        return tagRepository.findByName(name);
    }
    
    public Tag saveTag(@Valid Tag tag) {
        if (tagRepository.existsByName(tag.getName())) {
            throw new RuntimeException("Tag with name '" + tag.getName() + "' already exists");
        }
        return tagRepository.save(tag);
    }
    
    public Tag updateTag(String id, @Valid Tag tagDetails) {
        return tagRepository.findById(id)
            .map(tag -> {
                if (!tag.getName().equals(tagDetails.getName()) && 
                    tagRepository.existsByName(tagDetails.getName())) {
                    throw new RuntimeException("Tag with name '" + tagDetails.getName() + "' already exists");
                }
                tag.setName(tagDetails.getName());
                tag.setDescription(tagDetails.getDescription());
                tag.setIconUrl(tagDetails.getIconUrl());
                tag.setColor(tagDetails.getColor());
                return tagRepository.save(tag);
            })
            .orElseThrow(() -> new RuntimeException("Tag not found with id: " + id));
    }
    
    public void deleteTag(String id) {
        if (!tagRepository.existsById(id)) {
            throw new RuntimeException("Tag not found with id: " + id);
        }
        tagRepository.deleteById(id);
    }
    
    public List<Tag> searchTags(String name) {
        return tagRepository.findByNameContainingOrderByNameAsc(name);
    }
    
    public boolean tagExists(String id) {
        return tagRepository.existsById(id);
    }
    
    public void deleteAllTags() {
        tagRepository.deleteAll();
    }
}