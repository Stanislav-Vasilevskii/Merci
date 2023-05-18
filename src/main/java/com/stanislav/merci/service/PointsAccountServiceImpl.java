package com.stanislav.merci.service;

import com.stanislav.merci.dao.PointsAccountRepository;
import com.stanislav.merci.entity.PointsAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class PointsAccountServiceImpl implements PointsAccountService {

    @Autowired
    private PointsAccountRepository pointsAccountRepository;
    @Override
    public PointsAccount findByUserId(UUID userId) {
        return pointsAccountRepository.findByUserId(userId).orElseThrow();
    }

    @Override
    public List<PointsAccount> findAll() {
        return pointsAccountRepository.findAll();
    }

    @Override
    @Transactional
    public PointsAccount changeQuantity(int quantity, UUID userId) {
        PointsAccount account = pointsAccountRepository.findByUserId(userId).orElseThrow();
        int currentQuantity = account.getQuantity();
        if(quantity > 0 || currentQuantity > Math.abs(quantity)){
            pointsAccountRepository.changeQuantity(currentQuantity + quantity, userId);
        }
        return pointsAccountRepository.findByUserId(userId).orElseThrow();
    }
}
