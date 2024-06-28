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

            if(totalProductionQuantity == 0){
                numberOfPlans = 0;

                result[0] = numberOfPlans;
                result[1] = totalProductionQuantity;

                return result;

            }else {
                // 새로 만들어야할 계획 수
                while (totalProductionQuantity > 333) {
                    totalProductionQuantity -= 333;
                    numberOfPlans++;
                }

                result[0] = numberOfPlans;
                result[1] = totalProductionQuantity;

                return result;
            }


        }else {

            totalProductionQuantity = productionQuantity;
            numberOfPlans = 1;

            if(totalProductionQuantity == 0){
                numberOfPlans = 0;

                result[0] = numberOfPlans;
                result[1] = totalProductionQuantity;

                return result;

            }else{
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
    }

    public Plans findById(Integer id) {
        return plansRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Board not found"));
    }

    public Orders getOrderByPlanId(int planId) {
        return plansRepository.findOrderByPlanId(planId);
    }

    // 수주번호, 계획번호에 따른 현황 테이블
    public List<Plans> getRunningPlanEquipments(){
        return plansRepository.findAllByOrderShippingDateIsNull();
    }

    public List<Equipment_plan_date_Dto> findByOrderId(Orders orders){

        double Processing;
        LocalDateTime currentTime = timeService.getDateTimeFromDB().getTime();


        List<Equipment_plan_date_Dto> nowProcessing = new ArrayList<>();

        //orders객체로  매핑된 plans 객체를 리스트로 찾음,(수주테이블과 생산계획은 일대다 관계)
        List<Plans> byOrderId = plansRepository.findByOrderId(orders);



        //수주등록 시 딜레이가 있음. 이때 두번 클릭하면 수주가 두번 등록되는 문제 발생. 이
        //때 orders테이블에는 잘 저장되지만, plans에는 동일한 수주 번호로 등록되는 문제가 있음.
        //=>수주번호로 플랜을 조회할 수 없는 상황 발생(orders는 있으나 연관된 plan이 부재한 경우)
        //=>그래서 수주등록 시 주의해야함

        if(byOrderId.isEmpty()){
            throw new RuntimeException("수주 등록 오류로 인한 에러");
        }
        //각 plans객체를 통해 Plan_equipment를 리스트로 찾음('생산계획테이블'과 '설비별생산계획테이블'은 일대다 관계)
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

