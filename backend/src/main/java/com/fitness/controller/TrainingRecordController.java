package com.fitness.controller;

import com.fitness.dto.ApiResponse;
import com.fitness.entity.TrainingRecord;
import com.fitness.service.TrainingRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/training-records")
public class TrainingRecordController {

    @Autowired
    private TrainingRecordService trainingRecordService;

    @GetMapping
    public ResponseEntity<List<TrainingRecord>> getRecords(Authentication authentication,
                                                            @RequestParam(required = false) Integer days) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        
        List<TrainingRecord> records;
        if (days != null) {
            records = trainingRecordService.getRecentRecords(userId, days);
        } else {
            records = trainingRecordService.getRecordsByUserId(userId);
        }
        
        return ResponseEntity.ok(records);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TrainingRecord>> addRecord(@RequestBody TrainingRecord record,
                                                                   Authentication authentication) {
        Long userId = Long.parseLong(authentication.getPrincipal().toString());
        record.setUserId(userId);
        
        TrainingRecord saved = trainingRecordService.saveRecord(record);
        return ResponseEntity.ok(ApiResponse.success("添加成功", saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteRecord(@PathVariable Long id) {
        trainingRecordService.deleteRecord(id);
        return ResponseEntity.ok(ApiResponse.success("删除成功"));
    }
}
