package com.smartvehicle.repository;

import com.smartvehicle.model.User;
import com.smartvehicle.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    List<Vehicle> findByStatus(Vehicle.Status status);
    List<Vehicle> findBySeller(User seller);
    List<Vehicle> findByBuyer(User buyer);
    
    long countByStatus(Vehicle.Status status);
    
    @org.springframework.data.jpa.repository.Query("SELECT DISTINCT v.seller FROM Vehicle v WHERE v.status = :status")
    List<User> findDistinctSellersByStatus(@org.springframework.data.repository.query.Param("status") Vehicle.Status status);
}
