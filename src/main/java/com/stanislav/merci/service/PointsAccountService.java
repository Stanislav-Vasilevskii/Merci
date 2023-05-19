package com.stanislav.merci.service;

import com.stanislav.merci.entity.PointsAccount;

import java.util.List;
import java.util.UUID;

public interface PointsAccountService {
    PointsAccount findByUserId(UUID userId);
    List<PointsAccount> findAll();
    PointsAccount tryChangeAmount(UUID userId, Integer amount);
    void changeAmount(UUID userId, Integer amount);
}
