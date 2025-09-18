package com.wedding.invite.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class LineReplyService {

    @Value("${LINE_CHANNEL_TOKEN}")
    private String channelAccessToken;

    public void replyToUser(String replyToken, String messageText) throws Exception {
        OkHttpClient client = new OkHttpClient();

        String json = "{" +
            "\"replyToken\":\"" + replyToken + "\"," +
            "\"messages\":[{\"type\":\"text\",\"text\":\"" + messageText + "\"}]" +
            "}";

        Request request = new Request.Builder()
            .url("https://api.line.me/v2/bot/message/reply")
            .addHeader("Authorization", "Bearer " + channelAccessToken)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(json, MediaType.get("application/json")))
            .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("LINE API 回覆失敗：" + response.code());
        }

        System.out.println("LINE 回覆成功：" + response.body().string());
    }
}