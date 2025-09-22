package com.wedding.invite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling; // ✅ 加入這行

@SpringBootApplication
@EnableScheduling // ✅ 啟用排程功能
@EnableJpaRepositories(basePackages = "com.wedding.invite.repository")
@EntityScan(basePackages = "com.wedding.invite.model")
public class LineWeddingBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(LineWeddingBotApplication.class, args);
    }
}