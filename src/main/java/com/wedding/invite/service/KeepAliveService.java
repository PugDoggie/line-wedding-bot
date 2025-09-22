package com.wedding.invite.service;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class KeepAliveService {

    private final OkHttpClient client = new OkHttpClient();

    @Scheduled(fixedRate = 300000) // 每 5 分鐘執行一次
    public void pingWebhook() {
        String url = "https://line-wedding-bot-3.onrender.com/ping"; // ✅ 建議使用 /ping endpoint

        Request request = new Request.Builder()
            .url(url)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            System.out.println("保活 ping 成功：" + response.code());
        } catch (Exception e) {
            System.err.println("保活 ping 失敗：" + e.getMessage());
        }
    }
}