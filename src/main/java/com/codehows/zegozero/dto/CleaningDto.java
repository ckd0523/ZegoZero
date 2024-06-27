package com.codehows.zegozero.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@NoArgsConstructor
@Getter
@Setter
public class CleaningDto {

    private Integer shipped_quantity;
    private Integer planId;

    public CleaningDto(Integer shipped_quantity, Integer planId) {
        this.shipped_quantity = shipped_quantity;
        this.planId = planId;
    }

}
