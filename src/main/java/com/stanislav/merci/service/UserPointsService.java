package com.stanislav.merci.service;

import com.stanislav.merci.dto.UserPointsDto;
import java.util.List;
import java.util.UUID;

public interface UserPointsService {
    UserPointsDto findByUserId(UUID userId);
    List<UserPointsDto> findAll();
    void delete(UUID userId);
    void update(UserPointsDto userPointsDto);
    void create(UserPointsDto userPointsDto);
}
