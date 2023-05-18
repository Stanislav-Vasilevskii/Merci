package com.stanislav.merci.service;

import com.stanislav.merci.entity.PointsAccount;

import java.util.List;
import java.util.UUID;

public interface PointsAccountService {
    PointsAccount findByUserId(UUID userId);
    List<PointsAccount> findAll();
    PointsAccount changeQuantity(int quantity, UUID userId);
}
