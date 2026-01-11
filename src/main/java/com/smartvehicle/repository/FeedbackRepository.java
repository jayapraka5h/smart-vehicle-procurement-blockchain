package com.smartvehicle.repository;

import com.smartvehicle.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findByRating(Integer rating);
    List<Feedback> findAllByOrderByCreatedAtDesc();
    List<Feedback> findTop3ByRatingOrderByCreatedAtDesc(Integer rating);
    List<Feedback> findTop3ByOrderByRatingDescCreatedAtDesc();
    boolean existsByVehicleId(Long vehicleId);
}
