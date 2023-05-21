package com.stanislav.merci;

import com.stanislav.merci.dao.UserPointsRepository;
import com.stanislav.merci.dto.UserPointsDto;
import com.stanislav.merci.entity.UserPoints;
import com.stanislav.merci.service.UserPointsService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class UserPointsServiceTest {
    @SpyBean
    private UserPointsService service;

    @Autowired
    private UserPointsRepository repository;

    private final Integer POSITIVE_AMOUNT = 200;
    private final Integer NEGATIVE_AMOUNT = -100;

    @Test
    void shouldChangeAmountOnce() {
        // given
        final UserPoints srcUserPoints = repository.save( prepareUserPoints());
        assertEquals(0, srcUserPoints.getVersion());

        // when
        srcUserPoints.setAmount(srcUserPoints.getAmount() + POSITIVE_AMOUNT);
        service.update(UserPointsDto.toPointsDto(srcUserPoints));

        // then
        final UserPoints userPoints = repository
                .findById(srcUserPoints.getId()).orElseThrow();

        assertAll(
                () -> assertEquals(1, userPoints.getVersion()),
                () -> assertEquals(200, userPoints.getAmount()),
                () -> verify(service, times(1)).update(any())
        );
    }

    @Test
    void shouldChangeAmountTwice() {
        // given
        final UserPoints srcUserPoints = repository.save( prepareUserPoints());
        assertEquals(0, srcUserPoints.getVersion());

        // when
        srcUserPoints.setAmount(srcUserPoints.getAmount() + POSITIVE_AMOUNT);
        service.update(UserPointsDto.toPointsDto(srcUserPoints));
        service.update(UserPointsDto.toPointsDto(srcUserPoints));

        // then
        final UserPoints userPoints = repository
                .findByUserId(srcUserPoints.getUserId()).orElseThrow();

        assertAll(
                () -> assertEquals(2, userPoints.getVersion()),
                () -> assertEquals(400, userPoints.getAmount()),
                () -> verify(service, times(2)).update(any())
        );
    }

    @Test
    void shouldNotChangeAmount_notEnoughPoints() {
        // given
        final UserPoints srcUserPoints = repository.save( prepareUserPoints());
        assertEquals(0, srcUserPoints.getVersion());

        // when
        srcUserPoints.setAmount(srcUserPoints.getAmount() + NEGATIVE_AMOUNT);
        try {
            service.update(UserPointsDto.toPointsDto(srcUserPoints));
        } catch (Exception ex){
            assertEquals("Not enough points", ex.getMessage());
        }

        // then
        final UserPoints userPoints = repository
                .findById(srcUserPoints.getId()).orElseThrow();

        assertAll(
                () -> assertEquals(0, userPoints.getVersion()),
                () -> assertEquals(0, userPoints.getAmount()),
                () -> verify(service, times(1)).update(any())
        );
    }

    @Test
    void shouldChangeAmount_withOptimisticLockingHandling() throws InterruptedException {
        // given
        final UserPoints srcUserPoints = repository.save( prepareUserPoints());
        assertEquals(0, srcUserPoints.getVersion());

        Thread t1 = new Thread(() -> {
            UserPoints userPoints = null;
            try {
                userPoints = repository.findByUserId(srcUserPoints.getUserId()).orElseThrow();
                Thread.sleep(6000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            try {
                srcUserPoints.setAmount(100);
                service.update(UserPointsDto.toPointsDto(userPoints));
            }catch (Exception e){
                Assertions.assertTrue(e.getMessage().contains("Row was updated or deleted by another transaction"));
            }

        });

        Thread t2 = new Thread(() -> {
            srcUserPoints.setAmount(200);
            service.update(UserPointsDto.toPointsDto(srcUserPoints));
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        final UserPoints result = repository.findByUserId( srcUserPoints.getUserId()).orElse(null);

        assertAll(
                () -> assertEquals(1, result.getVersion()),
                () -> verify(service, times(2)).update(any())
        );
    }

    private static UserPoints prepareUserPoints(){
        UserPoints userPoints = new UserPoints();
        userPoints.setUserId(UUID.randomUUID());
        userPoints.setVersion(0L);
        userPoints.setAmount(0);
        return userPoints;
    }
}
