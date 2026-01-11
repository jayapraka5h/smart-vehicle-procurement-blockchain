package com.smartvehicle.service;

import com.smartvehicle.model.User;
import com.smartvehicle.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user) {
        // Basic validation
        if (userRepository.findByLoginId(user.getLoginId()).isPresent()) {
            throw new RuntimeException("Login ID already exists");
        }
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }
        
        // Set default status to WAITING
        user.setStatus(User.Status.WAITING);
        
        // If ADMIN role is requested, maybe approve automatically or handle specifically?
        // For this demo, let's assume ADMIN is pre-created or handled separately, typical users register as BUYER/SELLER
        
        return userRepository.save(user);
    }

    public User login(String loginId, String password) {
        User user = userRepository.findByLoginId(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!user.getPassword().equals(password)) {
            throw new RuntimeException("Invalid credentials");
        }
        
        if (user.getStatus() != User.Status.APPROVED) {
            throw new RuntimeException("Account not approved yet. Current status: " + user.getStatus());
        }
        
        return user;
    }

    public List<User> getPendingUsers() {
        return userRepository.findByStatus(User.Status.WAITING);
    }

    public void approveUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.Status.APPROVED);
        userRepository.save(user);
    }
    
    public void rejectUser(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setStatus(User.Status.REJECTED);
        userRepository.save(user);
    }
    
    @jakarta.annotation.PostConstruct
    public void createDefaultAdmin() {
        if (userRepository.findByLoginId("admin").isEmpty()) {
            User admin = new User();
            admin.setName("Administrator");
            admin.setLoginId("admin");
            admin.setPassword("admin"); // Simple password for demo
            admin.setEmail("admin@smartvehicle.com");
            admin.setMobile("0000000000");
            admin.setRole(User.Role.ADMIN);
            admin.setStatus(User.Status.APPROVED);
            userRepository.save(admin);
            System.out.println("DEFAULT ADMIN CREATED: Login 'admin' / 'admin'");
        }
    }
}
