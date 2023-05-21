package com.stanislav.merci.controller;

import com.stanislav.merci.dto.UserPointsDto;
import com.stanislav.merci.service.UserPointsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/user_points")
@Tag(name="User Points", description = "Methods to work with User Points")
public class UserPointsController {
    private final UserPointsService userPointsService;

    public UserPointsController(UserPointsService userPointsService) {
        this.userPointsService = userPointsService;
    }

    @GetMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get User Points by ID")
    public UserPointsDto getByUserId(@PathVariable final UUID user_id){
        return UserPointsDto.toPointsDto(userPointsService.findByUserId(user_id));
    }

    @GetMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Get all User Points")
    public List<UserPointsDto> getAll(){
        return userPointsService.findAll().stream()
                .map(UserPointsDto::toPointsDto)
                .toList();
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create new User Points")
    public void createUserPoints(@Valid @RequestBody final UserPointsDto userPointsDto){
        userPointsService.create(userPointsDto);
    }

    @PatchMapping()
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Update User Points")
    public void updateUserPoints(@RequestBody final UserPointsDto userPointsDto){
        userPointsService.update(userPointsDto);
    }

    @DeleteMapping("/{user_id}")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Soft delete User Points")
    public void deleteByUserId(@PathVariable final UUID user_id){
        userPointsService.delete(user_id);
    }
}
