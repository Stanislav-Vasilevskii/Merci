package com.stanislav.merci.service;

import com.stanislav.merci.dao.PointsAccountRepository;
import com.stanislav.merci.entity.PointsAccount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PointsAccountServiceImpl implements PointsAccountService {

    @Autowired
    private PointsAccountRepository pointsAccountRepository;
    @Override
    public PointsAccount findByUserId(int userId) {
        return pointsAccountRepository.findByUserId(userId).orElseThrow();
    }
}
