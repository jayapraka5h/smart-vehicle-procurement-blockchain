package com.smartvehicle.controller;

import com.smartvehicle.model.User;
import com.smartvehicle.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {

    @Autowired
    private UserService userService;

    @Autowired
    private com.smartvehicle.repository.FeedbackRepository feedbackRepository;

    @Autowired
    private com.smartvehicle.repository.VehicleRepository vehicleRepository;

    @GetMapping("/")
    public String index(Model model) {
        // Fetch top 3 highest rated reviews for the homepage
        model.addAttribute("happyCustomers", feedbackRepository.findTop3ByOrderByRatingDescCreatedAtDesc());
        
        // Fetch Top 3 Sellers and Buyers using Java Stream aggregation (Fail-safe)
        java.util.List<com.smartvehicle.model.Vehicle> allVehicles = vehicleRepository.findAll();

        // Top Sellers
        java.util.List<Object[]> sellers = allVehicles.stream()
            .filter(v -> v.getStatus() == com.smartvehicle.model.Vehicle.Status.SOLD && v.getSeller() != null)
            .collect(java.util.stream.Collectors.groupingBy(com.smartvehicle.model.Vehicle::getSeller, java.util.stream.Collectors.counting()))
            .entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(3)
            .map(e -> new Object[]{e.getKey(), e.getValue()})
            .collect(java.util.stream.Collectors.toList());

        // Top Buyers
        java.util.List<Object[]> buyers = allVehicles.stream()
            .filter(v -> v.getBuyer() != null)
            .collect(java.util.stream.Collectors.groupingBy(com.smartvehicle.model.Vehicle::getBuyer, java.util.stream.Collectors.counting()))
            .entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .limit(3)
            .map(e -> new Object[]{e.getKey(), e.getValue()})
            .collect(java.util.stream.Collectors.toList());
        
        model.addAttribute("topSellers", sellers);
        model.addAttribute("topBuyers", buyers);
        
        return "index";
    }

    // --- Authentication ---

    @GetMapping("/login")
    public String loginPage(Model model) {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String loginId, @RequestParam String password, HttpSession session, Model model) {
        try {
            User user = userService.login(loginId, password);
            session.setAttribute("currentUser", user);
            
            if (user.getRole() == User.Role.ADMIN) return "redirect:/admin/dashboard";
            if (user.getRole() == User.Role.SELLER) return "redirect:/seller/dashboard";
            if (user.getRole() == User.Role.BUYER) return "redirect:/buyer/dashboard";
            
            return "redirect:/";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "login";
        }
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login?success=Registration successful. Please wait for approval.";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}
