package com.fitness.controller;

import com.fitness.dto.ApiResponse;
import com.fitness.entity.FitnessPlan;
import com.fitness.service.FitnessPlanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/fitness-plans")
public class FitnessPlanController {

    @Autowired
    private FitnessPlanService fitnessPlanService;

    @GetMapping
    public ResponseEntity<List<FitnessPlan>> getPlans(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        List<FitnessPlan> plans = fitnessPlanService.getPlansByUserId(userId);
        return ResponseEntity.ok(plans);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<FitnessPlan>> addPlan(@RequestBody FitnessPlan plan,
                                                              Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        plan.setUserId(userId);
        plan.setIsAiGenerated(false);
        
        FitnessPlan saved = fitnessPlanService.savePlan(plan);
        return ResponseEntity.ok(ApiResponse.success("添加成功", saved));
    }

    @PostMapping("/generate")
    public ResponseEntity<ApiResponse<FitnessPlan>> generatePlan(@RequestBody Map<String, Object> request,
                                                                   Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        String goal = (String) request.get("goal");
        Double bodyFat = request.get("bodyFat") != null ? 
                         Double.parseDouble(request.get("bodyFat").toString()) : 20.0;
        Double bmi = request.get("bmi") != null ? 
                     Double.parseDouble(request.get("bmi").toString()) : 22.0;
        
        FitnessPlan plan = fitnessPlanService.generateAIPlan(userId, goal, bodyFat, bmi);
        return ResponseEntity.ok(ApiResponse.success("AI计划生成成功", plan));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deletePlan(@PathVariable Long id) {
        fitnessPlanService.deletePlan(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }
}
