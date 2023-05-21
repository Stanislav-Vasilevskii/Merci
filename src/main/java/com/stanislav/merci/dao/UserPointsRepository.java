package com.stanislav.merci.dao;

import com.stanislav.merci.entity.UserPoints;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserPointsRepository extends JpaRepository<UserPoints, UUID> {
    Optional<UserPoints> findByUserId(UUID userId);
}
