package com.wedding.invite.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Blessing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId;
    private String name;
    private String message;
    private LocalDateTime createdAt;

    public Blessing() {}

    public Blessing(String userId, String name, String message, LocalDateTime createdAt) {
        this.userId = userId;
        this.name = name;
        this.message = message;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public String getUserId() { return userId; }
    public String getName() { return name; }
    public String getMessage() { return message; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setUserId(String userId) { this.userId = userId; }
    public void setName(String name) { this.name = name; }
    public void setMessage(String message) { this.message = message; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}