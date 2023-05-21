package com.stanislav.merci.service;

import com.stanislav.merci.dao.UserPointsRepository;
import com.stanislav.merci.dto.UserPointsDto;
import com.stanislav.merci.entity.UserPoints;
import com.stanislav.merci.exception.NotEnoughPointsException;
import com.stanislav.merci.exception.UserPointsNotFoundException;
import com.stanislav.merci.exception.UserPointsWasDeletedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
@Slf4j
@Service
public class UserPointsServiceImpl implements UserPointsService {

    private final UserPointsRepository userPointsRepository;

    public UserPointsServiceImpl(UserPointsRepository userPointsRepository) {
        this.userPointsRepository = userPointsRepository;
    }

    @Override
    public UserPoints findByUserId(UUID userId) {
        UserPoints points = userPointsRepository.findByUserId(userId)
                .orElseThrow(() -> new UserPointsNotFoundException("User was not found"));
        if(points.isDeleted()){
            throw new UserPointsWasDeletedException("User was deleted");
        }
        return points;
    }

    @Override
    public List<UserPoints> findAll() {

        return userPointsRepository.findAll()
                .stream()
                .filter(p -> !p.isDeleted())
                .toList();
    }

    @Override
    public void save(UserPoints account) {
        userPointsRepository.save(account);
    }

    @Override
    public void delete(UUID userId) {
        UserPoints points = findByUserId(userId);
        points.setDeleted(true);
        save(points);
    }

    @Override
    @Transactional
    public void update(UserPointsDto pointsDto) {
        final UUID userId = pointsDto.getUserId();
        final Integer amount = pointsDto.getAmount();
        final UserPoints userPoints = findByUserId(userId);
        changeAmount(userPoints, amount);
        try {
            save(userPoints);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Somebody has already updated the amount for User:{} in concurrent transaction.", userId);
        }
    }

    @Override
    public void create(UserPointsDto userPointsDto) {
        UserPoints userPoints = new UserPoints();
        userPoints.setUserId(userPointsDto.getUserId());
        userPoints.setAmount(userPointsDto.getAmount());
        userPoints.setDeleted(false);
        userPointsRepository.save(userPoints);
    }

    private void changeAmount(UserPoints points, Integer amount) {
        if(isPositiveAmountToAdd(amount) || isEnoughPointsToSubtractAmount(points.getAmount(), amount)){
            points.setAmount(points.getAmount() + amount);
        } else {
            throw new NotEnoughPointsException("Not enough points");
        }
    }

    private boolean isPositiveAmountToAdd(Integer amount){
        return amount > 0;
    }

    private boolean isEnoughPointsToSubtractAmount(Integer currentAmount, Integer amount){
        return currentAmount >= Math.abs(amount);
    }
}
