package com.wedding.invite.service;

import com.wedding.invite.model.Blessing;
import com.wedding.invite.repository.BlessingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlessingService {

    @Autowired
    private BlessingRepository blessingRepository;

    public void saveBlessing(String userId, String message) {
        String userName = getUserNameFromUserId(userId);
        Blessing blessing = new Blessing(userId, userName, message, LocalDateTime.now());
        blessingRepository.save(blessing);
    }

    public List<Blessing> getBlessings() {
        return blessingRepository.findTop5ByOrderByCreatedAtDesc();
    }

    private String getUserNameFromUserId(String userId) {
        return "匿名祝福者" + userId.substring(userId.length() - 4);
    }
}