package com.stanislav.merci.service;

import com.stanislav.merci.dto.UserPointsDto;
import com.stanislav.merci.entity.UserPoints;

import java.util.List;
import java.util.UUID;

public interface UserPointsService {
    UserPoints findByUserId(UUID userId);
    List<UserPoints> findAll();
    void save(UserPoints points);
    void delete(UUID userId);
    void update(UserPointsDto userPointsDto);
    void create(UserPointsDto userPointsDto);
}
