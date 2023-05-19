package com.stanislav.merci.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stanislav.merci.entity.PointsAccount;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PointsAccountDto {
    private UUID userId;
    private int amount;

    public PointsAccount toPointsAccount(){
        PointsAccount account = new PointsAccount();
        account.setUserId(userId);
        account.setAmount(amount);
        return account;
    }

    public static PointsAccountDto toPointsAccountDto(PointsAccount account){
        PointsAccountDto dto = new PointsAccountDto();
        dto.setUserId(account.getUserId());
        dto.setAmount(account.getAmount());
        return dto;
    }
}
