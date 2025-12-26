package com.fitness.repository;

import com.fitness.entity.FitnessPlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 健身计划Repository
 */
@Repository
public interface FitnessPlanRepository extends JpaRepository<FitnessPlan, Long> {

    List<FitnessPlan> findByUserIdOrderByCreatedAtDesc(Long userId);
}
