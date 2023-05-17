package com.stanislav.merci.controller;

import com.stanislav.merci.service.PointsAccountService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PointsAccountController {
    private final PointsAccountService pointsAccountService;

    public PointsAccountController(PointsAccountService pointsAccountService) {
        this.pointsAccountService = pointsAccountService;
    }

    @GetMapping("/{user_id}")
    public int getQuantityByUserId(@PathVariable int user_id){
        return pointsAccountService.findByUserId(user_id).getQuantity();
    }
}
