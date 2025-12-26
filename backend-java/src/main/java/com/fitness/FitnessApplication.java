package com.fitness;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * 智能健身管理系统主应用类
 *
 * @author Fitness Team
 * @version 1.0
 */
@SpringBootApplication
@EnableJpaAuditing
public class FitnessApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnessApplication.class, args);
        System.out.println("\n========================================");
        System.out.println("智能健身管理系统启动成功！");
        System.out.println("API地址: http://localhost:5000/api");
        System.out.println("H2控制台: http://localhost:5000/api/h2-console");
        System.out.println("========================================\n");
    }
}
