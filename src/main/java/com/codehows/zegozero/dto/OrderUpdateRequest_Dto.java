package com.codehows.zegozero.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class OrderUpdateRequest_Dto {
    private List<Integer> orderIds;

}
