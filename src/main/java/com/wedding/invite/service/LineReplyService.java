package com.wedding.invite.service;

import okhttp3.*;
import org.springframework.stereotype.Service;

@Service
public class LineReplyService {

    private static final String CHANNEL_ACCESS_TOKEN = "0BZkNjdn+cfrhcB8dlfb3jheVZWoPlNZg6d/hNagiCUeeal3VP/NfU6QCESo9zZoUEi9zI/vqz2SducnhCoIZkT83p+jMdKZenAhbd7703dOumQ8Dj4PXIkxVX5cjIDtZzFqaF6QagvoXOaS796zgAdB04t89/1O/w1cDnyilFU=";

    public void replyToUser(String replyToken, String messageText) throws Exception {
        OkHttpClient client = new OkHttpClient();

        String json = "{"
            + "\"replyToken\":\"" + replyToken + "\","
            + "\"messages\":[{\"type\":\"text\",\"text\":\"" + messageText + "\"}]"
            + "}";

        Request request = new Request.Builder()
            .url("https://api.line.me/v2/bot/message/reply")
            .addHeader("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(json, MediaType.get("application/json")))
            .build();

        Response response = client.newCall(request).execute();
        System.out.println("LINE 回覆結果：" + response.body().string());
    }
}