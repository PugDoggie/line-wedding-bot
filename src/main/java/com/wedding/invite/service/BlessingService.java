package com.wedding.invite.service;

import com.wedding.invite.model.Blessing;
import com.wedding.invite.repository.BlessingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BlessingService {

    // 實際應用中，這裡應該注入 BlessingRepository 來與資料庫互動
    // 這裡為了範例方便，使用一個靜態 List 暫存資料
    private static final List<Blessing> blessings = new ArrayList<>();

    // 實際應用的方法：
    // @Autowired
    // private BlessingRepository blessingRepository;

    public void saveBlessing(String userId, String message) {
        // 實際應用中，你會在這裡查詢使用者名稱，並儲存到資料庫
        // Blessing blessing = new Blessing(userId, "使用者名稱", message, LocalDateTime.now());
        // blessingRepository.save(blessing);

        // 這裡為了範例，直接模擬儲存
        String userName = getUserNameFromUserId(userId);
        Blessing newBlessing = new Blessing(userId, userName, message, LocalDateTime.now());
        blessings.add(0, newBlessing); // 新增到列表最前面，以便 Flex Message 顯示最新留言
    }

    public List<Blessing> getBlessings() {
        // 實際應用中，你會從資料庫取得資料
        // return blessingRepository.findTop5ByOrderByCreatedAtDesc();
        
        // 這裡為了範例，直接回傳靜態列表
        return blessings;
    }

    // 模擬從 LINE 的 userId 取得使用者名稱
    private String getUserNameFromUserId(String userId) {
        // 實際應用中，需要呼叫 LINE Profile API 來取得使用者名稱
        // 這裡暫時用一個簡單的邏輯
        return "匿名祝福者" + userId.substring(userId.length() - 4);
    }
}