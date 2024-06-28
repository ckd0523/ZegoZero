package com.codehows.zegozero.controller;

import com.codehows.zegozero.dto.Equipment_plan_date_Dto;

import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Plan_equipment;
import com.codehows.zegozero.entity.Plans;
import com.codehows.zegozero.service.FullCalendarService;
import com.codehows.zegozero.service.OrderService;
import com.codehows.zegozero.service.PlanService;
import com.codehows.zegozero.service.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class plan_api_controller {

    private final PlanService planService;
    private final OrderService orderService;
    private final FullCalendarService fullCalendarService;
    private final TimeService timeService;

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

    // 수주번호, 계획번호에 따른 현황 테이블
    @GetMapping("/runningTable")
    public List<Map<String, Object>> getRunningTableData() {
        List<Orders> runningPlans = orderService.getRunningPlanEquipments();
        LocalDateTime currentTime = timeService.getDateTimeFromDB().getTime();

        return runningPlans.stream().map(o -> {
            double production = calculateProductionPercentage(o, currentTime);
            return Map.<String, Object>of(
                    "order_id", o.getOrderId(),
                    "production", production,
                    "customer_name", o.getCustomer_name(),
                    "expected_shipping_date", o.getExpected_shipping_date()
            );
        }).collect(Collectors.toList());
    }

    private double calculateProductionPercentage(Orders o, LocalDateTime currentTime) {
        LocalDateTime estimatedStart = o.getOrder_date();
        LocalDateTime estimatedEnd = o.getExpected_shipping_date();

        if (estimatedStart == null || estimatedEnd == null) {
            return 0.0;
        }

        Duration totalDuration = Duration.between(estimatedStart, estimatedEnd);
        Duration elapsedDuration = Duration.between(estimatedStart, currentTime);

        if (elapsedDuration.isNegative() || totalDuration.isZero()) {
            return 0.0;
        } else if (elapsedDuration.compareTo(totalDuration) > 0) {
            return 100.0;
        }

        // 소수점 버림
        double percentage = (double) elapsedDuration.toMillis() / totalDuration.toMillis() * 100;
        return Math.floor(percentage); // 소수점 버림 처리
    }


//    @GetMapping("/nowOrderProgress/{inputValue}")
//    public ResponseEntity<List<Equipment_plan_date_Dto>> findnowOrderProgess(@PathVariable Integer inputValue) {
//
//
//        Orders orders = orderService.findById(inputValue);
//
//        List<Equipment_plan_date_Dto> Dto= planService.findByOrderId(orders);
//
//        System.out.println(Dto.get(0));
//
//        return ResponseEntity.ok()
//                .body(Dto);
//    }

//    @GetMapping("/nowOrderProgress/{inputValue}")
//    public Map<String,Object> findnowOrderProgess(@PathVariable Integer inputValue) {
//        Map<String, Object> A = new HashMap<>();
//
//        try {
//            Orders orders = orderService.findById(inputValue);
//
//            if (orders != null) {
//                List<Equipment_plan_date_Dto> Dto = planService.findByOrderId(orders);
//                A.put("Data", Dto);
//
//                return A;
//            }
//
//        }catch (Exception e) {
//            A.put("message", "등록되지 않은 수주 번호입니다");
//            A.put("error", e.getMessage());
//        }
//        return A;
//    }


    @GetMapping("/nowOrderProgress/{inputValue}")
    public ResponseEntity<Map<String, Object>> findnowOrderProgress(@PathVariable(required = false) String inputValue) {
        Map<String, Object> response = new HashMap<>();

        try {

            Integer orderId = Integer.parseInt(inputValue); // 입력 값 파싱

            Orders orders = orderService.findById(orderId);
            //findById매서드에서 예외를 던져주기 때문에, 여기서는 else구문이 작동할 여지가 없음

            if (orders != null) {
                List<Equipment_plan_date_Dto> dto = planService.findByOrderId(orders);
                response.put("Data", dto);

                return ResponseEntity.ok().body(response);
            }else{
                throw new NullPointerException();
            }

        } catch (NumberFormatException e) {
            response.put("message", "입력 값이 올바르지 않습니다: " + inputValue);
            return ResponseEntity.badRequest().body(response);
        } catch (NullPointerException e) {
            response.put("message", "해당 주문을 찾을 수 없습니다: " + "입력한 값 :"+ inputValue);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        } catch (Exception e) {
            response.put("message", "서버 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


}



