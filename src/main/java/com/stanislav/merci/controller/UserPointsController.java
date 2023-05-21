package com.stanislav.merci.controller;

import com.stanislav.merci.dto.UserPointsDto;
import com.stanislav.merci.service.UserPointsService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user_points")
public class UserPointsController {
    private final UserPointsService userPointsService;

    public UserPointsController(UserPointsService userPointsService) {
        this.userPointsService = userPointsService;
    }

    @GetMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public UserPointsDto getByUserId(@PathVariable final UUID user_id){
        return UserPointsDto.toPointsDto(userPointsService.findByUserId(user_id));
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    public List<UserPointsDto> getAll(){
        return userPointsService.findAll().stream()
                .map(UserPointsDto::toPointsDto)
                .toList();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public void createUserPoints(@Valid @RequestBody final UserPointsDto userPointsDto){
        userPointsService.create(userPointsDto);
    }

    @PatchMapping()
    @ResponseStatus(HttpStatus.OK)
    public void updateUserPoints(@RequestBody final UserPointsDto userPointsDto){
        userPointsService.update(userPointsDto);
    }

    @DeleteMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteByUserId(@PathVariable final UUID user_id){
        userPointsService.delete(user_id);
    }
}
