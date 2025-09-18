package com.wedding.invite.controller;

import com.wedding.invite.model.WebhookRequest;
import com.wedding.invite.model.LineEvent;
import com.wedding.invite.service.LineReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.wedding.invite.service.BlessingService;

@RestController
public class LineWebhookController {

    @Autowired
    private LineReplyService replyService;
    private BlessingService blessingService;


    @PostMapping("/callback")
    public ResponseEntity<String> handleWebhook(@RequestBody WebhookRequest request) {
        StringBuilder result = new StringBuilder();

        for (LineEvent event : request.getEvents()) {
            String replyToken = event.getReplyToken();
            String messageText = event.getMessage().getText();

            try {
                // 根據訊息內容回覆不同內容
                if (messageText.contains("地點")) {
                    replyService.replyToUser(replyToken, "婚禮地點：彰化縣員林市員林大道一段298號 💒");
                } else if (messageText.contains("時間")) {
                    replyService.replyToUser(replyToken, "婚禮時間：2025年10月25日 中午12點30分 ⏰");
                } else if (messageText.contains("報名")) {
                    replyService.replyToUser(replyToken, "報名連結：https://yourdomain.com/rsvp 📝");
                } else if (messageText.contains("祝福")) {
                    replyService.replyToUser(replyToken, "感謝您的祝福！💖 您的留言將會展示在祝福牆上！");
                    blessingService.saveBlessing(event.getSource().getUserId(), messageText);
                } else {
                    replyService.replyToUser(replyToken, "感謝您的訊息，我們收到囉！");
                }

                result.append("Webhook received. Message: ").append(messageText).append(" | LINE API called.\n");
            } catch (Exception e) {
                result.append("LINE API failed: ").append(e.getMessage()).append("\n");
            }
        }

        return ResponseEntity.ok(result.toString());
    }
}