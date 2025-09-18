package com.wedding.invite.model;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
public class Blessing {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String message;

    public Blessing(String userId, String userName, String message2, LocalDateTime now) {
		// TODO Auto-generated constructor stub
	}
	public Long getId() { return id; }
    public String getName() { return name; }
    public String getMessage() { return message; }

    public void setId(Long id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setMessage(String message) { this.message = message; }
}