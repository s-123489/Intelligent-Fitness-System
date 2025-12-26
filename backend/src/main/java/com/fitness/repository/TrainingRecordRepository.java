package com.fitness.repository;

import com.fitness.entity.TrainingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 训练记录Repository
 */
@Repository
public interface TrainingRecordRepository extends JpaRepository<TrainingRecord, Long> {

    List<TrainingRecord> findByUserId(Long userId);

    List<TrainingRecord> findByUserIdOrderByRecordDateDesc(Long userId);

    List<TrainingRecord> findByUserIdAndRecordDateAfterOrderByRecordDateDesc(Long userId, LocalDate date);

    @Query("SELECT COUNT(t) FROM TrainingRecord t WHERE t.userId = :userId")
    Long countByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(t.duration) FROM TrainingRecord t WHERE t.userId = :userId")
    Integer sumDurationByUserId(@Param("userId") Long userId);

    @Query("SELECT SUM(t.calories) FROM TrainingRecord t WHERE t.userId = :userId")
    Double sumCaloriesByUserId(@Param("userId") Long userId);
}
