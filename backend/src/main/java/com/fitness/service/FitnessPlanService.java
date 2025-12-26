package com.fitness.service;

import com.fitness.entity.FitnessPlan;
import com.fitness.entity.User;
import com.fitness.repository.FitnessPlanRepository;
import com.fitness.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FitnessPlanService {

    @Autowired
    private FitnessPlanRepository fitnessPlanRepository;
    
    @Autowired
    private UserRepository userRepository;

    public List<FitnessPlan> getPlansByUserId(Long userId) {
        return fitnessPlanRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public FitnessPlan savePlan(FitnessPlan plan) {
        return fitnessPlanRepository.save(plan);
    }

    public void deletePlan(Long id) {
        fitnessPlanRepository.deleteById(id);
    }

    /**
     * AI生成健身计划
     */
    public FitnessPlan generateAIPlan(Long userId, String goal, Double bodyFat, Double bmi) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("用户不存在"));
        
        FitnessPlan plan = new FitnessPlan();
        plan.setUserId(userId);
        plan.setGoal(goal);
        plan.setIsAiGenerated(true);
        
        // 根据目标设置计划名称和难度
        switch (goal) {
            case "减脂":
                plan.setPlanName("AI智能减脂计划");
                plan.setDifficulty(bodyFat > 25 ? "初级" : "中级");
                plan.setDuration(12);
                plan.setDescription("专为减脂目标定制的科学训练计划，结合有氧和力量训练");
                plan.setPlanContent(generateFatLossPlan(user, bodyFat));
                break;
            case "增肌":
                plan.setPlanName("AI智能增肌计划");
                plan.setDifficulty(bmi < 20 ? "初级" : "中级");
                plan.setDuration(16);
                plan.setDescription("系统化增肌训练，注重肌肉围度和力量提升");
                plan.setPlanContent(generateMuscleBuildingPlan(user, bmi));
                break;
            case "塑形":
                plan.setPlanName("AI智能塑形计划");
                plan.setDifficulty("中级");
                plan.setDuration(12);
                plan.setDescription("改善身体线条，打造理想体型");
                plan.setPlanContent(generateShapingPlan(user));
                break;
            default:
                plan.setPlanName("AI智能保持计划");
                plan.setDifficulty("初级");
                plan.setDuration(8);
                plan.setDescription("维持当前体型，保持健康状态");
                plan.setPlanContent(generateMaintenancePlan(user));
        }
        
        return fitnessPlanRepository.save(plan);
    }

    private String generateFatLossPlan(User user, Double bodyFat) {
        return "第1-4周：基础有氧+核心\n" +
               "- 周一/三/五：快走/慢跑 30分钟 + 核心训练15分钟\n" +
               "- 周二/四/六：力量训练 40分钟（上肢/下肢/全身循环）\n\n" +
               "第5-8周：进阶有氧+力量\n" +
               "- 周一/三/五：HIIT训练 25分钟 + 拉伸\n" +
               "- 周二/四/六：复合动作力量训练 45分钟\n\n" +
               "第9-12周：冲刺期\n" +
               "- 周一/三/五：高强度间歇 30分钟\n" +
               "- 周二/四/六：循环力量训练 50分钟\n" +
               "- 周日：活动性恢复（瑜伽/游泳）";
    }

    private String generateMuscleBuildingPlan(User user, Double bmi) {
        return "第1-4周：力量基础建立\n" +
               "- 周一：胸+三头（卧推、飞鸟、臂屈伸）\n" +
               "- 周三：背+二头（引体、划船、弯举）\n" +
               "- 周五：腿+肩（深蹲、推举、侧平举）\n\n" +
               "第5-12周：肌肥大训练\n" +
               "- 周一：胸部专项（4-5个动作，每个4组8-12次）\n" +
               "- 周二：背部专项\n" +
               "- 周四：腿部专项\n" +
               "- 周五：肩臂专项\n\n" +
               "第13-16周：力量提升\n" +
               "- 采用5x5训练法\n" +
               "- 主打复合动作（深蹲、卧推、硬拉）";
    }

    private String generateShapingPlan(User user) {
        return "全身塑形8周计划\n\n" +
               "周一：上肢塑形\n" +
               "- 哑铃推举 3x12\n" +
               "- 俯卧撑 3x15\n" +
               "- 三头下压 3x15\n\n" +
               "周三：核心+臀腿\n" +
               "- 深蹲 4x15\n" +
               "- 臀桥 3x20\n" +
               "- 平板支撑 3x60秒\n\n" +
               "周五：全身循环\n" +
               "- 波比跳 3x10\n" +
               "- 箭步蹲 3x12（每侧）\n" +
               "- 卷腹 3x20";
    }

    private String generateMaintenancePlan(User user) {
        return "健康保持计划\n\n" +
               "每周3-4次训练：\n" +
               "- 有氧运动20-30分钟（跑步/骑行/游泳）\n" +
               "- 基础力量训练20分钟\n" +
               "- 拉伸放松10分钟\n\n" +
               "推荐动作：\n" +
               "- 深蹲、俯卧撑、引体向上\n" +
               "- 平板支撑、卷腹\n" +
               "- 瑜伽或普拉提";
    }
}
