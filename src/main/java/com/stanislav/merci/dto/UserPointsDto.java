package com.stanislav.merci.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stanislav.merci.entity.UserPoints;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Schema(description = "User Points")
public class UserPointsDto {
    @Schema(description = "User ID")
    @NotNull
    private UUID userId;
    @Schema(description = "Amount of points")
    @Min(0)
    private Integer amount;

    public UserPoints toPoints(){
        UserPoints points = new UserPoints();
        points.setUserId(userId);
        points.setAmount(amount);
        return points;
    }

    public static UserPointsDto toPointsDto(UserPoints points){
        UserPointsDto dto = new UserPointsDto();
        dto.setUserId(points.getUserId());
        dto.setAmount(points.getAmount());
        return dto;
    }
}
