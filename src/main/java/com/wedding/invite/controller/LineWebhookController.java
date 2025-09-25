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
                    if ("text".equals(messageType) && messageText != null) {
                        messageText = messageText.trim();
                        switch (messageText) {
                            case "地點":
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "婚禮地點：彰化縣員林市員林大道一段298號 💒\n👉 點我導航：https://maps.app.goo.gl/7asmcGXXye3Tkmv27?g_st=ipc");
                                continue;
                            case "時間":
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "📅 婚禮日期：2025年10月25日（星期六）\r\n"
                                    + "⏰ 入場時間：中午 12:00\r\n"
                                    + "🍽️ 開席時間：中午 12:30\r\n");
                                continue;
                            case "報名":
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "報名開放囉！📝\n"
                                    + "👉 點我填寫報名表：https://forms.gle/ZtYcJVXMaLq7tPXn9\n\n"
                                    + "為方便統計與安排，請大家儘早填寫，感謝您的配合 💖");
                                continue;
                            case "祝福牆":
                                var blessings = blessingService.getBlessings();
                                if (blessings == null || blessings.isEmpty()) {
                                    lineReplyService.replyWithQuickReply(replyToken, userId,
                                        "目前還沒有祝福留言，快來成為第一位吧！🎉");
                                } else {
                                    lineReplyService.replyWithBlessingFlex(replyToken, userId, blessings);
                                    lineReplyService.pushQuickReply(userId,
                                        "🎊 一起來留言祝福牆吧！\r\n"
                                        + "請輸入格式：祝福: 您的祝福話語\r\n"
                                        + "範例：祝福: 新婚快樂，百年好合 ");
                                }
                                continue;
                            case "留言數量":
                                int count = blessingService.getBlessings().size();
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "目前祝福牆共有 " + count + " 則留言 🎉\n快來留言祝福新人吧！");
                                continue;
                            case "清除測試留言":
                                blessingService.deleteBlessingsByKeyword("測試");
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "✅ 已清除所有包含『測試』的祝福留言 🧹");
                                continue;
                            case "清除8888留言":
                                blessingService.deleteBlessingsByKeyword("8888");
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "✅ 已清除所有包含『8888』的祝福留言 🧹");
                                continue;
                            case "清除牆 第1頁留言":
                                blessingService.deleteBlessingsByKeyword("牆");
                                lineReplyService.replyWithQuickReply(replyToken, userId,
                                    "✅ 已清除所有包含『牆』有關的祝福留言 🧹");
                                continue;
                            default:
                                if (messageText.contains("祝福")) {
                                    String blessingMessage = messageText.replace("祝福:", "").replace("祝福", "").trim();
                                    logger.info("🎁 收到祝福留言：userId={}, message={}", userId, blessingMessage);

                                    if (!blessingMessage.isEmpty()) {
                                        blessingService.saveBlessing(userId, blessingMessage);
                                        lineReplyService.replyWithQuickReply(replyToken, userId, "感謝您的祝福 💖");
                                    } else {
                                        lineReplyService.replyWithQuickReply(replyToken, userId, "請輸入有效的祝福內容，例如：祝福:新婚快樂！");
                                    }
                                    continue;
                                }
                        }
                    }

                    // ✅ 非文字訊息類型
                    lineReplyService.replyWithQuickReply(replyToken, userId,
                        "請選擇您想查詢的項目 😊\n若您想留言祝福牆，請輸入：祝福:您的祝福內容\n例如：祝福:新婚快樂，永浴愛河 💖");

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