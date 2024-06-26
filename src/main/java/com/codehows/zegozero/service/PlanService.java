package com.codehows.zegozero.service;

import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Plans;
import com.codehows.zegozero.repository.PlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class PlanService {

    private final PlansRepository plansRepository;

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
}
