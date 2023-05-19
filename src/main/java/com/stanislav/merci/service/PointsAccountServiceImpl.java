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
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void save(PointsAccount account) {
        pointsAccountRepository.save(account);
    }
    @Override
    @Transactional
    public PointsAccount tryChangeAmount(UUID userId, Integer amount) {
        PointsAccount account = findByUserId(userId);
        changeAmount(account, amount);
        try {
            save(account);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Somebody has already updated the amount for User:{} in concurrent transaction.", userId);
        }
        return pointsAccountRepository.findByUserId(userId).orElseThrow();
    }

    private void changeAmount(PointsAccount account, Integer amount) {
        if(isPositiveAmountToAdd(amount) || isEnoughPointsToSubtractAmount(account.getAmount(), amount)){
            account.setAmount(account.getAmount() + amount);
        }
    }

    private boolean isPositiveAmountToAdd(int amount){
        return amount >= 0;
    }

    private boolean isEnoughPointsToSubtractAmount(int currentAmount, int amount){
        return currentAmount > Math.abs(amount);
    }
}
