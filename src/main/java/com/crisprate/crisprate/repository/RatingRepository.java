package com.crisprate.crisprate.repository;

import com.crisprate.crisprate.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RatingRepository extends JpaRepository<Rating, Long> {

    List<Rating> findByEstablishmentIdOrderByCreatedAtDesc(Long establishmentId);

    List<Rating> findByUserIdOrderByCreatedAtDesc(Long userId);
}
