package com.fitness.entity;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 训练记录实体类
 */
@Entity
@Table(name = "training_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonProperty("user_id")
    private Long userId;

    @JsonProperty("plan_id")
    private Long planId;

    @Column(nullable = false, length = 100)
    @JsonProperty("exercise_name")
    private String exerciseName;

    private Integer duration;

    private Double calories;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    @JsonProperty("record_date")
    private LocalDate recordDate;
}
