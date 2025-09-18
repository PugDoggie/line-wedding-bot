package com.wedding.invite.controller;

import com.wedding.invite.model.WebhookRequest;
import com.wedding.invite.model.LineEvent;
import com.wedding.invite.service.LineReplyService;
import com.wedding.invite.service.BlessingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class LineWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(LineWebhookController.class);

    private final LineReplyService replyService;
    private final BlessingService blessingService;

    // 使用建構子注入，讓 Spring 自動注入依賴
    @Autowired
    public LineWebhookController(LineReplyService replyService, BlessingService blessingService) {
        this.replyService = replyService;
        this.blessingService = blessingService;
    }

    @PostMapping("/callback")
    public ResponseEntity<String> handleWebhook(@RequestBody WebhookRequest request) {
        if (request.getEvents() == null || request.getEvents().isEmpty()) {
            return ResponseEntity.badRequest().body("No events found.");
        }

        for (LineEvent event : request.getEvents()) {
            String replyToken = event.getReplyToken();

            // 確保是文字訊息事件
            if ("message".equals(event.getType()) && event.getMessage() != null && "text".equals(event.getMessage().getType())) {
                String messageText = event.getMessage().getText().trim(); // 使用 trim() 避免空白影響判斷

                try {
                    // 根據訊息內容回覆不同內容
                    if ("地點".equals(messageText)) {
                        replyService.replyToUser(replyToken, "婚禮地點：彰化縣員林市員林大道一段298號 💒");
                    } else if ("時間".equals(messageText)) {
                        replyService.replyToUser(replyToken, "婚禮時間：2025年10月25日 中午12點30分 ⏰");
                    } else if ("報名".equals(messageText)) {
                        replyService.replyToUser(replyToken, "報名連結：https://forms.gle/ZtYcJVXMaLq7tPXn9 📝");
                    } else if (messageText.startsWith("祝福:")) { // 判斷是否為祝福留言，例如 "祝福:新婚快樂！"
                        String blessingMessage = messageText.substring(3).trim();
                        // 儲存祝福留言
                        blessingService.saveBlessing(event.getSource().getUserId(), blessingMessage);
                        replyService.replyToUser(replyToken, "感謝您的祝福！💖 您的留言已成功記錄。");
                    } else if ("祝福牆".equals(messageText)) { // 新增的指令：查看祝福牆
                        // 取得祝福列表並以 Flex Message 呈現
                        var blessings = blessingService.getBlessings();
                        replyService.replyWithBlessingFlex(replyToken, blessings);
                    } else {
                        // 預設回覆：使用快速回覆選單引導使用者
                        replyService.replyWithQuickReply(replyToken, "歡迎來到我們的婚禮邀請頁面！請選擇您想查詢的項目 😊");
                    }
                } catch (Exception e) {
                    logger.error("LINE API 回覆失敗, 原因: {}", e.getMessage(), e);
                }
            }
        }
        return ResponseEntity.ok("ok");
    }
}