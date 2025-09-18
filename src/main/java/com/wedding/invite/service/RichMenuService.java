package com.wedding.invite.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class RichMenuService {

    @Value("${LINE_CHANNEL_TOKEN}")
    private String channelAccessToken;

    private final OkHttpClient client = new OkHttpClient();

    public void createMenu() throws Exception {
        ObjectMapper mapper = new ObjectMapper();

        Map<String, Object> payload = Map.of(
            "size", Map.of("width", 2500, "height", 843),
            "selected", true,
            "name", "WeddingMenu",
            "chatBarText", "婚禮選單",
            "areas", List.of(
                Map.of("bounds", Map.of("x", 0, "y", 0, "width", 625, "height", 843),
                       "action", Map.of("type", "uri", "uri", "https://yourdomain.com/map")),
                Map.of("bounds", Map.of("x", 625, "y", 0, "width", 625, "height", 843),
                       "action", Map.of("type", "uri", "uri", "https://yourdomain.com/bless")),
                Map.of("bounds", Map.of("x", 1250, "y", 0, "width", 625, "height", 843),
                       "action", Map.of("type", "uri", "uri", "https://yourdomain.com/photos")),
                Map.of("bounds", Map.of("x", 1875, "y", 0, "width", 625, "height", 843),
                       "action", Map.of("type", "uri", "uri", "https://yourdomain.com/video"))
            )
        );

        String json = mapper.writeValueAsString(payload);

        Request request = new Request.Builder()
            .url("https://api.line.me/v2/bot/richmenu")
            .addHeader("Authorization", "Bearer " + channelAccessToken)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(json, MediaType.get("application/json")))
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Rich Menu 建立失敗：" + response.code());
            }
            System.out.println("Rich Menu 建立成功：" + response.body().string());
        }
    }
}