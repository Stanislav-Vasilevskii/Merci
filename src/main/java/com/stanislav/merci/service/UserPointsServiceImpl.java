package com.stanislav.merci.service;

import com.stanislav.merci.dao.UserPointsRepository;
import com.stanislav.merci.dto.UserPointsDto;
import com.stanislav.merci.entity.UserPoints;
import com.stanislav.merci.exception.NotEnoughPointsException;
import com.stanislav.merci.exception.UserPointsAlreadyExistsException;
import com.stanislav.merci.exception.UserPointsNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
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
    public UserPointsDto findByUserId(UUID userId) {
        UserPoints points = userPointsRepository.findByUserId(userId)
                .orElseThrow(() -> new UserPointsNotFoundException("User was not found"));
        return UserPointsDto.toPointsDto(points);
    }

    @Override
    public List<UserPointsDto> findAll() {

        return userPointsRepository.findAll()
                .stream()
                .filter(p -> !p.isDeleted())
                .map(UserPointsDto::toPointsDto)
                .toList();
    }

    @Override
    public void delete(UUID userId) {
        UserPoints userPoints = userPointsRepository.findByUserId(userId)
                .orElseThrow(() -> new UserPointsNotFoundException("User was not found"));
        userPoints.setDeleted(true);
        userPointsRepository.save(userPoints);
    }

    @Override
    @Transactional
    public void update(UserPointsDto pointsDto) {
        final UUID userId = pointsDto.getUserId();
        final Integer changeAmount = pointsDto.getAmount();
        final UserPoints userPoints = userPointsRepository.findByUserId(userId)
                .orElseThrow(() -> new UserPointsNotFoundException("User was not found"));
        final Integer resultAmount = changeAmount(userPoints.getAmount(), changeAmount);
        userPoints.setAmount(resultAmount);
        try {
            userPointsRepository.save(userPoints);
        } catch (ObjectOptimisticLockingFailureException e) {
            log.warn("Somebody has already updated the amount for User:{} in concurrent transaction.", userId);
        }
    }

    @Override
    public void create(UserPointsDto userPointsDto) {
        try {
            userPointsRepository.save(userPointsDto.toPoints());
        } catch (DataIntegrityViolationException ex){
            throw new UserPointsAlreadyExistsException("User Points already exists");
        }
    }

    private Integer changeAmount(Integer initialAmount, Integer changeAmount) {
        if(isPositiveAmountToAdd(changeAmount) || isEnoughPointsToSubtractAmount(initialAmount, changeAmount)){
            return initialAmount + changeAmount;
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
