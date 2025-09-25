package com.wedding.invite.service;

import com.wedding.invite.model.Blessing;
import com.wedding.invite.repository.BlessingRepository;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

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

    private final BlessingRepository blessingRepository;

    public BlessingService(BlessingRepository blessingRepository) {
        this.blessingRepository = blessingRepository;
    }

    public void saveBlessing(String userId, String message) {
        logger.info("🔔 進入 saveBlessing()，userId={}, message={}", userId, message);

        if (userId == null || userId.length() < 4) {
            logger.warn("⚠️ 無效的 userId，略過儲存");
            return;
        }

        String userName = getDisplayName(userId);
        logger.info("👤 使用者名稱取得結果：{}", userName);

        try {
            Blessing blessing = new Blessing(userId, userName, message, LocalDateTime.now());
            blessingRepository.save(blessing);
            logger.info("✅ 儲存祝福成功：userId={}, name={}, message={}", userId, userName, message);
        } catch (Exception e) {
            logger.error("❌ 儲存祝福失敗：{}", e.getMessage(), e);
        }
    }

    public List<Blessing> getBlessingsByPage(int page, int size) {
        logger.info("📥 取得祝福留言分頁：page={}, size={}", page, size);
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Blessing> blessings = blessingRepository.findAll(pageable).getContent();
        logger.info("✅ 撈出祝福筆數：{}", blessings.size());
        return blessings;
    }

    private String getDisplayName(String userId) {
        logger.info("🌐 呼叫 LINE API 取得使用者名稱：{}", userId);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.line.me/v2/bot/profile/" + userId))
                .header("Authorization", "Bearer " + channelAccessToken)
                .build();

            HttpResponse<String> response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString());

            logger.info("📡 LINE API 回應狀態碼：{}", response.statusCode());
            logger.debug("📡 LINE API 回應內容：{}", response.body());

            if (response.statusCode() != 200) {
                logger.warn("⚠️ LINE API 回傳非 200，使用匿名名稱");
                return fallbackName(userId);
            }

            JSONObject json = new JSONObject(response.body());
            return json.getString("displayName");
        } catch (Exception e) {
            logger.error("❌ 取得使用者名稱失敗：{}", e.getMessage(), e);
            return fallbackName(userId);
        }
    }

    private String fallbackName(String userId) {
        String fallback = "匿名祝福者" + userId.substring(userId.length() - 4);
        logger.info("🕶 使用匿名名稱：{}", fallback);
        return fallback;
    }

    // ✅ 新增這段方法，讓 Controller 可以使用 getBlessings()
    public List<Blessing> getBlessings() {
        logger.info("📥 取得所有祝福留言");
        return blessingRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
    @Transactional
    public void deleteBlessingsByKeyword(String keyword) {
        logger.warn("⚠️ 即將刪除所有包含『{}』的祝福留言", keyword);
        blessingRepository.deleteByMessageContaining(keyword);
        logger.info("🧹 已刪除所有包含『{}』的留言", keyword);
    }

}