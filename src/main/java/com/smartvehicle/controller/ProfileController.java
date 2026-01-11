package com.smartvehicle.controller;

import com.smartvehicle.model.User;
import com.smartvehicle.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class ProfileController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/profile")
    public String viewProfile(HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return "redirect:/login";
        }
        // Refresh user from DB to get latest data
        User currentUser = userRepository.findById(user.getId()).orElse(user);
        model.addAttribute("user", currentUser);
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@RequestParam("mobile") String mobile,
                                @RequestParam("email") String email,
                                HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        User currentUser = userRepository.findById(user.getId()).orElse(user);
        
        // Name is NOT updated as per requirement
        currentUser.setMobile(mobile);
        currentUser.setEmail(email);
        
        userRepository.save(currentUser);
        session.setAttribute("currentUser", currentUser); // Update session

        model.addAttribute("success", "Profile updated successfully!");
        model.addAttribute("user", currentUser); // Ensure model has updated user
        return "profile";
    }

    @PostMapping("/profile/password")
    public String changePassword(@RequestParam("currentPassword") String currentPassword,
                                 @RequestParam("newPassword") String newPassword,
                                 HttpSession session, Model model) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) return "redirect:/login";

        User currentUser = userRepository.findById(user.getId()).orElse(user);

        if (!currentUser.getPassword().equals(currentPassword)) {
            model.addAttribute("error", "Incorrect current password");
            model.addAttribute("user", currentUser);
            return "profile";
        }

        currentUser.setPassword(newPassword);
        userRepository.save(currentUser);
        
        model.addAttribute("success", "Password changed successfully!");
        model.addAttribute("user", currentUser);
        return "profile";
    }
}
