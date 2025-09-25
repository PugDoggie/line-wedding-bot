package com.wedding.invite.service;

import com.wedding.invite.model.Blessing;
import com.wedding.invite.repository.BlessingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlessingService {

    private static final Logger logger = LoggerFactory.getLogger(BlessingService.class);

    @Autowired
    private BlessingRepository blessingRepository;

    public void saveBlessing(String userId, String message) {
        if (userId == null || userId.length() < 4) {
            logger.warn("無效的 userId，略過儲存");
            return;
        }

        String userName = getUserNameFromUserId(userId);
        Blessing blessing = new Blessing(userId, userName, message, LocalDateTime.now());
        blessingRepository.save(blessing);
        logger.info("✅ 儲存祝福成功：userId={}, name={}, message={}", userId, userName, message);
    }

    public List<Blessing> getBlessings() {
        return blessingRepository.findTop5ByOrderByCreatedAtDesc();
    }

    private String getUserNameFromUserId(String userId) {
        return "匿名祝福者" + userId.substring(userId.length() - 4);
    }
}