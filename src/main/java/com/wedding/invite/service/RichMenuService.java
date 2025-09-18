package com.wedding.invite.service;

import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RichMenuService {

    @Value("${LINE_CHANNEL_TOKEN}")
    private String channelAccessToken;

    public void createMenu() throws Exception {
        OkHttpClient client = new OkHttpClient();

        String json = "{" +
            "\"size\":{\"width\":2500,\"height\":843}," +
            "\"selected\":true," +
            "\"name\":\"WeddingMenu\"," +
            "\"chatBarText\":\"婚禮選單\"," +
            "\"areas\":[" +
            "{\"bounds\":{\"x\":0,\"y\":0,\"width\":625,\"height\":843},\"action\":{\"type\":\"uri\",\"uri\":\"https://yourdomain.com/map\"}}," +
            "{\"bounds\":{\"x\":625,\"y\":0,\"width\":625,\"height\":843},\"action\":{\"type\":\"uri\",\"uri\":\"https://yourdomain.com/bless\"}}," +
            "{\"bounds\":{\"x\":1250,\"y\":0,\"width\":625,\"height\":843},\"action\":{\"type\":\"uri\",\"uri\":\"https://yourdomain.com/photos\"}}," +
            "{\"bounds\":{\"x\":1875,\"y\":0,\"width\":625,\"height\":843},\"action\":{\"type\":\"uri\",\"uri\":\"https://yourdomain.com/video\"}}" +
            "]" +
            "}";

        Request request = new Request.Builder()
            .url("https://api.line.me/v2/bot/richmenu")
            .addHeader("Authorization", "Bearer " + channelAccessToken)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(json, MediaType.get("application/json")))
            .build();

        Response response = client.newCall(request).execute();
        if (!response.isSuccessful()) {
            throw new RuntimeException("Rich Menu 建立失敗：" + response.code());
        }

        System.out.println("Rich Menu 建立成功：" + response.body().string());
    }
}