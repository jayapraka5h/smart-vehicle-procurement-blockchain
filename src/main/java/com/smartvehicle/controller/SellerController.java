package com.smartvehicle.controller;

import com.smartvehicle.model.User;
import com.smartvehicle.service.VehicleService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/seller")
public class SellerController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null || user.getRole() != User.Role.SELLER) return "redirect:/login";

        model.addAttribute("myVehicles", vehicleService.getMySoldVehicles(user));
        return "seller_dashboard";
    }
}
