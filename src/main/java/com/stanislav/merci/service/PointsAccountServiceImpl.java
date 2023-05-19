package com.stanislav.merci.service;

import com.stanislav.merci.dao.PointsAccountRepository;
import com.stanislav.merci.entity.PointsAccount;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class PointsAccountServiceImpl implements PointsAccountService {

    private final PointsAccountRepository pointsAccountRepository;

    public PointsAccountServiceImpl(PointsAccountRepository pointsAccountRepository) {
        this.pointsAccountRepository = pointsAccountRepository;
    }

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
    public PointsAccount tryChangeAmount(UUID userId, Integer amount) {
        try {
            changeAmount(userId, amount);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Somebody has already updated the amount for User:{} in concurrent transaction. Will try again...", userId);
            changeAmount(userId, amount);
        }
        return pointsAccountRepository.findByUserId(userId).orElseThrow();
    }
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void changeAmount(UUID userId, Integer amount) {
        PointsAccount account = pointsAccountRepository.findByUserId(userId).orElseThrow();
        int currentAmount = account.getAmount();
        if(amount > 0 || currentAmount > Math.abs(amount)){
            account.setAmount(currentAmount + amount);
            pointsAccountRepository.save(account);
        }
    }
}
