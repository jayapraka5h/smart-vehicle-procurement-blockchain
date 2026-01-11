package com.smartvehicle.controller;

import com.smartvehicle.model.User;
import com.smartvehicle.model.Vehicle;
import com.smartvehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/vehicle")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;
    
    private final String UPLOAD_DIR = "uploads/";

    // --- Seller Operations ---

    @GetMapping("/add")
    public String addVehiclePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.SELLER) return "redirect:/login";
        
        model.addAttribute("vehicle", new Vehicle());
        return "add_vehicle";
    }

    @PostMapping("/add")
    public String addVehicle(@ModelAttribute Vehicle vehicle, 
                             @RequestParam("image") MultipartFile image,
                             HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.SELLER) return "redirect:/login";

        try {
            // Save Image to DB
            if (!image.isEmpty()) {
                vehicle.setImage(image.getBytes());
                vehicle.setImageContentType(image.getContentType());
            }
            
            vehicleService.addVehicle(vehicle, user);
            return "redirect:/seller/dashboard";
            
        } catch (Exception e) {
            e.printStackTrace();
            return "redirect:/vehicle/add?error=" + e.getMessage();
        }
    }

    @GetMapping("/image/{id}")
    @ResponseBody
    public org.springframework.http.ResponseEntity<byte[]> getVehicleImage(@PathVariable Long id) {
        Vehicle vehicle = vehicleService.getVehicleById(id);
        if (vehicle != null && vehicle.getImage() != null) {
            return org.springframework.http.ResponseEntity.ok()
                    .contentType(org.springframework.http.MediaType.parseMediaType(vehicle.getImageContentType()))
                    .body(vehicle.getImage());
        }
        return org.springframework.http.ResponseEntity.notFound().build();
    }

    // --- Buyer Operations ---

    @PostMapping("/buy/{id}")
    public String buyVehicle(@PathVariable Long id, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.BUYER) return "redirect:/login";

        vehicleService.purchaseVehicle(id, user);
        return "redirect:/buyer/dashboard";
    }
}
