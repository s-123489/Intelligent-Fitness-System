package com.fitness.service;

import com.fitness.entity.BodyData;
import com.fitness.entity.User;
import com.fitness.repository.BodyDataRepository;
import com.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 身体数据Service
 */
@Service
public class BodyDataService {

    @Autowired
    private BodyDataRepository bodyDataRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 添加身体数据
     */
    @Transactional
    public Map<String, Object> addBodyData(Long userId, BodyData bodyData) {
        // 参数验证
        if (bodyData.getWeight() == null || bodyData.getBodyFat() == null) {
            throw new RuntimeException("体重和体脂率不能为空");
        }

        if (bodyData.getWeight() <= 0 || bodyData.getWeight() > 500) {
            throw new RuntimeException("体重数据不合理");
        }

        if (bodyData.getBodyFat() < 0 || bodyData.getBodyFat() > 100) {
            throw new RuntimeException("体脂率数据不合理");
        }

        // 获取用户身高计算BMI
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        Double bmi = null;
        if (user.getHeight() != null && bodyData.getWeight() != null) {
            bmi = bodyData.getWeight() / Math.pow(user.getHeight() / 100, 2);
            bmi = Math.round(bmi * 100.0) / 100.0;
        }

        bodyData.setUserId(userId);
        bodyData.setBmi(bmi);
        bodyDataRepository.save(bodyData);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "身体数据添加成功");
        result.put("bmi", bmi);
        return result;
    }

    /**
     * 获取身体数据历史
     */
    public List<BodyData> getBodyDataHistory(Long userId) {
        return bodyDataRepository.findTop30ByUserIdOrderByRecordDateDesc(userId);
    }
}
