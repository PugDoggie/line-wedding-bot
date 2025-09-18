package com.wedding.invite;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.wedding.invite.repository")
@EntityScan(basePackages = "com.wedding.invite.model")
public class LineWeddingBotApplication {
    public static void main(String[] args) {
        SpringApplication.run(LineWeddingBotApplication.class, args);
    }
}