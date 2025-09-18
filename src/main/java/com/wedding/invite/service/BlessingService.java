package com.wedding.invite.service;

import com.wedding.invite.model.Blessing;
import com.wedding.invite.repository.BlessingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BlessingService {

    @Autowired
    private BlessingRepository blessingRepository;

    public void saveBlessing(String userId, String messageText) {
        Blessing blessing = new Blessing();
        blessing.setName(userId);
        blessing.setMessage(messageText);
        blessingRepository.save(blessing);
    }
}