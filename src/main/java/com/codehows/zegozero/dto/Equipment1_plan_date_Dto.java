package com.codehows.zegozero.dto;

import com.codehows.zegozero.entity.Plan_equipment;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Equipment1_plan_date_Dto {
    // 발주계획
    private int equipmentId;
    private LocalDateTime estimatedStartDate;
    private LocalDateTime estimatedEndDate;
    private int input;
    private int output;

}
