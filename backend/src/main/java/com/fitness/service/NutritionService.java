package com.fitness.service;

import com.fitness.entity.User;
import com.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class NutritionService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 获取营养建议
     */
    public Map<String, Object> getRecommendation(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        if (user.getAge() == null || user.getHeight() == null) {
            throw new RuntimeException("请先完善个人资料（年龄、身高）");
        }
        
        // 假设体重，如果没有可以从body_data表获取最新的
        double weight = 70.0; // 默认值
        
        // 计算BMR（基础代谢率）
        double bmr = calculateBMR(user.getGender(), weight, user.getHeight(), user.getAge());
        
        // 计算总热量消耗（TDEE）考虑活动系数
        double tdee = bmr * 1.5; // 中等活动水平
        
        // 根据目标调整热量
        double targetCalories = tdee;
        String goal = "保持"; // 默认目标
        
        Map<String, Object> recommendation = new HashMap<>();
        recommendation.put("calories", Math.round(targetCalories));
        recommendation.put("protein", Math.round(weight * 1.6)); // 每公斤体重1.6g蛋白质
        recommendation.put("carbs", Math.round(targetCalories * 0.5 / 4)); // 50%来自碳水，1g碳水=4卡
        recommendation.put("fat", Math.round(targetCalories * 0.25 / 9)); // 25%来自脂肪，1g脂肪=9卡
        recommendation.put("water", Math.round(weight * 35)); // 每公斤体重35ml水
        recommendation.put("meals", generateMealPlan(goal));
        
        return recommendation;
    }

    /**
     * 计算基础代谢率BMR
     * 男性: BMR = 88.362 + (13.397 × 体重kg) + (4.799 × 身高cm) - (5.677 × 年龄)
     * 女性: BMR = 447.593 + (9.247 × 体重kg) + (3.098 × 身高cm) - (4.330 × 年龄)
     */
    private double calculateBMR(String gender, double weight, double height, int age) {
        if ("男".equals(gender)) {
            return 88.362 + (13.397 * weight) + (4.799 * height) - (5.677 * age);
        } else {
            return 447.593 + (9.247 * weight) + (3.098 * height) - (4.330 * age);
        }
    }

    /**
     * 生成推荐食谱
     */
    private Map<String, String[]> generateMealPlan(String goal) {
        Map<String, String[]> meals = new HashMap<>();
        
        meals.put("breakfast", new String[]{
            "燕麦粥+鸡蛋+牛奶",
            "全麦面包+鸡胸肉+水果",
            "紫薯+煎蛋+豆浆"
        });
        
        meals.put("lunch", new String[]{
            "糙米饭+鸡胸肉+西兰花+番茄炒蛋",
            "全麦意面+牛肉+蔬菜沙拉",
            "藜麦饭+三文鱼+芦笋"
        });
        
        meals.put("dinner", new String[]{
            "红薯+虾仁+青菜",
            "玉米+鸡胸肉+蘑菇",
            "糙米粥+豆腐+蔬菜"
        });
        
        meals.put("snacks", new String[]{
            "坚果（杏仁、核桃）",
            "水果（苹果、香蕉）",
            "无糖酸奶",
            "蛋白棒"
        });
        
        return meals;
    }
}
