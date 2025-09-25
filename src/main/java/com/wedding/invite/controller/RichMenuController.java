package com.wedding.invite.controller;

import com.wedding.invite.service.BlessingService;
import com.wedding.invite.service.RichMenuService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/richmenu")
public class RichMenuController {

    private static final Logger logger = LoggerFactory.getLogger(RichMenuController.class);

    private final RichMenuService richMenuService;
    private final BlessingService blessingService;

    // ✅ 建構式注入，Spring Boot 會自動注入這兩個 Service
    public RichMenuController(RichMenuService richMenuService, BlessingService blessingService) {
        this.richMenuService = richMenuService;
        this.blessingService = blessingService;
    }

    @PostMapping("/create")
    public String createRichMenu() {
        logger.info("🔧 開始建立 Rich Menu");
        try {
            richMenuService.createMenu();
            logger.info("✅ Rich Menu 建立成功");
            return "Rich Menu created!";
        } catch (Exception e) {
            logger.error("❌ 建立 Rich Menu 失敗：{}", e.getMessage(), e);
            return "Failed to create Rich Menu.";
        }
    }

    // ✅ 如果你未來想加上祝福牆測試或其他功能，可以在這裡擴充
    @GetMapping("/test-blessings")
    public String testBlessingAccess() {
        try {
            int count = blessingService.getBlessings().size();
            logger.info("📥 成功取得祝福牆留言數量：{}", count);
            return "目前祝福牆留言數量：" + count;
        } catch (Exception e) {
            logger.error("❌ 取得祝福牆留言失敗：{}", e.getMessage(), e);
            return "無法取得祝福牆留言";
        }
    }
}