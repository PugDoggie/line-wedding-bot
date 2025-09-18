package com.wedding.invite.service;

import okhttp3.*;
import org.springframework.stereotype.Service;

@Service
public class RichMenuService {

    private static final String CHANNEL_ACCESS_TOKEN = "你的 Channel Access Token";

    public void createMenu() throws Exception {
        OkHttpClient client = new OkHttpClient();

        String json = "{"
            + "\"size\":{\"width\":2500,\"height\":843},"
            + "\"selected\":true,"
            + "\"name\":\"WeddingMenu\","
            + "\"chatBarText\":\"婚禮選單\","
            + "\"areas\":["
            + "{\"bounds\":{\"x\":0,\"y\":0,\"width\":625,\"height\":843},\"action\":{\"type\":\"uri\",\"uri\":\"https://yourdomain.com/map\"}},"
            + "{\"bounds\":{\"x\":625,\"y\":0,\"width\":625,\"height\":843},\"action\":{\"type\":\"uri\",\"uri\":\"https://yourdomain.com/bless\"}},"
            + "{\"bounds\":{\"x\":1250,\"y\":0,\"width\":625,\"height\":843},\"action\":{\"type\":\"uri\",\"uri\":\"https://yourdomain.com/photos\"}},"
            + "{\"bounds\":{\"x\":1875,\"y\":0,\"width\":625,\"height\":843},\"action\":{\"type\":\"uri\",\"uri\":\"https://yourdomain.com/video\"}}"
            + "]"
            + "}";

        Request request = new Request.Builder()
            .url("https://api.line.me/v2/bot/richmenu")
            .addHeader("Authorization", "Bearer " + CHANNEL_ACCESS_TOKEN)
            .addHeader("Content-Type", "application/json")
            .post(RequestBody.create(json, MediaType.get("application/json")))
            .build();

        Response response = client.newCall(request).execute();
        System.out.println("Rich Menu 建立結果：" + response.body().string());
    }
}