package com.fitness.controller;

import com.fitness.dto.ApiResponse;
import com.fitness.entity.BodyData;
import com.fitness.service.BodyDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 身体数据Controller
 */
@RestController
@RequestMapping("/body-data")
public class BodyDataController {

    @Autowired
    private BodyDataService bodyDataService;

    /**
     * 添加身体数据
     */
    @PostMapping
    public ResponseEntity<?> addBodyData(
            Authentication authentication,
            @RequestBody BodyData bodyData) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            Map<String, Object> result = bodyDataService.addBodyData(userId, bodyData);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取身体数据历史
     */
    @GetMapping
    public ResponseEntity<List<BodyData>> getBodyDataHistory(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            List<BodyData> data = bodyDataService.getBodyDataHistory(userId);
            return ResponseEntity.ok(data);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
