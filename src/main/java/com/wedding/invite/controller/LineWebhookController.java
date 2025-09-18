package com.wedding.invite.controller;

import com.wedding.invite.model.WebhookRequest;
import com.wedding.invite.model.LineEvent;
import com.wedding.invite.service.LineReplyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class LineWebhookController {

    @Autowired
    private LineReplyService replyService;

    @PostMapping("/callback")
    public ResponseEntity<String> handleWebhook(@RequestBody WebhookRequest request) {
        StringBuilder result = new StringBuilder();

        for (LineEvent event : request.getEvents()) {
            String replyToken = event.getReplyToken();
            String messageText = event.getMessage().getText();

            try {
                replyService.replyToUser(replyToken, "收到您的訊息：" + messageText);
                result.append("Webhook received. Message: ").append(messageText).append(" | LINE API called.\n");
            } catch (Exception e) {
                result.append("LINE API failed: ").append(e.getMessage()).append("\n");
            }
        }

        return ResponseEntity.ok(result.toString());
    }
}