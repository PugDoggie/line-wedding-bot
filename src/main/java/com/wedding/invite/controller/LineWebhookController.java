package com.wedding.invite.controller;

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
    public ResponseEntity<String> handleWebhook(@RequestBody LineEvent event) {
        String replyToken = event.getReplyToken();
        String result = "Webhook received. ReplyToken: " + replyToken;

        try {
            replyService.replyToUser(replyToken, "感謝您的訊息，我們收到囉！");
            result += " | LINE API called.";
        } catch (Exception e) {
            result += " | LINE API failed: " + e.getMessage();
        }

        return ResponseEntity.ok(result);
    }
}