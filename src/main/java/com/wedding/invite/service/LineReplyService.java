package com.wedding.invite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class LineReplyService {

    @Value("${LINE_CHANNEL_TOKEN}")
    private String channelAccessToken;

    // 單例 OkHttpClient，避免每次建立新連線
    private final OkHttpClient client = new OkHttpClient();

    public void replyToUser(String replyToken, String messageText) throws Exception {
        // 使用 Jackson 組裝 JSON
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> payload = Map.of(
            "replyToken", replyToken,
            "messages", List.of(Map.of("type", "text", "text", messageText))
        );
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