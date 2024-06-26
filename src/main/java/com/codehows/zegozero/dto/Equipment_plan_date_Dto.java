package com.codehows.zegozero.dto;

import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Plan_equipment;
import com.codehows.zegozero.entity.Plans;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class Equipment_plan_date_Dto {
    // 발주계획
    private int equipment_plan_id;
    private LocalDateTime estimated_start_date;
    private LocalDateTime estimated_end_date;
    private int input;
    private int output;
    private String product_name;
    private LocalDateTime start_date;
    private LocalDateTime end_date;
    private int plan_id;



    public Equipment_plan_date_Dto(Plan_equipment plan_equipment) {
        this.equipment_plan_id = plan_equipment.getEquipment_plan_id();
        this.estimated_start_date = plan_equipment.getEstimated_start_date();
        this.estimated_end_date = plan_equipment.getEstimated_end_date();
        this.input = plan_equipment.getInput();
        this.output = plan_equipment.getOutput();
        this.product_name = plan_equipment.getPlan().getProduct_name();
        this.start_date = plan_equipment.getStart_date();
        this.end_date = plan_equipment.getEnd_date();
        this.plan_id = plan_equipment.getPlan().getPlan_id();
    }

}
