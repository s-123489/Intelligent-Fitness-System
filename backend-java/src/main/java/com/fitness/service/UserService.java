package com.fitness.service;

import com.fitness.entity.User;
import com.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 用户Service
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

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

        if (updateData.getEmail() != null) {
            user.setEmail(updateData.getEmail());
        }
        if (updateData.getGender() != null) {
            user.setGender(updateData.getGender());
        }
        if (updateData.getAge() != null) {
            user.setAge(updateData.getAge());
        }
        if (updateData.getHeight() != null) {
            user.setHeight(updateData.getHeight());
        }

        userRepository.save(user);
    }
}
