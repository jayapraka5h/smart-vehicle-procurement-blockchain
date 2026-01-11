package com.smartvehicle.controller;

import com.smartvehicle.model.Feedback;
import com.smartvehicle.model.User;
import com.smartvehicle.model.Vehicle;
import com.smartvehicle.repository.FeedbackRepository;
import com.smartvehicle.repository.VehicleRepository;
import com.smartvehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/buyer")
public class BuyerController {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private VehicleService vehicleService;

    @Autowired
    private FeedbackRepository feedbackRepository;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !"BUYER".equals(user.getRole().name())) {
            return "redirect:/login";
        }

        List<Vehicle> availableVehicles = vehicleRepository.findByStatus(Vehicle.Status.AVAILABLE);
        List<Vehicle> myPurchases = vehicleRepository.findByBuyer(user);

        model.addAttribute("availableVehicles", availableVehicles);
        model.addAttribute("myPurchases", myPurchases);
        model.addAttribute("user", user);

        return "buyer_dashboard";
    }

    @PostMapping("/buy/{id}")
    public String buyVehicle(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !"BUYER".equals(user.getRole().name())) {
            return "redirect:/login";
        }

        Vehicle vehicle = vehicleRepository.findById(id).orElse(null);
        if (vehicle != null && vehicle.getStatus() == Vehicle.Status.AVAILABLE) {
            vehicle.setBuyer(user);
            vehicle.setStatus(Vehicle.Status.SOLD);
            vehicleRepository.save(vehicle);
        }

        return "redirect:/buyer/dashboard";
    }

    @GetMapping("/feedback/{vehicleId}")
    public String showFeedbackForm(@PathVariable Long vehicleId, HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !"BUYER".equals(user.getRole().name())) {
            return "redirect:/login";
        }

        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        // Ensure vehicle exists, belongs to buyer, and is sold
        if (vehicle == null || vehicle.getBuyer() == null || !vehicle.getBuyer().getId().equals(user.getId())) {
             return "redirect:/buyer/dashboard";
        }

        // Check if feedback already exists
        if (feedbackRepository.existsByVehicleId(vehicleId)) {
             return "redirect:/buyer/dashboard?error=FeedbackAlreadySubmitted";
        }

        model.addAttribute("vehicle", vehicle);
        return "feedback_form";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam Long vehicleId, 
                                 @RequestParam Integer rating, 
                                 @RequestParam String comment, 
                                 HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || !"BUYER".equals(user.getRole().name())) {
            return "redirect:/login";
        }

        System.out.println("DEBUG: Received feedback request. VehicleId: " + vehicleId + ", Rating: " + rating + ", Comment: " + comment);

        Vehicle vehicle = vehicleRepository.findById(vehicleId).orElse(null);
        if (vehicle != null && vehicle.getBuyer() != null && vehicle.getBuyer().getId().equals(user.getId())) {
             Feedback feedback = new Feedback(vehicle, user, rating, comment);
             feedbackRepository.save(feedback);
             System.out.println("DEBUG: Feedback saved successfully for Vehicle ID: " + vehicleId);
        } else {
             System.out.println("DEBUG: Feedback save FAILED. Vehicle null or buyer mismatch.");
        }

        return "redirect:/buyer/dashboard?success=FeedbackSubmitted";
    }
}
