package com.stanislav.merci.service;

import com.stanislav.merci.entity.PointsAccount;

public interface PointsAccountService {
    PointsAccount findByUserId(int userId);
}
