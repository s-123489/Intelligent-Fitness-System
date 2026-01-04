package com.fitness.entity;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 健身计划实体类
 */
@Entity
@Table(name = "fitness_plans")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class FitnessPlan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonProperty("user_id")
    private Long userId;

    @Column(nullable = false, length = 100)
    @JsonProperty("plan_name")
    private String planName;

    @Column(length = 50)
    private String goal;

    @Column(length = 50)
    private String difficulty;

    private Integer duration;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(columnDefinition = "TEXT")
    @JsonProperty("plan_content")
    private String planContent;

    @JsonProperty("is_ai_generated")
    private Boolean isAiGenerated;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    @JsonProperty("created_at")
    private LocalDateTime createdAt;
}
