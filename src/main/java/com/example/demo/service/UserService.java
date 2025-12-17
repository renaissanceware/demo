package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> getAllUsers() {
        logger.info("getAllUsers called");
        try {
            List<User> users = userRepository.findAll();
            logger.info("Retrieved {} users", users.size());
            return users;
        } catch (Exception e) {
            logger.error("Error in getAllUsers: {}", e.getMessage(), e);
            throw e;
        }
    }

    public Optional<User> getUserById(String id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User saveUser(User user) {
        // Validate password for new user
        if (user.getPassword() == null || user.getPassword().trim().isEmpty()) {
            throw new RuntimeException("Password is required for new users");
        }
        // Check if username already exists
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("User with username '" + user.getUsername() + "' already exists");
        }
        // Check if email already exists
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("User with email '" + user.getEmail() + "' already exists");
        }
        // Encrypt password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        return userRepository.save(user);
    }

    public User updateUser(String id, User userDetails) {
        logger.info("updateUser called with id: {} and userDetails: {}", id, userDetails);
        return userRepository.findById(id)
                .map(user -> {
                    logger.info("Found existing user: {}", user);
                    // Check if username already exists
                    if (!user.getUsername().equals(userDetails.getUsername()) &&
                            userRepository.existsByUsername(userDetails.getUsername())) {
                        throw new RuntimeException("User with username '" + userDetails.getUsername() + "' already exists");
                    }
                    // Check if email already exists
                    if (!user.getEmail().equals(userDetails.getEmail()) &&
                            userRepository.existsByEmail(userDetails.getEmail())) {
                        throw new RuntimeException("User with email '" + userDetails.getEmail() + "' already exists");
                    }
                    // Update user details
                    logger.info("Updating user details: username={}, email={}, fullName={}, title={}, role={}, iconUrl={}", 
                               userDetails.getUsername(), userDetails.getEmail(), userDetails.getFullName(),
                               userDetails.getTitle(), userDetails.getRole(), userDetails.getIconUrl());
                    user.setUsername(userDetails.getUsername());
                    user.setEmail(userDetails.getEmail());
                    user.setFullName(userDetails.getFullName());
                    user.setTitle(userDetails.getTitle());
                    user.setRole(userDetails.getRole());
                    user.setIconUrl(userDetails.getIconUrl());
                    // Only update password if provided
                    if (userDetails.getPassword() != null && !userDetails.getPassword().isEmpty()) {
                        logger.info("Updating password");
                        user.setPassword(passwordEncoder.encode(userDetails.getPassword()));
                    } else {
                        logger.info("Password not provided, skipping password update");
                    }
                    User updatedUser = userRepository.save(user);
                    logger.info("Updated user: {}", updatedUser);
                    return updatedUser;
                })
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    public void deleteUser(String id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    public boolean userExists(String id) {
        return userRepository.existsById(id);
    }

    public void deleteAllUsers() {
        userRepository.deleteAll();
    }

    public java.util.Optional<User> authenticate(String username, String rawPassword) {
        java.util.Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (passwordEncoder.matches(rawPassword, user.getPassword())) {
                return java.util.Optional.of(user);
            }
        }
        return java.util.Optional.empty();
    }
}