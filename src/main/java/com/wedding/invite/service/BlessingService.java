package com.wedding.invite.service;

import com.wedding.invite.model.Blessing;
import com.wedding.invite.repository.BlessingRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class BlessingService {

    private static final Logger logger = LoggerFactory.getLogger(BlessingService.class);

    @Value("${line.channel.token}")
    private String channelAccessToken;

    @Autowired
    private BlessingRepository blessingRepository;

    public void saveBlessing(String userId, String message) {
        if (userId == null || userId.length() < 4) {
            logger.warn("無效的 userId，略過儲存");
            return;
        }

        String userName = getDisplayName(userId);
        Blessing blessing = new Blessing(userId, userName, message, LocalDateTime.now());
        blessingRepository.save(blessing);
        logger.info("✅ 儲存祝福成功：userId={}, name={}, message={}", userId, userName, message);
    }

    public List<Blessing> getBlessings() {
        return blessingRepository.findTop5ByOrderByCreatedAtDesc();
    }

    private String getDisplayName(String userId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.line.me/v2/bot/profile/" + userId))
                .header("Authorization", "Bearer " + channelAccessToken)
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            return json.getString("displayName");
        } catch (Exception e) {
            logger.warn("取得使用者名稱失敗，使用匿名名稱：{}", e.getMessage());
            return "匿名祝福者" + userId.substring(userId.length() - 4);
        }
    }
}