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

    @PostMapping
    public ResponseEntity<String> handleWebhook(@RequestBody WebhookRequest request) {
        if (request == null || request.getEvents() == null || request.getEvents().isEmpty()) {
            logger.warn("收到空的 webhook 請求");
            return ResponseEntity.badRequest().body("No events found.");
        }

        for (LineEvent event : request.getEvents()) {
            String replyToken = event.getReplyToken();
            if (replyToken == null || event.getMessage() == null || event.getSource() == null) {
                logger.warn("事件缺少必要欄位，略過處理");
                continue;
            }

            String userId = event.getSource().getUserId();
            String messageType = event.getMessage().getType();
            String eventType = event.getType();
            String messageText = event.getMessage().getText();

            if (Objects.equals(eventType, "message") && Objects.equals(messageType, "text") && messageText != null) {
                messageText = messageText.trim();

                try {
                    switch (messageText) {
                        case "地點":
                            lineReplyService.replyToUser(replyToken, "婚禮地點：彰化縣員林市員林大道一段298號 💒");
                            break;
                        case "時間":
                            lineReplyService.replyToUser(replyToken, "婚禮時間：2025年10月25日 中午12點30分 ⏰");
                            break;
                        case "報名":
                            lineReplyService.replyToUser(replyToken, "報名連結：https://forms.gle/ZtYcJVXMaLq7tPXn9 📝");
                            break;
                        case "祝福牆":
                            var blessings = blessingService.getBlessings();
                            if (blessings == null || blessings.isEmpty()) {
                                lineReplyService.replyToUser(replyToken, "目前還沒有祝福留言，快來成為第一位吧！🎉");
                            } else {
                                lineReplyService.replyWithBlessingFlex(replyToken, blessings);
                            }
                            break;
                        default:
                            if (messageText.startsWith("祝福:")) {
                                String blessingMessage = messageText.substring(3).trim();
                                if (!blessingMessage.isEmpty()) {
                                    blessingService.saveBlessing(userId, blessingMessage);
                                    lineReplyService.replyToUser(replyToken, "感謝您的祝福！💖 您的留言已成功記錄。");
                                } else {
                                    lineReplyService.replyToUser(replyToken, "請輸入有效的祝福內容，例如：祝福:新婚快樂！");
                                }
                            } else {
                                lineReplyService.replyWithQuickReply(replyToken, "歡迎來到我們的婚禮邀請頁面！請選擇您想查詢的項目 😊");
                            }
                            break;
                    }
                } catch (Exception e) {
                    logger.error("處理 webhook 發生錯誤：{}", e.getMessage(), e);
                    try {
                        lineReplyService.replyToUser(replyToken, "發生錯誤，請稍後再試 🙏");
                    } catch (Exception ex) {
                        logger.error("錯誤回覆失敗：{}", ex.getMessage(), ex);
                    }
                }
            }
        }

        return ResponseEntity.ok("OK");
    }
}