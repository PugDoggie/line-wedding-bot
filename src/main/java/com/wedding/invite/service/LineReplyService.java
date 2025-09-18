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

    // 1️⃣ 基本文字回覆
    public void replyToUser(String replyToken, String messageText) throws Exception {
        Map<String, Object> payload = Map.of(
            "replyToken", replyToken,
            "messages", List.of(Map.of("type", "text", "text", messageText))
        );
        sendReply(payload);
    }

    // 2️⃣ 快速回覆選單
    public void replyWithQuickReply(String replyToken, String messageText) throws Exception {
        Map<String, Object> quickReply = Map.of(
            "items", List.of(
                Map.of("type", "action", "action", Map.of("type", "message", "label", "地點", "text", "地點")),
                Map.of("type", "action", "action", Map.of("type", "message", "label", "報名", "text", "報名")),
                Map.of("type", "action", "action", Map.of("type", "message", "label", "祝福", "text", "祝福"))
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
    }

    // 3️⃣ Flex Message：祝福牆展示
    public void replyWithBlessingFlex(String replyToken, List<Blessing> blessings) throws Exception {
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
    }

    // 共用方法：送出 LINE 回覆
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
                throw new RuntimeException("LINE API 回覆失敗：" + response.code());
            }
            System.out.println("LINE 回覆成功：" + response.body().string());
        }
    }
}