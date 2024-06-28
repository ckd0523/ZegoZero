package com.codehows.zegozero.controller;

import com.codehows.zegozero.dto.Output_Dto;
import com.codehows.zegozero.dto.Production_performance_Dto;
import com.codehows.zegozero.repository.FinishProductRepository;
import com.codehows.zegozero.repository.OrdersRepository;
import com.codehows.zegozero.repository.PlanEquipmentRepository;
import com.codehows.zegozero.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class chart_api_controller {

    private final FinishProductRepository finishProductRepository;
    private final OrderService orderService;
    private final PlanEquipmentRepository planEquipmentRepository;

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // 일별생산량 데이터
    @GetMapping("/dailyProduction")
    public List<Output_Dto> getDailyProduction(@RequestParam int month) throws ParseException {

        List<Object[]> results = planEquipmentRepository.findTotalOutputByDate();

        // 결과를 Output_Dto 리스트로 변환
        List<Output_Dto> dailyData = new ArrayList<>();
        for (Object[] result : results) {
            Long totalQuantity = (Long) result[0];
            java.sql.Date date = (java.sql.Date) result[1];
            dailyData.add(new Output_Dto(totalQuantity.intValue(), date));
        }

        // 월별 데이터 필터링
        List<Output_Dto> filteredData = dailyData.stream()
                .filter(dto -> dto.getReceived_date().getMonth() == month)  // 월별 데이터 필터링
                .collect(Collectors.toList());

        // 날짜 순으로 정렬
        filteredData.sort(Comparator.comparing(Output_Dto::getReceived_date));

        return filteredData;
    }

    // 월별생산량 데이터
    @GetMapping("/monthlyProduction")
    public Map<String, Integer> getMonthlyProduction() throws ParseException {
        Map<String, Integer> monthlyProduction = new LinkedHashMap<>();

        // 월별로 생산량 합산
        for (int month = 0; month <= 11; month++) {
            List<Output_Dto> dailyData = getDailyProduction(month);
            int sum = dailyData.stream()
                    .mapToInt(Output_Dto::getReceived_quantity)
                    .sum();
            String monthStr = String.format("%d-%02d", 2024, month + 1); // 월 포맷 맞추기
            monthlyProduction.put(monthStr, sum);
        }

        return monthlyProduction;
    }

    // 생산실적 데이터
    @GetMapping("/performance")
    public List<Production_performance_Dto> getProductionPerformance() {

        List<Production_performance_Dto> chart2 = orderService.getChart2();

        // 배송 완료된 수주번호, 제작수량, 산출량
        return chart2;
    }
}
