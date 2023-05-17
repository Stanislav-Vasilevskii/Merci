package com.stanislav.merci.dao;

import com.stanislav.merci.entity.PointsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PointsAccountRepository extends JpaRepository<PointsAccount, Integer> {
    Optional<PointsAccount> findByUserId(int userId);
}
