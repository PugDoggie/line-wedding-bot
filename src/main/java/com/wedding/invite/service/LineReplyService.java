package com.wedding.invite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wedding.invite.model.Blessing;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class LineReplyService {

    @Value("${LINE_CHANNEL_TOKEN}")
    private String channelAccessToken;

    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    // 1️⃣ 基本文字回覆（含備援推播）
    public void replyToUser(String replyToken, String userId, String messageText) {
        try {
            Map<String, Object> payload = Map.of(
                "replyToken", replyToken,
                "messages", List.of(Map.of("type", "text", "text", messageText))
            );
            sendReply(payload);
        } catch (Exception e) {
            System.err.println("replyToken 回覆失敗，改用 pushMessage：" + e.getMessage());
            pushMessage(userId, messageText);
        }
    }

    // 2️⃣ 快速回覆選單（含備援推播）
    public void replyWithQuickReply(String replyToken, String userId, String messageText) {
        try {
            Map<String, Object> quickReply = Map.of(
                "items", List.of(
                    Map.of("type", "action", "action", Map.of("type", "message", "label", "地點", "text", "地點")),
                    Map.of("type", "action", "action", Map.of("type", "message", "label", "時間", "text", "時間")),
                    Map.of("type", "action", "action", Map.of("type", "message", "label", "報名", "text", "報名")),
                    Map.of("type", "action", "action", Map.of("type", "message", "label", "祝福牆", "text", "祝福牆"))
                )
            );

            Map<String, Object> message = Map.of(
                "type", "text",
                "text", messageText,
                "quickReply", quickReply
            );

            Map<String, Object> payload = Map.of(
                "replyToken", replyToken,
                "messages", List.of(message)
            );

            sendReply(payload);
        } catch (Exception e) {
            System.err.println("快速回覆失敗，改用 pushMessage：" + e.getMessage());
            pushMessage(userId, messageText);
        }
    }

    // 3️⃣ Flex Message：祝福牆展示（含備援推播）
    public void replyWithBlessingFlex(String replyToken, String userId, List<Blessing> blessings) {
        try {
            List<Map<String, Object>> contents = blessings.stream()
                .limit(5)
                .map(b -> {
                    Map<String, Object> item = new HashMap<>();
                    item.put("type", "text");
                    item.put("text", "來自 " + b.getName() + "： " + b.getMessage());
                    item.put("wrap", true);
                    return item;
                })
                .collect(Collectors.toList());

            Map<String, Object> bubble = Map.of(
                "type", "bubble",
                "body", Map.of(
                    "type", "box",
                    "layout", "vertical",
                    "contents", contents
                )
            );

            Map<String, Object> message = Map.of(
                "type", "flex",
                "altText", "祝福牆",
                "contents", bubble
            );

            Map<String, Object> payload = Map.of(
                "replyToken", replyToken,
                "messages", List.of(message)
            );

            sendReply(payload);
        } catch (Exception e) {
            System.err.println("Flex 回覆失敗，改用 pushMessage：" + e.getMessage());
            pushMessage(userId, "目前無法載入祝福牆，請稍後再試 🙏");
        }
    }

    // 4️⃣ 主動推播訊息（不依賴 replyToken）
    public void pushMessage(String userId, String messageText) {
        try {
            Map<String, Object> message = Map.of(
                "type", "text",
                "text", messageText
            );

            Map<String, Object> payload = Map.of(
                "to", userId,
                "messages", List.of(message)
            );

            String json = mapper.writeValueAsString(payload);

            Request request = new Request.Builder()
                .url("https://api.line.me/v2/bot/message/push")
                .addHeader("Authorization", "Bearer " + channelAccessToken)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.get("application/json")))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Push 訊息失敗：" + response.body().string());
                } else {
                    System.out.println("Push 訊息成功：" + response.body().string());
                }
            }
        } catch (Exception e) {
            System.err.println("Push 處理錯誤：" + e.getMessage());
        }
    }
 // ✅ 推送快速選單（用於 replyToken 無效時）
    public void pushQuickReply(String userId, String messageText) {
        try {
            Map<String, Object> quickReply = Map.of(
                "items", List.of(
                    Map.of("type", "action", "action", Map.of("type", "message", "label", "地點", "text", "地點")),
                    Map.of("type", "action", "action", Map.of("type", "message", "label", "時間", "text", "時間")),
                    Map.of("type", "action", "action", Map.of("type", "message", "label", "報名", "text", "報名")),
                    Map.of("type", "action", "action", Map.of("type", "message", "label", "祝福牆", "text", "祝福牆"))
                )
            );

            Map<String, Object> message = Map.of(
                "type", "text",
                "text", messageText,
                "quickReply", quickReply
            );

            Map<String, Object> payload = Map.of(
                "to", userId,
                "messages", List.of(message)
            );

            String json = mapper.writeValueAsString(payload);

            Request request = new Request.Builder()
                .url("https://api.line.me/v2/bot/message/push")
                .addHeader("Authorization", "Bearer " + channelAccessToken)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(json, MediaType.get("application/json")))
                .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("Push QuickReply 失敗：" + response.body().string());
                } else {
                    System.out.println("Push QuickReply 成功：" + response.body().string());
                }
            }
        } catch (Exception e) {
            System.err.println("Push QuickReply 處理錯誤：" + e.getMessage());
        }
    }

    // 5️⃣ 共用方法：送出 LINE 回覆
    private void sendReply(Map<String, Object> payload) throws Exception {
        String json = mapper.writeValueAsString(payload);

        Request request = new Request.Builder()
            .url("https://api.line.me/v2/bot/message/reply")
            .addHeader("Authorization", "Bearer " + channelAccessToken)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(json, MediaType.get("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("LINE API 回覆失敗：" + response.code() + ", Body: " + response.body().string());
            }
            System.out.println("LINE 回覆成功：" + response.body().string());
        }
    }
}
