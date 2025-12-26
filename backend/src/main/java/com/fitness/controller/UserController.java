package com.fitness.controller;

import com.fitness.dto.ApiResponse;
import com.fitness.entity.User;
import com.fitness.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

/**
 * 用户Controller
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping("/profile")
    public ResponseEntity<User> getProfile(Authentication authentication) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            User user = userService.getUserProfile(userId);
            // 不返回密码
            user.setPassword(null);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 更新用户信息
     */
    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<Void>> updateProfile(
            Authentication authentication,
            @RequestBody User updateData) {
        try {
            Long userId = (Long) authentication.getPrincipal();
            userService.updateUserProfile(userId, updateData);
            return ResponseEntity.ok(ApiResponse.success("更新成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(e.getMessage()));
        }
    }
}
