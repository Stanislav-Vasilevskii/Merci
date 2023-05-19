package com.stanislav.merci.dao;

import com.stanislav.merci.entity.PointsAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PointsAccountRepository extends JpaRepository<PointsAccount, UUID> {
    Optional<PointsAccount> findByUserId(UUID userId);
}
