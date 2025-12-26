package com.fitness.entity;

import jakarta.persistence.*;
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
    private Long userId;

    private Double weight;

    private Double bodyFat;

    private Double bmi;

    @Column(nullable = false)
    private LocalDate recordDate;
}
