package com.codehows.zegozero.controller;

import com.codehows.zegozero.dto.*;
import com.codehows.zegozero.entity.Plan_equipment;
import com.codehows.zegozero.entity.System_time;
import com.codehows.zegozero.service.PlanEquipmentService;
import com.codehows.zegozero.service.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class Plan_equipment_api_controller {

    private final PlanEquipmentService planEquipmentService;
    private final TimeService timeService;

    // 설비 3 및 설비 4의 마지막 예상 출고 날짜 비교
    @GetMapping("/earliestEndDate")
    public ResponseEntity<Object> getEquipmentDetails(
            @RequestParam String productName,
            @RequestParam int equipmentId3,
            @RequestParam int equipmentId4,
            @RequestParam int input) {

        planEquipmentService.clearTemporaryPlans();

        Object equipmentDetails = planEquipmentService.findEarliestEndDateForEquipments(productName, equipmentId3, equipmentId4, input);

        if (equipmentDetails != null) {
            return ResponseEntity.ok(equipmentDetails);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 설비2 계획 잡기
    @GetMapping("/id2Plan")
    public ResponseEntity<Equipment2_plan_date_Dto> id2Plan(
            @RequestParam int id2Input,
            @RequestParam int id2Output,
            @RequestParam int cleaningTimeMinutes,
            @RequestParam String cleaningStartDateTime){

        LocalDateTime startDateTime = LocalDateTime.parse(cleaningStartDateTime, DateTimeFormatter.ISO_DATE_TIME);
        Equipment2_plan_date_Dto equipment2Plan = planEquipmentService.createCleaningPlan(id2Input, id2Output, cleaningTimeMinutes, startDateTime);

        return ResponseEntity.ok(equipment2Plan);

    }

    // 설비1 계획 잡기
    @GetMapping("/id1Plan")
    public ResponseEntity<Equipment1_plan_date_Dto> id1Plan(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedStartDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime estimatedEndDate,
            @RequestParam int output) {

        Equipment1_plan_date_Dto equipment1Plan = planEquipmentService.createEquipment1Plan(estimatedStartDate, estimatedEndDate, output);

        return ResponseEntity.ok(equipment1Plan);
    }

    // 설비9 계획 잡기
    @GetMapping("/id9Plan")
    public ResponseEntity<Equipment9_plan_date_Dto> id9Plan(
            @RequestParam LocalDateTime id34EndDate,
            @RequestParam int id9Input) {

        Equipment9_plan_date_Dto equipment9Plan = planEquipmentService.createEquipment9Plan(id34EndDate, id9Input);

        return ResponseEntity.ok(equipment9Plan);
    }

    // 설비 56 계획 잡기
    @GetMapping("/id56Plan")
    public ResponseEntity<Object> id56Plan(
            @RequestParam String id56EndDate,
            @RequestParam int id56Input) {

        LocalDateTime startDateTime = LocalDateTime.parse(id56EndDate, DateTimeFormatter.ISO_DATE_TIME);
        Object equipment56Plan = planEquipmentService.id56Plan(startDateTime, id56Input);

        return ResponseEntity.ok(equipment56Plan);
    }

    // 충진기1,2(즙) 계획 잡기
    @GetMapping("/id10Plan")
    public ResponseEntity<Equipment10_plan_date_Dto> id10Plan(
            @RequestParam String id10StartDate,
            @RequestParam int id10Input) {

        LocalDateTime startDateTime = LocalDateTime.parse(id10StartDate, DateTimeFormatter.ISO_DATE_TIME);
        Equipment10_plan_date_Dto equipment10Plan = planEquipmentService.createEquipment10Plan(startDateTime, id10Input);

        return ResponseEntity.ok(equipment10Plan);
    }

    // 검사기 계획 잡기
    @GetMapping("/id13Plan")
    public ResponseEntity<Equipment13_plan_date_Dto> id13Plan(
            @RequestParam String id13StartDate,
            @RequestParam int id13Input) {

        LocalDateTime startDateTime = LocalDateTime.parse(id13StartDate, DateTimeFormatter.ISO_DATE_TIME);
        Equipment13_plan_date_Dto equipment13Plan = planEquipmentService.createEquipment13Plan(startDateTime, id13Input);

        return ResponseEntity.ok(equipment13Plan);
    }

    // Box포장기 계획 잡기
    @GetMapping("/id12Plan")
    public ResponseEntity<Equipment12_plan_date_Dto> id12Plan(
            @RequestParam String id12StartDate,
            @RequestParam int id12Input) {

        LocalDateTime startDateTime = LocalDateTime.parse(id12StartDate, DateTimeFormatter.ISO_DATE_TIME);
        Equipment12_plan_date_Dto equipment12Plan = planEquipmentService.createEquipment12Plan(startDateTime, id12Input);

        return ResponseEntity.ok(equipment12Plan);
    }

    // 생산계획 mysql에 저장
    @PostMapping("/saveNewPlan")
    public ResponseEntity<Void> saveNewPlan() {
        planEquipmentService.saveNewPlan();
        return ResponseEntity.ok().build();
    }

    // 모든 데이터 mysql에 저장
    @PostMapping("/saveAllEquipmentPlans")
    public ResponseEntity<Void> saveAllEquipmentPlans() {
        planEquipmentService.savePlanEquipments();
        return ResponseEntity.ok().build();
    }


    @GetMapping("/equipment/{equipmentId}")
    public Map<String, Object> getTodayPlansByEquipment(@PathVariable int equipmentId ) {

        LocalDate date = timeService.getDateTimeFromDB().getTime().toLocalDate();


        Map<String, Object> equipmentPlanResponse = new HashMap<>();

        // equipmentId와 date에 해당하는 계획을 조회하여 DTO로 매핑
        List<Equipment_plan_date_Dto> equipmentPlans = planEquipmentService.getPlansByEquipmentIdAndDate(equipmentId, date)
                .stream()
                .map(Equipment_plan_date_Dto::new) // Entity를 DTO로 변환
                .collect(Collectors.toList());

        equipmentPlanResponse.put("data", equipmentPlans);

        return equipmentPlanResponse;
    }


}
