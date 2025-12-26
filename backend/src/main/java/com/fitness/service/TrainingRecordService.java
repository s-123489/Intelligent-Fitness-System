package com.fitness.service;

import com.fitness.entity.TrainingRecord;
import com.fitness.repository.TrainingRecordRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class TrainingRecordService {

    @Autowired
    private TrainingRecordRepository trainingRecordRepository;

    public List<TrainingRecord> getRecordsByUserId(Long userId) {
        return trainingRecordRepository.findByUserIdOrderByRecordDateDesc(userId);
    }

    public List<TrainingRecord> getRecentRecords(Long userId, int days) {
        LocalDate startDate = LocalDate.now().minusDays(days);
        return trainingRecordRepository.findByUserIdAndRecordDateAfterOrderByRecordDateDesc(userId, startDate);
    }

    public TrainingRecord saveRecord(TrainingRecord record) {
        return trainingRecordRepository.save(record);
    }

    public void deleteRecord(Long id) {
        trainingRecordRepository.deleteById(id);
    }

    public Map<String, Object> getStats(Long userId) {
        List<TrainingRecord> records = trainingRecordRepository.findByUserId(userId);
        
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalRecords", records.size());
        stats.put("totalDuration", records.stream().mapToInt(r -> r.getDuration() != null ? r.getDuration() : 0).sum());
        stats.put("totalCalories", records.stream().mapToDouble(r -> r.getCalories() != null ? r.getCalories() : 0).sum());
        
        // 计算连续打卡天数
        int streak = calculateStreak(records);
        stats.put("streak", streak);
        
        return stats;
    }

    private int calculateStreak(List<TrainingRecord> records) {
        if (records.isEmpty()) return 0;
        
        LocalDate today = LocalDate.now();
        LocalDate currentDate = today;
        int streak = 0;
        
        for (TrainingRecord record : records) {
            if (record.getRecordDate().equals(currentDate)) {
                streak++;
                currentDate = currentDate.minusDays(1);
            } else if (record.getRecordDate().isBefore(currentDate)) {
                break;
            }
        }
        
        return streak;
    }
}
