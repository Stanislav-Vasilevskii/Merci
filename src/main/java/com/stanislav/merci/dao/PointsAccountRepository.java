package com.stanislav.merci.dao;

import com.stanislav.merci.entity.PointsAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PointsAccountRepository extends JpaRepository<PointsAccount, UUID> {
    Optional<PointsAccount> findByUserId(UUID userId);

    @Modifying
    @Query(value = "update points set quantity = :quantity where user_id = :userId", nativeQuery = true)
    void changeQuantity(@Param("quantity") Integer quantity, @Param("userId") UUID userId);
}
