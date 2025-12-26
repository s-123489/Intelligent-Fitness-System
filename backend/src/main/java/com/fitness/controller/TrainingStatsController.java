package com.fitness.controller;

import com.fitness.service.TrainingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/training-stats")
public class TrainingStatsController {

    @Autowired
    private TrainingRecordService trainingRecordService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getStats(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        Map<String, Object> stats = trainingRecordService.getStats(userId);
        return ResponseEntity.ok(stats);
    }
}
