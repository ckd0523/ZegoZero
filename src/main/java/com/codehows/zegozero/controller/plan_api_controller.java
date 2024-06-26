package com.codehows.zegozero.controller;

import com.codehows.zegozero.entity.Plan_equipment;
import com.codehows.zegozero.entity.Plans;
import com.codehows.zegozero.service.FullCalendarService;
import com.codehows.zegozero.service.PlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class plan_api_controller {

    private final PlanService planService;
    private final FullCalendarService fullCalendarService;

    // 생산계획 개수와 제작수량 계산
    @GetMapping("/calculateProductionQuantity")
    public ResponseEntity<int[]> calculateProductionQuantity(@RequestParam String productName, @RequestParam int productionQuantity) {
        int[] result = planService.calculateProductionQuantity(productName, productionQuantity);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/fullCalendar")
    public ResponseEntity<List<Map<String, Object>>> getFullCalendar(){
        List<Plan_equipment> planEquipments = fullCalendarService.getAllPlanEquipments();
        List<Map<String, Object>> events = planEquipments.stream().map(pe -> {
            Map<String, Object> fullCalendar = new HashMap<>();
            fullCalendar.put("title", pe.getEquipment().getEquipment_name());
            fullCalendar.put("start", pe.getEstimated_start_date().toString());
            fullCalendar.put("end", pe.getEstimated_end_date().toString());
            fullCalendar.put("color", getColorByPlanId(pe.getPlan().getPlan_id()));
            return fullCalendar;
        }).toList();
        return ResponseEntity.ok(events);
    }

    private String getColorByPlanId(int planId){
        // plan_id에 따른 색상 지정 로직
        switch(planId % 10){
            case 0: return "#a63632";
            case 1: return "#17871d";
            case 2: return "#007c85";
            case 3: return "#0d017a";
            case 4: return "#990094";
            case 5: return "#ff5733";
            case 6: return "#33ff57";
            case 7: return "#3357ff";
            case 8: return "#ff33a1";
            case 9: return "#f3ff33";
            default: return "#000000";
        }
    }



}
