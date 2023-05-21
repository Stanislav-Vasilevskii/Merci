package com.stanislav.merci;

import com.stanislav.merci.controller.UserPointsController;
import com.stanislav.merci.dao.UserPointsRepository;
import com.stanislav.merci.dto.UserPointsDto;
import com.stanislav.merci.entity.UserPoints;
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
public class UserPointsControllerTest {
    @SpyBean
    private UserPointsController controller;

    @Autowired
    private UserPointsRepository repository;

    private final Integer positiveAmount = 200;
    private final Integer negativeAmount = -100;

    @Test
    void shouldCreateUserPoints() {
        // given
        final UserPointsDto srcUserPointsDto = prepareUserPointsDto();

        // when
        controller.createUserPoints(srcUserPointsDto);

        // then
        final UserPoints userPoints = repository
                .findByUserId(srcUserPointsDto.getUserId()).orElseThrow();

        assertAll(
                () -> assertEquals(0, userPoints.getVersion()),
                () -> assertEquals(0, userPoints.getAmount()),
                () -> verify(controller, times(1)).createUserPoints(any())
        );
    }

    @Test
    void shouldNotCreateUserPoints_nullUserId() {
        // given
        final UserPointsDto srcUserPointsDto = prepareUserPointsDtoWithNullUserId();

        // when
        try {
            controller.createUserPoints(srcUserPointsDto);
        } catch (Exception ex){
            Assertions.assertTrue(ex.getMessage().contains("Validation failed for object='userPointsDto'"));
        }

        // then

        assertAll(
                () -> verify(controller, times(1)).createUserPoints(any())
        );
    }

    @Test
    void shouldUpdateUserPoints() {
        // given
        final UserPointsDto srcUserPointsDto = prepareUserPointsDto();
        controller.createUserPoints(srcUserPointsDto);
        srcUserPointsDto.setAmount(positiveAmount);

        // when
       controller.updateUserPoints(srcUserPointsDto);

        // then
        final UserPoints userPoints = repository
                .findByUserId(srcUserPointsDto.getUserId()).orElseThrow();

        assertAll(
                () -> assertEquals(1, userPoints.getVersion()),
                () -> assertEquals(200, userPoints.getAmount()),
                () -> verify(controller, times(1)).updateUserPoints(any())
        );
    }

    @Test
    void shouldNotUpdateUserPoints_notEnoughPointsException() {
        // given
        final UserPointsDto srcUserPointsDto = prepareUserPointsDto();
        controller.createUserPoints(srcUserPointsDto);
        srcUserPointsDto.setAmount(negativeAmount);

        // when
        try {
            controller.updateUserPoints(srcUserPointsDto);
        } catch (Exception ex){
            assertEquals("Not enough points", ex.getMessage());
        }

        // then
        final UserPoints userPoints = repository
                .findByUserId(srcUserPointsDto.getUserId()).orElseThrow();

        assertAll(
                () -> assertEquals(0, userPoints.getVersion()),
                () -> assertEquals(0, userPoints.getAmount()),
                () -> verify(controller, times(1)).updateUserPoints(any())
        );
    }

    @Test
    void shouldDeleteUserPoints() {
        // given
        final UserPointsDto srcUserPointsDto = prepareUserPointsDto();
        controller.createUserPoints(srcUserPointsDto);

        // when
        controller.deleteByUserId(srcUserPointsDto.getUserId());

        // then
        try {
            controller.getByUserId(srcUserPointsDto.getUserId());
        } catch (Exception ex){
            assertEquals("User was not found", ex.getMessage());
        }

        assertAll(
                () -> verify(controller, times(1)).deleteByUserId(any())
        );
    }

    private static UserPointsDto prepareUserPointsDto(){
        UserPointsDto userPointsDto = new UserPointsDto();
        userPointsDto.setUserId(UUID.randomUUID());
        userPointsDto.setAmount(0);
        return userPointsDto;
    }

    private static UserPointsDto prepareUserPointsDtoWithNullUserId(){
        UserPointsDto userPointsDto = new UserPointsDto();
        userPointsDto.setAmount(0);
        return userPointsDto;
    }
}
