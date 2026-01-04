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
        if (bodyData.getWeight() == null) {
            throw new RuntimeException("体重不能为空");
        }

        if (bodyData.getWeight() <= 0 || bodyData.getWeight() > 500) {
            throw new RuntimeException("体重数据不合理");
        }

        // 获取用户信息
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));

        // 计算BMI
        Double bmi = null;
        if (user.getHeight() != null && bodyData.getWeight() != null) {
            bmi = bodyData.getWeight() / Math.pow(user.getHeight() / 100, 2);
            bmi = Math.round(bmi * 100.0) / 100.0;
        }

        // 如果用户未输入体脂率，则自动计算
        if (bodyData.getBodyFat() == null && bmi != null && user.getAge() != null && user.getGender() != null) {
            Double bodyFat = calculateBodyFat(bmi, user.getAge(), user.getGender());
            bodyData.setBodyFat(bodyFat);
        }

        // 验证体脂率（如果有值）
        if (bodyData.getBodyFat() != null && (bodyData.getBodyFat() < 0 || bodyData.getBodyFat() > 100)) {
            throw new RuntimeException("体脂率数据不合理");
        }

        bodyData.setUserId(userId);
        bodyData.setBmi(bmi);
        bodyDataRepository.save(bodyData);

        Map<String, Object> result = new HashMap<>();
        result.put("message", "身体数据添加成功");
        result.put("bmi", bmi);
        result.put("bodyFat", bodyData.getBodyFat());
        result.put("isAutoCalculated", bodyData.getBodyFat() != null);
        return result;
    }

    /**
     * 根据BMI、年龄、性别计算体脂率
     * 男性：体脂率% = 1.20 × BMI + 0.23 × 年龄 - 16.2
     * 女性：体脂率% = 1.20 × BMI + 0.23 × 年龄 - 5.4
     */
    private Double calculateBodyFat(Double bmi, Integer age, String gender) {
        double bodyFat;
        if ("男".equals(gender) || "male".equalsIgnoreCase(gender)) {
            bodyFat = 1.20 * bmi + 0.23 * age - 16.2;
        } else {
            bodyFat = 1.20 * bmi + 0.23 * age - 5.4;
        }
        // 确保体脂率在合理范围内
        bodyFat = Math.max(5.0, Math.min(50.0, bodyFat));
        // 保留两位小数
        return Math.round(bodyFat * 100.0) / 100.0;
    }

    /**
     * 获取身体数据历史
     */
    public List<BodyData> getBodyDataHistory(Long userId) {
        return bodyDataRepository.findTop30ByUserIdOrderByRecordDateDesc(userId);
    }
}
