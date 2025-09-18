package com.wedding.invite.controller;

import com.wedding.invite.service.RichMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/richmenu")
public class RichMenuController {

    @Autowired
    private RichMenuService richMenuService;

    @PostMapping("/create")
    public String createRichMenu() {
        try {
            richMenuService.createMenu();
            return "Rich Menu created!";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to create Rich Menu.";
        }
    }
}