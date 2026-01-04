package com.fitness.service;

import com.fitness.entity.User;
import com.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户Service
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 获取用户信息
     */
    public User getUserProfile(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
    }

    /**
     * 更新用户信息
     */
    @Transactional
    public void updateUserProfile(Long userId, User updateData) {
        User user = getUserProfile(userId);

        // 无条件更新，允许清空字段
        user.setEmail(updateData.getEmail());
        user.setGender(updateData.getGender());
        user.setAge(updateData.getAge());
        user.setHeight(updateData.getHeight());

        userRepository.save(user);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = getUserProfile(userId);

        // 验证旧密码
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("旧密码不正确");
        }

        // 验证新密码强度（至少6位）
        if (newPassword.length() < 6) {
            throw new RuntimeException("新密码长度不能少于6位");
        }

        // 加密并保存新密码
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}
