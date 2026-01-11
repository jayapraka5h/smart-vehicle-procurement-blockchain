package com.smartvehicle.service;

import com.smartvehicle.model.User;
import com.smartvehicle.model.Vehicle;
import com.smartvehicle.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class VehicleService {

    @Autowired
    private VehicleRepository vehicleRepository;

    public Vehicle addVehicle(Vehicle vehicle, User seller) {
        vehicle.setSeller(seller);
        vehicle.setStatus(Vehicle.Status.AVAILABLE);
        vehicle.setCreatedAt(LocalDateTime.now());
        
        // Generate initial block hash
        vehicle.setBlockHash(calculateHash(vehicle));
        
        return vehicleRepository.save(vehicle);
    }

    public List<Vehicle> getAvailableVehicles() {
        return vehicleRepository.findByStatus(Vehicle.Status.AVAILABLE);
    }
    
    public List<Vehicle> getMySoldVehicles(User seller) {
        return vehicleRepository.findBySeller(seller);
    }

    public Vehicle getVehicleById(Long id) {
        return vehicleRepository.findById(id).orElse(null);
    }

    public void purchaseVehicle(Long vehicleId, User buyer) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));
        
        if (vehicle.getStatus() != Vehicle.Status.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available for purchase");
        }
        
        vehicle.setBuyer(buyer);
        vehicle.setStatus(Vehicle.Status.SOLD);
        
        // Blockchain: Update hash to link transaction
        String transactionData = vehicle.getVehicleNumber() + buyer.getLoginId() + LocalDateTime.now().toString();
        String newHash = calculateHash(transactionData, vehicle.getBlockHash());
        vehicle.setBlockHash(newHash);
        
        vehicleRepository.save(vehicle);
    }

    // "Blockchain" Simulation: SHA-256 Hashing
    private String calculateHash(Vehicle v) {
        String data = v.getVehicleNumber() + v.getSeller().getLoginId() + v.getPrice() + v.getCreatedAt();
        return applySha256(data);
    }
    
    private String calculateHash(String newData, String previousHash) {
        return applySha256(newData + previousHash);
    }

    private String applySha256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
