package com.fitness.controller;

import com.fitness.service.NutritionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/nutrition")
public class NutritionController {

    @Autowired
    private NutritionService nutritionService;

    @GetMapping("/recommendation")
    public ResponseEntity<Map<String, Object>> getRecommendation(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        Map<String, Object> recommendation = nutritionService.getRecommendation(userId);
        return ResponseEntity.ok(recommendation);
    }
}
