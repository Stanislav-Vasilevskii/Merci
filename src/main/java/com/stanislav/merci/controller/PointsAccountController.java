package com.stanislav.merci.controller;

import com.stanislav.merci.dto.PointsAccountDto;
import com.stanislav.merci.entity.PointsAccount;
import com.stanislav.merci.service.PointsAccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/points_account")
public class PointsAccountController {
    private final PointsAccountService pointsAccountService;

    public PointsAccountController(PointsAccountService pointsAccountService) {
        this.pointsAccountService = pointsAccountService;
    }

    @GetMapping("/{user_id}")
    public ResponseEntity<PointsAccountDto> getByUserId(@PathVariable UUID user_id){
        PointsAccount account = pointsAccountService.findByUserId(user_id);
        PointsAccountDto result = PointsAccountDto.toPointsAccountDto(account);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<List<PointsAccountDto>> getAllPointsAccounts(){
        List<PointsAccountDto> result = pointsAccountService.findAll().stream()
                .map(PointsAccountDto::toPointsAccountDto)
                .toList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/{user_id}/{amount}")
    public ResponseEntity<PointsAccountDto> changeQuantity(@PathVariable UUID user_id, @PathVariable Integer amount){
        PointsAccount account = pointsAccountService.tryChangeAmount(user_id, amount);
        PointsAccountDto result = PointsAccountDto.toPointsAccountDto(account);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
