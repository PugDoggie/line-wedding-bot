package com.wedding.invite.controller;

import com.wedding.invite.model.LineEvent;
import com.wedding.invite.model.WebhookRequest;
import com.wedding.invite.service.BlessingService;
import com.wedding.invite.service.LineReplyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/callback")
public class LineWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(LineWebhookController.class);

    private final BlessingService blessingService;
    private final LineReplyService lineReplyService;

    @Autowired
    public LineWebhookController(BlessingService blessingService, LineReplyService lineReplyService) {
        this.blessingService = blessingService;
        this.lineReplyService = lineReplyService;
    }

    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody WebhookRequest request) {
        if (request == null || request.getEvents() == null || request.getEvents().isEmpty()) {
            logger.warn("收到空的 webhook 請求");
            return ResponseEntity.badRequest().body("No events found.");
        }

        logger.info("收到 LINE webhook 事件，共 {} 筆", request.getEvents().size());

        for (LineEvent event : request.getEvents()) {
            String replyToken = event.getReplyToken();
            String eventType = event.getType();

            if (event.getSource() == null) {
                logger.warn("事件缺少使用者資訊，略過處理");
                continue;
            }

            String userId = event.getSource().getUserId();

            // ✅ 使用者加入好友
            if (Objects.equals(eventType, "follow")) {
                try {
                    logger.info("使用者加入好友：{}", userId);
                    lineReplyService.pushQuickReply(userId,
                        "歡迎加入婚禮小管家 🎉 請選擇您想查詢的項目 😊\n若您想留言祝福牆，請輸入：祝福:您的祝福內容\n例如：祝福:新婚快樂，永浴愛河 💖");
                } catch (Exception e) {
                    logger.error("歡迎訊息推送失敗：{}", e.getMessage(), e);
                }
                continue;
            }

            // ✅ 處理訊息事件
            if (replyToken == null || event.getMessage() == null) {
                logger.warn("事件缺少必要欄位，略過處理");
                continue;
            }

            String messageType = event.getMessage().getType();
            String messageText = event.getMessage().getText();

            if (Objects.equals(eventType, "message")) {
                try {
                    // ✅ 處理文字訊息
                    if ("text".equals(messageType) && messageText != null) {
                        messageText = messageText.trim();
                        switch (messageText) {
                            case "地點":
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "婚禮地點：彰化縣員林市員林大道一段298號 💒");
                                continue;
                            case "時間":
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "婚禮時間：2025年10月25日 \n中午12點入場 12點30開席 ⏰");
                                continue;
                            case "報名":
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "報名連結：https://forms.gle/ZtYcJVXMaLq7tPXn9 📝\n請大家盡速填寫方便我們做統計呦!!");
                                continue;
                            case "祝福牆":
                                var blessings = blessingService.getBlessings();
                                if (blessings == null || blessings.isEmpty()) {
                                    lineReplyService.replyWithQuickReply(replyToken, userId,
                                        "目前還沒有祝福留言，快來成為第一位吧！🎉");
                                } else {
                                    lineReplyService.replyWithBlessingFlex(replyToken, userId, blessings);
                                    lineReplyService.pushQuickReply(userId,
                                        "歡迎來到我們的婚禮邀請頁面！請選擇您想查詢的項目 😊\n若您想留言祝福牆，請輸入：祝福:您的祝福內容\n例如：祝福:新婚快樂，永浴愛河 💖");
                                }
                                continue;
                            default:
                                if (messageText.startsWith("祝福:")) {
                                    String blessingMessage = messageText.substring(3).trim();
                                    if (!blessingMessage.isEmpty()) {
                                        blessingService.saveBlessing(userId, blessingMessage);
                                        lineReplyService.replyWithQuickReply(replyToken, userId,
                                            "感謝您的祝福！💖 您的留言已成功記錄。");
                                    } else {
                                        lineReplyService.replyWithQuickReply(replyToken, userId,
                                            "請輸入有效的祝福內容，例如：祝福:新婚快樂！");
                                    }
                                    continue;
                                }
                        }
                    }

                    // ✅ 所有非文字訊息類型（貼圖、圖片、影片、語音、位置等）
                    lineReplyService.replyWithQuickReply(replyToken, userId,
                        "歡迎來到我們的婚禮邀請頁面！請選擇您想查詢的項目 😊\n若您想留言祝福牆，請輸入：祝福:您的祝福內容\n例如：祝福:新婚快樂，永浴愛河 💖");

                } catch (Exception e) {
                    logger.error("處理 {} 類型訊息時發生錯誤：{}", messageType, e.getMessage(), e);
                    lineReplyService.pushQuickReply(userId,
                        "系統發生錯誤，請稍後再試 🙏");
                }
            }
        }

        return ResponseEntity.ok("OK");
    }
}