package com.smartvehicle.controller;

import com.smartvehicle.model.User;
import com.smartvehicle.model.Vehicle;
import com.smartvehicle.repository.VehicleRepository;
import com.smartvehicle.repository.UserRepository;
import com.smartvehicle.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserService userService;



    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.ADMIN) return "redirect:/login";

        model.addAttribute("pendingUsers", userService.getPendingUsers());
        
        // Add Statistics
        model.addAttribute("buyerCount", userRepository.countByRole(User.Role.BUYER));
        model.addAttribute("sellerCount", userRepository.countByRole(User.Role.SELLER));
        model.addAttribute("soldCount", vehicleRepository.countByStatus(Vehicle.Status.SOLD));
        model.addAttribute("availableCount", vehicleRepository.countByStatus(Vehicle.Status.AVAILABLE));
        
        // Add Pending Sellers List (Sellers with Available cars)
        model.addAttribute("pendingSellers", vehicleRepository.findDistinctSellersByStatus(Vehicle.Status.AVAILABLE));
        
        return "admin_dashboard";
    }

    @GetMapping("/approve/{id}")
    public String approveUser(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.ADMIN) return "redirect:/login";

        userService.approveUser(id);
        return "redirect:/admin/dashboard";
    }
    @GetMapping("/transactions")
    public String viewTransactions(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.ADMIN) return "redirect:/login";

        // Fetch all SOLD vehicles
        List<Vehicle> soldVehicles = vehicleRepository.findByStatus(Vehicle.Status.SOLD);
        model.addAttribute("transactions", soldVehicles);
        
        return "admin_transactions";
    }

    @GetMapping("/inventory")
    public String viewInventory(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.ADMIN) return "redirect:/login";

        List<Vehicle> availableVehicles = vehicleRepository.findByStatus(Vehicle.Status.AVAILABLE);
        model.addAttribute("vehicles", availableVehicles);
        
        return "admin_inventory";
    }
    @GetMapping("/users/{role}")
    public String listUsers(@PathVariable String role, HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) return "redirect:/login";

        try {
            User.Role userRole = User.Role.valueOf(role.toUpperCase());
            List<User> users = userRepository.findByRole(userRole);
            model.addAttribute("users", users);
            model.addAttribute("roleTitle", role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return "redirect:/admin/dashboard";
        }
        return "admin_users";
    }

    @GetMapping("/user/deactivate/{id}")
    public String deactivateUser(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) return "redirect:/login";

        userRepository.findById(id).ifPresent(user -> {
            if (user.getRole() != User.Role.ADMIN) {
                 user.setStatus(User.Status.REJECTED);
                 userRepository.save(user);
            }
        });
        
        // Redirect back to the list based on the role of the user we just modified?
        // Simpler to go to dashboard or we can try to grab role from referer, but dashboard is safe.
        // Or we could fetch the user role before redirecting to go back to the list.
        return "redirect:/admin/dashboard"; 
    }

    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable Long id, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || currentUser.getRole() != User.Role.ADMIN) return "redirect:/login";
        
        userRepository.findById(id).ifPresent(user -> {
            if (user.getRole() != User.Role.ADMIN) {
                try {
                    userRepository.delete(user);
                } catch (Exception e) {
                    System.err.println("Error deleting user: " + e.getMessage());
                }
            }
        });

        return "redirect:/admin/dashboard";
    }
    @Autowired
    private com.smartvehicle.repository.FeedbackRepository feedbackRepository;

    @GetMapping("/reviews")
    public String viewReviews(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.ADMIN) return "redirect:/login";

        model.addAttribute("reviews", feedbackRepository.findAllByOrderByCreatedAtDesc());
        return "admin_reviews";
    }

    @GetMapping("/leaderboard")
    public String leaderboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.ADMIN) return "redirect:/login";

        java.util.List<com.smartvehicle.model.Vehicle> allVehicles = vehicleRepository.findAll();

        // Top Sellers (Sold vehicles)
        java.util.Map<User, Long> sellerCounts = allVehicles.stream()
            .filter(v -> v.getStatus() == com.smartvehicle.model.Vehicle.Status.SOLD && v.getSeller() != null)
            .collect(java.util.stream.Collectors.groupingBy(com.smartvehicle.model.Vehicle::getSeller, java.util.stream.Collectors.counting()));

        java.util.List<Object[]> topSellers = sellerCounts.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .map(e -> new Object[]{e.getKey(), e.getValue()})
            .collect(java.util.stream.Collectors.toList());

        // Top Buyers (Vehicles with buyers)
        java.util.Map<User, Long> buyerCounts = allVehicles.stream()
            .filter(v -> v.getBuyer() != null)
            .collect(java.util.stream.Collectors.groupingBy(com.smartvehicle.model.Vehicle::getBuyer, java.util.stream.Collectors.counting()));

        java.util.List<Object[]> topBuyers = buyerCounts.entrySet().stream()
            .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
            .map(e -> new Object[]{e.getKey(), e.getValue()})
            .collect(java.util.stream.Collectors.toList());

        model.addAttribute("topSellers", topSellers);
        model.addAttribute("topBuyers", topBuyers);
        return "admin_leaderboard";
    }
}
