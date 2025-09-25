package com.wedding.invite.repository;

import com.wedding.invite.model.Blessing;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BlessingRepository extends JpaRepository<Blessing, Long> {
    List<Blessing> findTop20ByOrderByCreatedAtDesc(); // 可選：撈固定筆數

    // ✅ 加入這行：根據關鍵字刪除留言
    void deleteByMessageContaining(String keyword);
}