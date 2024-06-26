package com.codehows.zegozero.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment_Dto {

    private int equipmentPlanId;
    private LocalDateTime start_date;
    private LocalDateTime end_date;

}
