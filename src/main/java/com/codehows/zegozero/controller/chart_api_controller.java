package com.codehows.zegozero.controller;

import com.codehows.zegozero.dto.Output_Dto;
import com.codehows.zegozero.dto.Production_performance_Dto;
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

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    // 일별생산량 데이터
    @GetMapping("/dailyProduction")
    public List<Output_Dto> getDailyProduction(@RequestParam int month) throws ParseException {
        List<Output_Dto> dailyData = Arrays.asList(
                new Output_Dto(50, sdf.parse("2024-06-10")),
                new Output_Dto(70, sdf.parse("2024-06-11")),
                new Output_Dto(80, sdf.parse("2024-06-12")),
                new Output_Dto(60, sdf.parse("2024-06-13")),
                new Output_Dto(90, sdf.parse("2024-06-14")),
                new Output_Dto(40, sdf.parse("2024-06-15")),
                new Output_Dto(100, sdf.parse("2024-06-16")),
                new Output_Dto(90, sdf.parse("2024-07-16")),
                new Output_Dto(40, sdf.parse("2024-07-15")),
                new Output_Dto(100, sdf.parse("2024-07-15")),
                new Output_Dto(120, sdf.parse("2024-07-16")),
                new Output_Dto(60, sdf.parse("2024-07-24")),
                new Output_Dto(20, sdf.parse("2024-07-06")),
                new Output_Dto(40, sdf.parse("2024-07-01")),
                new Output_Dto(80, sdf.parse("2024-07-28"))

        );

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
        return Arrays.asList(
                new Production_performance_Dto(1, 300, 290),
                new Production_performance_Dto(2, 500, 490),
                new Production_performance_Dto(3, 400, 380),
                new Production_performance_Dto(4, 600, 590),
                new Production_performance_Dto(5, 700, 680),
                new Production_performance_Dto(6, 800, 780)
        );
    }
}
