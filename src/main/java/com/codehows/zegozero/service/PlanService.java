package com.codehows.zegozero.service;

import com.codehows.zegozero.dto.Equipment_plan_date_Dto;
import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Plan_equipment;
import com.codehows.zegozero.entity.Plans;
import com.codehows.zegozero.entity.Purchase_matarial;
import com.codehows.zegozero.repository.PlanEquipmentRepository;
import com.codehows.zegozero.repository.PlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PlanService {

    private final PlansRepository plansRepository;
    private final PlanEquipmentRepository planEquipmentRepository;
    private final TimeService timeService;

    int latestPlanQuantity = 0; // 기존 마지막 계획에 잡혀있는 수량
    int totalProductionQuantity = 0; // 실제작수량
    int numberOfPlans = 0; // 새로 만들어야하는 생산계획 수
    int[] result = new int[2];

    // 제품명과 제작수량을 받아서 새로운 계획을 만들지 기존 계획에 추가할지 판단
    @Transactional(readOnly = true)
    public int[] calculateProductionQuantity(String productName, int productionQuantity) {

        if(productName.equals("양배추즙")||productName.equals("흑마늘즙")){
            totalProductionQuantity = productionQuantity;
            numberOfPlans = 1;
            // 새로 만들어야할 계획 수
            while (totalProductionQuantity > 333) {
                totalProductionQuantity -= 333;
                numberOfPlans++;
            }

            result[0] = numberOfPlans;
            result[1] = totalProductionQuantity;

            return result;
        }else {

            totalProductionQuantity = productionQuantity;
            numberOfPlans = 1;
            // 새로 만들어야할 계획 수
            while (totalProductionQuantity > 160) {
                totalProductionQuantity -= 160;
                numberOfPlans++;
            }

            result[0] = numberOfPlans;
            result[1] = totalProductionQuantity;

            return result;
        }
    }

    public Plans findById(Integer id) {
        return plansRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    public Orders getOrderByPlanId(int planId) {
        return plansRepository.findOrderByPlanId(planId);
    }


    public List<Equipment_plan_date_Dto> findByOrderId(Orders orders){

        double Processing;
        LocalDateTime currentTime = timeService.getDateTimeFromDB().getTime();


        List<Equipment_plan_date_Dto> nowProcessing = new ArrayList<>();
        List<Plans> byOrderId = plansRepository.findByOrderId(orders);


        for(Plans plan : byOrderId){
            List<Plan_equipment> plans = planEquipmentRepository.findByPlans(plan);
            Processing = calculateProductionPercentage(plan,currentTime);
            for (Plan_equipment planEquipment : plans) {
                Equipment_plan_date_Dto Dto = new Equipment_plan_date_Dto(planEquipment, orders.getOrderId(),Processing);
                nowProcessing.add(Dto);
            }
        }

        System.out.println(nowProcessing);

        return nowProcessing;
    }

    private double calculateProductionPercentage(Plans p, LocalDateTime currentTime) {
        LocalDateTime estimatedStart = p.getStart_date();
        LocalDateTime estimatedEnd = p.getCompletion_date();

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


}

