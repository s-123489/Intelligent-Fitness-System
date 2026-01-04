package com.fitness.entity;

import javax.persistence.*;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 身体数据实体类
 */
@Entity
@Table(name = "body_data")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BodyData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @JsonProperty("user_id")
    private Long userId;

    private Double weight;

    @JsonProperty("body_fat")
    private Double bodyFat;

    private Double bmi;

    @Column(nullable = false)
    @JsonProperty("record_date")
    private LocalDate recordDate;
}
