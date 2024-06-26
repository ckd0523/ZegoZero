package com.codehows.zegozero.service;

import com.codehows.zegozero.dto.*;
import com.codehows.zegozero.entity.Equipment;
import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Plan_equipment;
import com.codehows.zegozero.entity.Plans;
import com.codehows.zegozero.repository.PlanEquipmentRepository;
import com.codehows.zegozero.repository.PlansRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlanEquipmentService {

    private final PlanEquipmentRepository planEquipmentRepository;
    private final PlansRepository plansRepository;
    private final TimeService timeService;

    // 생산계획 Dto를 담을 리스트
    private final List<Plans> Plans = new ArrayList<>();

    // 설비별 Dto를 담을 리스트
    private final List<Plan_equipment> temporaryPlans = new CopyOnWriteArrayList<>();

    // 설비별 Dto를 담을 리스트
    private final List<Plan_equipment> temporaryPlans2 = new CopyOnWriteArrayList<>();



    // 젤리 계획 잡기
    public void zeliPlan(String productName, int input7) {

        //LocalDateTime currentDateTime = LocalDateTime.now();
        LocalDateTime currentDateTime = timeService.getDateTimeFromDB().getTime();
        LocalDateTime estimatedStartDate7;
        LocalDateTime estimatedEndDate7;
        LocalDateTime id1StartTime;

        // 설비 시작 시간에 따라 계산
        if (currentDateTime.getHour() < 15) {
            estimatedStartDate7 = currentDateTime.plusDays(3).withHour(9).truncatedTo(ChronoUnit.HOURS);
        } else {
            estimatedStartDate7 = currentDateTime.plusDays(4).withHour(9).truncatedTo(ChronoUnit.HOURS);
        }

        List<Plan_equipment> existingPlans7 = planEquipmentRepository.findAllByEquipmentEquipmentId(7);

        LocalDateTime adjustedStartTime7 = adjustStartTime7(estimatedStartDate7, existingPlans7);

        // 가동시간 8시간 추가
        estimatedEndDate7 = adjustedStartTime7.plusHours(8);

        int output7 = input7 * 25 * 10;

        // 혼합기 계획 저장
        Equipment equipment7 = new Equipment();
        equipment7.setEquipment_id(7);

        Plan_equipment plan7 = new Plan_equipment();
        plan7.setEquipment(equipment7);
        plan7.setEstimated_start_date(adjustedStartTime7);
        plan7.setEstimated_end_date(estimatedEndDate7);
        plan7.setInput(input7);
        plan7.setOutput(output7);
        temporaryPlans2.add(plan7);

        // 발주 계획 생성
        if (adjustedStartTime7.getHour() > 9 || adjustedStartTime7.getHour() == 9) {
            id1StartTime = adjustedStartTime7.minusDays(3).withHour(15).truncatedTo(ChronoUnit.HOURS);
        } else {
            id1StartTime = adjustedStartTime7.minusDays(4).withHour(15).truncatedTo(ChronoUnit.HOURS);
        }

        // 발주 계획 저장
        Equipment equipment1 = new Equipment();
        equipment1.setEquipment_id(1);

        Plan_equipment plan1 = new Plan_equipment();
        plan1.setEquipment(equipment1);
        plan1.setEstimated_start_date(id1StartTime);
        plan1.setEstimated_end_date(id1StartTime.plusDays(3).withHour(9).truncatedTo(ChronoUnit.HOURS));
        plan1.setInput(0);
        plan1.setOutput(input7*125);
        temporaryPlans2.add(plan1);
        
        // 살균기 설비 계획
        LocalDateTime id56EndDate = estimatedEndDate7;
        int id56Input = output7;

        // 설비 5와 설비 6의 기존 계획들을 가져옴
        List<Plan_equipment> existingPlans5 = planEquipmentRepository.findAllByEquipmentEquipmentId(5);
        List<Plan_equipment> existingPlans6 = planEquipmentRepository.findAllByEquipmentEquipmentId(6);

        // 설비 5와 6의 계획을 겹치지 않도록 조정하고 선택
        LocalDateTime adjustedStartTime5 = id56StartTime(id56EndDate, existingPlans5);
        LocalDateTime adjustedStartTime6 = id56StartTime(id56EndDate, existingPlans6);

        LocalDateTime selectedStartTime;
        int selectedEquipmentId;

        // 설비5와 6의 계획을 검토하여 가장 빠른 가능 시간을 선택
        if (adjustedStartTime5.isBefore(adjustedStartTime6)) {
            selectedStartTime = adjustedStartTime5;
            selectedEquipmentId = 5;
        } else {
            selectedStartTime = adjustedStartTime6;
            selectedEquipmentId = 6;
        }

        if (selectedEquipmentId == 5) {

            Equipment equipment5 = new Equipment();
            equipment5.setEquipment_id(selectedEquipmentId);

            Plan_equipment plan5 = new Plan_equipment();
            plan5.setEquipment(equipment5);
            plan5.setEstimated_start_date(selectedStartTime);
            plan5.setEstimated_end_date(selectedStartTime.plusHours(2));
            plan5.setInput(id56Input);
            plan5.setOutput(id56Input);
            temporaryPlans2.add(plan5);

        } else {

            Equipment equipment6 = new Equipment();
            equipment6.setEquipment_id(selectedEquipmentId);

            Plan_equipment plan6 = new Plan_equipment();
            plan6.setEquipment(equipment6);
            plan6.setEstimated_start_date(selectedStartTime);
            plan6.setEstimated_end_date(selectedStartTime.plusHours(2));
            plan6.setInput(id56Input);
            plan6.setOutput(id56Input);
            temporaryPlans2.add(plan6);

        }

        // 충진기3,4(젤리) 계획 생성
        LocalDateTime id11StartDate = selectedStartTime.plusHours(2);
        int id11Input = id56Input;
        // 투입량을 1시간에 4000개 처리할 수 있는 경우, 끝나는 시간 계산 (10분 단위로 올림)
        int processingRatePerHour = 4000;
        int totalProcessingTimeMinutes = (int) Math.ceil((double) id11Input / processingRatePerHour * 60 / 10) * 10;
        LocalDateTime id11EndDate = id11StartDate.plusMinutes(totalProcessingTimeMinutes);

        // 산출량 계산
        double defectRate = 0.01 + (0.03 - 0.01) * Math.random(); // 1~3% 랜덤 불량률
        int id11Output = (int) Math.floor((id11Input / 10) * (1 - defectRate));

        // 충진기3,4(젤리) 계획 저장
        Equipment equipment11 = new Equipment();
        equipment11.setEquipment_id(11);

        Plan_equipment plan11 = new Plan_equipment();
        plan11.setEquipment(equipment11);
        plan11.setEstimated_start_date(id11StartDate);
        plan11.setEstimated_end_date(id11EndDate);
        plan11.setInput(id56Input);
        plan11.setOutput(id11Output);
        temporaryPlans2.add(plan11);

        // 냉각 공정 계획 저장
        Equipment equipment8 = new Equipment();
        equipment8.setEquipment_id(8);

        Plan_equipment plan8 = new Plan_equipment();
        plan8.setEquipment(equipment8);
        plan8.setEstimated_start_date(id11EndDate);
        plan8.setEstimated_end_date(id11EndDate.plusHours(8));
        plan8.setInput(id11Output);
        plan8.setOutput(id11Output);
        temporaryPlans2.add(plan8);

        // 검사기 계획 생성
        LocalDateTime id13StartDate = id11EndDate.plusHours(8);
        int id13Input = id11Output;

        // 설비13 계획을 가져옴
        List<Plan_equipment> existingPlans = planEquipmentRepository.findAllByEquipmentEquipmentId(13);

        // 겹치지 않도록 시작 시간을 조정
        LocalDateTime adjustedStartTime = id13StartTime(id13StartDate, id13Input, existingPlans);

        // 처리 시간을 분 단위로 계산
        double processingTimeInHours = (double) id13Input / 5000;
        long processingTimeInMinutes = (long) Math.ceil(processingTimeInHours * 60);

        // 종료 시간 계산
        LocalDateTime id13EndDate = adjustedStartTime.plusMinutes(processingTimeInMinutes);

        // 종료 시간을 10분 단위로 올림
        id13EndDate = roundUpToNearest10Minutes(id13EndDate);

        // 검사기 계획 저장
        Equipment equipment13 = new Equipment();
        equipment13.setEquipment_id(13);

        Plan_equipment plan13 = new Plan_equipment();
        plan13.setEquipment(equipment13);
        plan13.setEstimated_start_date(adjustedStartTime);
        plan13.setEstimated_end_date(id13EndDate);
        plan13.setInput(id13Input);
        plan13.setOutput(id13Input);
        temporaryPlans2.add(plan13);

        // Box 포장기 계획 생성
        LocalDateTime id12StartDate = id13EndDate;
        int id12Input = id13Input;

        // 박스 수 계산 (나머지는 버림)
        int boxes = id12Input / 25;

        // 필요한 가동 시간 계산 (시간과 분으로 분리)
        int processingHours = (int) Math.floor(boxes / 160);  // 시간
        int processingMinutes = (int) Math.ceil((boxes % 160) * 60.0 / 160);  // 분

        // 이미 계획된 설비 12의 계획들을 가져옴
        List<Plan_equipment> existing12Plans = planEquipmentRepository.findAllByEquipmentEquipmentId(12);

        // 겹치지 않도록 시작 시간을 조정
        LocalDateTime adjusted12StartTime = adjustStartTimeForEquipment12(id12StartDate, existing12Plans, processingHours, processingMinutes);

        // 종료시간 계산
        LocalDateTime endTime = adjusted12StartTime.plusHours(processingHours).plusMinutes(processingMinutes);

        // 종료 시간을 10분 단위로 올림
        endTime = roundUpToNearest10Minutes(endTime);

        // 포장기 계획 저장
        Equipment equipment12 = new Equipment();
        equipment12.setEquipment_id(12);

        Plan_equipment plan12 = new Plan_equipment();
        plan12.setEquipment(equipment12);
        plan12.setEstimated_start_date(adjusted12StartTime);
        plan12.setEstimated_end_date(endTime);
        plan12.setInput(id12Input);
        plan12.setOutput(boxes);
        temporaryPlans2.add(plan12);

        // 생산계획 dto 저장
        Plans newplan = new Plans();
        newplan.setProduct_name(productName);
        newplan.setPlanned_quantity(input7);
        newplan.setStatus("planned");
        newplan.setStart_date(id1StartTime);
        newplan.setCompletion_date(endTime);
        Plans.add(newplan);

    }

    // 설비7기존 계획과 겹치지 않도록 시작 시간을 조정하는 메서드
    private LocalDateTime adjustStartTime7(LocalDateTime proposedStartTime, List<Plan_equipment> existingPlans) {
        LocalDateTime adjustedTime = proposedStartTime;

        for (Plan_equipment plan : existingPlans) {
            LocalDateTime existingStart = plan.getEstimated_start_date();
            LocalDateTime existingEnd = plan.getEstimated_end_date();

            // 겹치는 경우
            if (adjustedTime.isBefore(existingEnd) && adjustedTime.plusHours(8).plusHours(1).isAfter(existingStart)) {
                adjustedTime = existingEnd.plusHours(1); // 기존 계획의 종료 시간 이후로 조정
            }
        }

        return adjustedTime;
    }

    // 설비3 및 설비4의 마지막 예상 출고 날짜 비교
    @Transactional(readOnly = true)
    public Object findEarliestEndDateForEquipments(String productName, int equipmentId3, int equipmentId4, int input) {

        // 생산계획 dto 저장
        Plans newplan = new Plans();
        newplan.setProduct_name(productName);
        newplan.setPlanned_quantity(input);
        newplan.setStatus("planned");
        Plans.add(newplan);

        Optional<Plan_equipment> equipment1Optional = planEquipmentRepository.findLatestPlanEquipmentByEquipmentId(equipmentId3);
        Optional<Plan_equipment> equipment2Optional = planEquipmentRepository.findLatestPlanEquipmentByEquipmentId(equipmentId4);

        // input 값에 * 4 * 0.75를 한 값을 투입량으로 사용
        int inputCalculated = (int) (input * 4 * 0.75);

        // 산출량
        int output = (inputCalculated * 1000) / 5;

        // 현재 한국 시간
        LocalDateTime now = timeService.getDateTimeFromDB().getTime();
        //LocalDateTime now = LocalDateTime.now(ZoneId.of("UTC"));

        // 설비 3과 설비 4 모두 데이터가 없는 경우
        if (equipment1Optional.isEmpty() && equipment2Optional.isEmpty()) {
            // 현재 시간에 3일을 더한 날짜를 반환하되, 10분 단위로 올림 처리
            LocalDateTime currentDatePlus3Days = now.plusDays(3);
            int equipmentId = 3;

            Equipment equipment = new Equipment();
            equipment.setEquipment_id(equipmentId);

            Plan_equipment plan = new Plan_equipment();
            plan.setEquipment(equipment);
            plan.setEstimated_start_date(roundUpToNearest10Minutes(currentDatePlus3Days));
            plan.setEstimated_end_date(roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)));
            plan.setInput(inputCalculated);
            plan.setOutput(output);
            temporaryPlans.add(plan);
            return new Equipment3_plan_date_Dto(
                    equipmentId,
                    roundUpToNearest10Minutes(currentDatePlus3Days),
                    roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)),
                    inputCalculated,
                    output
            );
        }

        // 설비 3만 데이터가 있는 경우
        if (equipment1Optional.isPresent() && equipment2Optional.isEmpty()) {
            LocalDateTime endDate3 = equipment1Optional.get().getEstimated_end_date();
            // 현재 시간에 1일과 23시간을 더한 날짜
            LocalDateTime currentDatePlus1Day23Hours = now.plusDays(1).plusHours(23);

            // 현재 시간 + 1일과 23시간보다 빠른 경우
            if (endDate3 != null && endDate3.isBefore(currentDatePlus1Day23Hours)) {
                // 현재 시간에 3일을 더한 날짜를 반환하되, 10분 단위로 올림 처리
                LocalDateTime currentDatePlus3Days = now.plusDays(3);
                int equipmentId = 3;

                Equipment equipment = new Equipment();
                equipment.setEquipment_id(equipmentId);

                Plan_equipment plan = new Plan_equipment();
                plan.setEquipment(equipment);
                plan.setEstimated_start_date(roundUpToNearest10Minutes(currentDatePlus3Days));
                plan.setEstimated_end_date(roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)));
                plan.setInput(inputCalculated);
                plan.setOutput(output);
                temporaryPlans.add(plan);
                return new Equipment3_plan_date_Dto(
                        equipmentId,
                        roundUpToNearest10Minutes(currentDatePlus3Days),
                        roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)),
                        inputCalculated,
                        output
                );
            } else {
                // 현재 시간에 3일을 더한 날짜를 반환하되, 10분 단위로 올림 처리
                LocalDateTime currentDatePlus3Days = now.plusDays(3);
                int equipmentId = 4;

                Equipment equipment = new Equipment();
                equipment.setEquipment_id(equipmentId);

                Plan_equipment plan = new Plan_equipment();
                plan.setEquipment(equipment);
                plan.setEstimated_start_date(roundUpToNearest10Minutes(currentDatePlus3Days));
                plan.setEstimated_end_date(roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)));
                plan.setInput(inputCalculated);
                plan.setOutput(output);
                temporaryPlans.add(plan);
                return new Equipment4_plan_date_Dto(
                        equipmentId,
                        roundUpToNearest10Minutes(currentDatePlus3Days),
                        roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)),
                        inputCalculated,
                        output
                );
            }
        }

        // 설비 4만 데이터가 있는 경우
        if (equipment2Optional.isPresent() && equipment1Optional.isEmpty()) {
            LocalDateTime endDate4 = equipment2Optional.get().getEstimated_end_date();
            // 현재 시간에 1일과 23시간을 더한 날짜
            LocalDateTime currentDatePlus1Day23Hours = now.plusDays(1).plusHours(23);

            // 현재 시간 + 1일과 23시간보다 빠른 경우
            if (endDate4 != null && endDate4.isBefore(currentDatePlus1Day23Hours)) {
                // 현재 시간에 3일을 더한 날짜를 반환하되, 10분 단위로 올림 처리
                LocalDateTime currentDatePlus3Days = now.plusDays(3);
                int equipmentId = 3;

                Equipment equipment = new Equipment();
                equipment.setEquipment_id(equipmentId);

                Plan_equipment plan = new Plan_equipment();
                plan.setEquipment(equipment);
                plan.setEstimated_start_date(roundUpToNearest10Minutes(currentDatePlus3Days));
                plan.setEstimated_end_date(roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)));
                plan.setInput(inputCalculated);
                plan.setOutput(output);
                temporaryPlans.add(plan);
                return new Equipment3_plan_date_Dto(
                        equipmentId,
                        roundUpToNearest10Minutes(currentDatePlus3Days),
                        roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)),
                        inputCalculated,
                        output
                );
            } else {
                // 현재 시간에 3일을 더한 날짜를 반환하되, 10분 단위로 올림 처리
                LocalDateTime currentDatePlus3Days = now.plusDays(3);
                int equipmentId = 4;

                Equipment equipment = new Equipment();
                equipment.setEquipment_id(equipmentId);

                Plan_equipment plan = new Plan_equipment();
                plan.setEquipment(equipment);
                plan.setEstimated_start_date(roundUpToNearest10Minutes(currentDatePlus3Days));
                plan.setEstimated_end_date(roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)));
                plan.setInput(inputCalculated);
                plan.setOutput(output);
                temporaryPlans.add(plan);
                return new Equipment4_plan_date_Dto(
                        equipmentId,
                        roundUpToNearest10Minutes(currentDatePlus3Days),
                        roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)),
                        inputCalculated,
                        output
                );
            }
        }

        // 설비 3과 설비 4 모두 데이터가 있는 경우
        LocalDateTime endDate3 = equipment1Optional.get().getEstimated_end_date();
        LocalDateTime endDate4 = equipment2Optional.get().getEstimated_end_date();

        // 현재 시간에 1일과 23시간을 더한 날짜
        LocalDateTime currentDatePlus1Day23Hours = now.plusDays(1).plusHours(23);

        // 현재 시간 + 1일과 23시간보다 빠른 경우
        if ((endDate3 != null && endDate3.isBefore(currentDatePlus1Day23Hours))
                || (endDate4 != null && endDate4.isBefore(currentDatePlus1Day23Hours))) {
            // 현재 시간에 3일을 더한 날짜를 반환하되, 10분 단위로 올림 처리
            LocalDateTime currentDatePlus3Days = now.plusDays(3);
            if (endDate3 != null && endDate3.isBefore(currentDatePlus1Day23Hours)) {
                int equipmentId = 3;

                Equipment equipment = new Equipment();
                equipment.setEquipment_id(equipmentId);

                Plan_equipment plan = new Plan_equipment();
                plan.setEquipment(equipment);
                plan.setEstimated_start_date(roundUpToNearest10Minutes(currentDatePlus3Days));
                plan.setEstimated_end_date(roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)));
                plan.setInput(inputCalculated);
                plan.setOutput(output);
                temporaryPlans.add(plan);
                return new Equipment3_plan_date_Dto(
                        equipmentId,
                        roundUpToNearest10Minutes(currentDatePlus3Days),
                        roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)),
                        inputCalculated,
                        output
                );
            } else if (endDate4 != null && endDate4.isBefore(currentDatePlus1Day23Hours)) {
                int equipmentId = 4;

                Equipment equipment = new Equipment();
                equipment.setEquipment_id(equipmentId);

                Plan_equipment plan = new Plan_equipment();
                plan.setEquipment(equipment);
                plan.setEstimated_start_date(roundUpToNearest10Minutes(currentDatePlus3Days));
                plan.setEstimated_end_date(roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)));
                plan.setInput(inputCalculated);
                plan.setOutput(output);
                temporaryPlans.add(plan);
                return new Equipment4_plan_date_Dto(
                        equipmentId,
                        roundUpToNearest10Minutes(currentDatePlus3Days),
                        roundUpToNearest10Minutes(currentDatePlus3Days.plusDays(1)),
                        inputCalculated,
                        output
                );
            }
        } else {
            // 설비 예상 종료 시간 중에서 더 빠른 것 반환 후 +1일 1시간 추가
            LocalDateTime earliestEndDate = endDate3.isBefore(endDate4) ? endDate3 : endDate4;
            LocalDateTime modifiedEndDate = earliestEndDate.plusDays(1).plusHours(1);
            LocalDateTime modifiedEndDateAdd1Hour = modifiedEndDate.plusDays(1);

            if (earliestEndDate.equals(endDate3)) {
                int equipmentId = 3;

                Equipment equipment = new Equipment();
                equipment.setEquipment_id(equipmentId);

                Plan_equipment plan = new Plan_equipment();
                plan.setEquipment(equipment);
                plan.setEstimated_start_date(roundUpToNearest10Minutes(roundUpToNearest10Minutes(modifiedEndDate)));
                plan.setEstimated_end_date(roundUpToNearest10Minutes(roundUpToNearest10Minutes(modifiedEndDateAdd1Hour)));
                plan.setInput(inputCalculated);
                plan.setOutput(output);
                temporaryPlans.add(plan);
                return new Equipment3_plan_date_Dto(
                        equipmentId,
                        roundUpToNearest10Minutes(modifiedEndDate),
                        roundUpToNearest10Minutes(modifiedEndDateAdd1Hour),
                        inputCalculated,
                        output
                );
            } else {
                int equipmentId = 4;

                Equipment equipment = new Equipment();
                equipment.setEquipment_id(equipmentId);

                Plan_equipment plan = new Plan_equipment();
                plan.setEquipment(equipment);
                plan.setEstimated_start_date(roundUpToNearest10Minutes(roundUpToNearest10Minutes(modifiedEndDate)));
                plan.setEstimated_end_date(roundUpToNearest10Minutes(roundUpToNearest10Minutes(modifiedEndDateAdd1Hour)));
                plan.setInput(inputCalculated);
                plan.setOutput(output);
                temporaryPlans.add(plan);
                return new Equipment4_plan_date_Dto(
                        equipmentId,
                        roundUpToNearest10Minutes(modifiedEndDate),
                        roundUpToNearest10Minutes(modifiedEndDateAdd1Hour),
                        inputCalculated,
                        output
                );
            }
        }

        // 예외 상황 처리 - 이 코드는 실제로는 실행되지 않아야 하지만, 컴파일 오류를 방지하기 위해 넣었습니다.
        return null;
    }

    // 세척 계획을 생성하는 메서드
    public Equipment2_plan_date_Dto createCleaningPlan(int id2Input, int id2Output, int cleaningTimeMinutes, LocalDateTime cleaningStartDateTime) {
        // id2 설비를 찾는 메서드
        List<Plan_equipment> existingPlans = planEquipmentRepository.findAllByEquipmentEquipmentId(2);

        // 겹치지 않도록 시작 시간을 조정
        LocalDateTime adjustedStartTime = id2StartTime(cleaningStartDateTime, cleaningTimeMinutes, existingPlans);

        // 종료 시간 계산
        LocalDateTime endTime = adjustedStartTime.plusMinutes(cleaningTimeMinutes);

        Equipment equipment = new Equipment();
        equipment.setEquipment_id(2);

        Plan_equipment plan = new Plan_equipment();
        plan.setEquipment(equipment);
        plan.setEstimated_start_date(adjustedStartTime);
        plan.setEstimated_end_date(endTime);
        plan.setInput(id2Input);
        plan.setOutput(id2Output);
        temporaryPlans.add(plan);

        // DTO 반환
        return new Equipment2_plan_date_Dto(2, adjustedStartTime, endTime, id2Input, id2Output);
    }

    // 발주 계획 생성 메서드
    public Equipment1_plan_date_Dto createEquipment1Plan(LocalDateTime estimatedStartDate, int output) {

        LocalDateTime endTime = estimatedStartDate.minusHours(9).withHour(9).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime startTime = endTime.minusDays(1).minusHours(21);

        Equipment equipment = new Equipment();
        equipment.setEquipment_id(1);

        Plan_equipment plan = new Plan_equipment();
        plan.setEquipment(equipment);
        plan.setEstimated_start_date(startTime);
        plan.setEstimated_end_date(endTime);
        plan.setInput(0);
        plan.setOutput(output);
        temporaryPlans.add(plan);

        // DTO 반환
        return new Equipment1_plan_date_Dto(1, startTime, endTime, 0, output);
    }

    // 여과기 계획 생성 메서드
    public Equipment9_plan_date_Dto createEquipment9Plan(LocalDateTime id34EndDate, int id9Input) {
        // 설비9 계획 시작 시간 설정 (id34EndDate를 기준으로 시작)
        LocalDateTime proposedStartTime = id34EndDate;

        // 서비스에서 가져온 설비9 계획들
        List<Plan_equipment> existingPlans = planEquipmentRepository.findAllByEquipmentEquipmentId(9);

        // 겹치지 않도록 시작 시간을 조정
        LocalDateTime id9StartTime = id9StartTime(proposedStartTime, existingPlans);

        Equipment equipment = new Equipment();
        equipment.setEquipment_id(9);

        Plan_equipment plan = new Plan_equipment();
        plan.setEquipment(equipment);
        plan.setEstimated_start_date(id9StartTime);
        plan.setEstimated_end_date(id9StartTime.plusHours(4));
        plan.setInput(id9Input);
        plan.setOutput((int) Math.floor(id9Input * 0.5));
        temporaryPlans.add(plan);


        // DTO에 저장
        Equipment9_plan_date_Dto equipmentDto = new Equipment9_plan_date_Dto(
                9,
                id9StartTime,
                id9StartTime.plusHours(4), // 조정된 시작 시간에 4시간을 더하여 종료 시간 설정
                id9Input,
                (int) Math.floor(id9Input * 0.5) // 여과기 산출량은 투입량의 50%
        );

        // DTO 반환
        return equipmentDto;
    }

    // 살균기 설비 계획 수립
    public Object id56Plan(LocalDateTime id56EndDate, int id56Input) {
        // 설비 5와 설비 6의 기존 계획들을 가져옴
        List<Plan_equipment> existingPlans5 = planEquipmentRepository.findAllByEquipmentEquipmentId(5);
        List<Plan_equipment> existingPlans6 = planEquipmentRepository.findAllByEquipmentEquipmentId(6);

        // 설비 5와 6의 계획을 겹치지 않도록 조정하고 선택
        LocalDateTime adjustedStartTime5 = id56StartTime(id56EndDate, existingPlans5);
        LocalDateTime adjustedStartTime6 = id56StartTime(id56EndDate, existingPlans6);

        LocalDateTime selectedStartTime;
        int selectedEquipmentId;

        // 설비5와 6의 계획을 검토하여 가장 빠른 가능 시간을 선택
        if (adjustedStartTime5.isBefore(adjustedStartTime6)) {
            selectedStartTime = adjustedStartTime5;
            selectedEquipmentId = 5;
        } else {
            selectedStartTime = adjustedStartTime6;
            selectedEquipmentId = 6;
        }

        if (selectedEquipmentId == 5) {

            Equipment equipment = new Equipment();
            equipment.setEquipment_id(selectedEquipmentId);

            Plan_equipment plan = new Plan_equipment();
            plan.setEquipment(equipment);
            plan.setEstimated_start_date(selectedStartTime);
            plan.setEstimated_end_date(selectedStartTime.plusHours(2));
            plan.setInput(id56Input);
            plan.setOutput(id56Input);
            temporaryPlans.add(plan);

            Equipment5_plan_date_Dto equipmentDto = new Equipment5_plan_date_Dto(
                    selectedEquipmentId,
                    selectedStartTime,
                    selectedStartTime.plusHours(2), // 조정된 시작 시간에 2시간을 더하여 종료 시간 설정
                    id56Input,
                    id56Input
            );
            return equipmentDto;
        } else {

            Equipment equipment = new Equipment();
            equipment.setEquipment_id(selectedEquipmentId);

            Plan_equipment plan = new Plan_equipment();
            plan.setEquipment(equipment);
            plan.setEstimated_start_date(selectedStartTime);
            plan.setEstimated_end_date(selectedStartTime.plusHours(2));
            plan.setInput(id56Input);
            plan.setOutput(id56Input);
            temporaryPlans.add(plan);

            Equipment6_plan_date_Dto equipmentDto = new Equipment6_plan_date_Dto(
                    selectedEquipmentId,
                    selectedStartTime,
                    selectedStartTime.plusHours(2), // 조정된 시작 시간에 2시간을 더하여 종료 시간 설정
                    id56Input,
                    id56Input
            );
            return equipmentDto;
        }
    }

    // 충진기1,2(즙) 계획 생성 메서드
    public Equipment10_plan_date_Dto createEquipment10Plan(LocalDateTime id10StartDate, int id10Input) {
        // 설비10 계획을 가져옴
        List<Plan_equipment> existingPlans = planEquipmentRepository.findAllByEquipmentEquipmentId(10);

        // 겹치지 않도록 시작 시간을 조정
        LocalDateTime adjustedStartTime = id10StartTime(id10StartDate, id10Input, existingPlans);

        // 처리 시간을 분 단위로 계산
        double processingTimeInHours = (double) (id10Input / 10) / 2500;
        long processingTimeInMinutes = (long) Math.ceil(processingTimeInHours * 60);

        // 종료 시간 계산
        LocalDateTime endTime = adjustedStartTime.plusMinutes(processingTimeInMinutes);

        // 종료 시간을 10분 단위로 올림
        endTime = roundUpToNearest10Minutes(endTime);

        // id10Input에 10을 나누고 불량률 1~3% 랜덤 난수 적용
        int id10Output = (int) (id10Input / 10 * (0.97 + (Math.random() * 0.03)));

        Equipment equipment = new Equipment();
        equipment.setEquipment_id(10);

        Plan_equipment plan = new Plan_equipment();
        plan.setEquipment(equipment);
        plan.setEstimated_start_date(adjustedStartTime);
        plan.setEstimated_end_date(endTime);
        plan.setInput(id10Input);
        plan.setOutput(id10Output);
        temporaryPlans.add(plan);

        // DTO 반환
        return new Equipment10_plan_date_Dto(10, adjustedStartTime, endTime, id10Input, id10Output);
    }

    // 검사기 계획 생성 메서드
    public Equipment13_plan_date_Dto createEquipment13Plan(LocalDateTime id13StartDate, int id13Input) {
        // 설비13 계획을 가져옴
        List<Plan_equipment> existingPlans = planEquipmentRepository.findAllByEquipmentEquipmentId(13);

        // 겹치지 않도록 시작 시간을 조정
        LocalDateTime adjustedStartTime = id13StartTime(id13StartDate, id13Input, existingPlans);

        // 처리 시간을 분 단위로 계산
        double processingTimeInHours = (double) id13Input / 5000;
        long processingTimeInMinutes = (long) Math.ceil(processingTimeInHours * 60);

        // 종료 시간 계산
        LocalDateTime endTime = adjustedStartTime.plusMinutes(processingTimeInMinutes);

        // 종료 시간을 10분 단위로 올림
        endTime = roundUpToNearest10Minutes(endTime);

        Equipment equipment = new Equipment();
        equipment.setEquipment_id(13); // 여기서 12는 설비 번호

        Plan_equipment plan = new Plan_equipment();
        plan.setEquipment(equipment);
        plan.setEstimated_start_date(adjustedStartTime);
        plan.setEstimated_end_date(endTime);
        plan.setInput(id13Input);
        plan.setOutput(id13Input);
        temporaryPlans.add(plan);

        // DTO 반환
        return new Equipment13_plan_date_Dto(13, adjustedStartTime, endTime, id13Input, id13Input);
    }

    // Box 포장기 계획 생성 메서드
    public Equipment12_plan_date_Dto createEquipment12Plan(LocalDateTime id12StartDate, int id12Input) {
        // 박스 수 계산 (나머지는 버림)
        int boxes = id12Input / 30;

        // 필요한 가동 시간 계산 (시간과 분으로 분리)
        int processingHours = (int) Math.floor(boxes / 160);  // 시간
        int processingMinutes = (int) Math.ceil((boxes % 160) * 60.0 / 160);  // 분

        // 이미 계획된 설비 12의 계획들을 가져옴
        List<Plan_equipment> existingPlans = planEquipmentRepository.findAllByEquipmentEquipmentId(12);

        // 겹치지 않도록 시작 시간을 조정
        LocalDateTime adjustedStartTime = adjustStartTimeForEquipment12(id12StartDate, existingPlans, processingHours, processingMinutes);

        // 종료시간 계산
        LocalDateTime endTime = adjustedStartTime.plusHours(processingHours).plusMinutes(processingMinutes);

        // 종료 시간을 10분 단위로 올림
        endTime = roundUpToNearest10Minutes(endTime);

        Equipment equipment = new Equipment();
        equipment.setEquipment_id(12); // 여기서 12는 설비 번호

        Plan_equipment plan = new Plan_equipment();
        plan.setEquipment(equipment); // Equipment 객체를 설정
        plan.setEstimated_start_date(adjustedStartTime);
        plan.setEstimated_end_date(endTime);
        plan.setInput(id12Input);
        plan.setOutput(boxes);
        temporaryPlans.add(plan);

        // DTO 반환
        return new Equipment12_plan_date_Dto(12, adjustedStartTime, endTime, id12Input, boxes);
    }

    // 설비 12의 계획을 조정하는 메서드
    private LocalDateTime adjustStartTimeForEquipment12(LocalDateTime proposedStartTime, List<Plan_equipment> existingPlans, int processingHours, int processingMinutes ) {
        for (Plan_equipment plan : existingPlans) {
            LocalDateTime existingStart = plan.getEstimated_start_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
            LocalDateTime existingEnd = plan.getEstimated_end_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();

            // 제안된 시작 시간이 기존 계획의 끝 이전에 있고, 제안된 끝 시간이 기존 계획의 시작 이후인 경우
            if (proposedStartTime.isBefore(existingEnd) && proposedStartTime.plusHours(processingHours).plusMinutes(processingMinutes).plusMinutes(30).isAfter(existingStart)) {
                // 제안된 시작 시간을 기존 계획 종료 시간 이후로 조정
                proposedStartTime = existingEnd.plusMinutes(30);
            }
        }
        return proposedStartTime;
    }

    // 세척 시작 시간을 조정하는 메서드
    private LocalDateTime id2StartTime(LocalDateTime proposedStartTime, int cleaningTimeMinutes, List<Plan_equipment> existingPlans) {
        for (Plan_equipment plan : existingPlans) {
            LocalDateTime existingStart = plan.getEstimated_start_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
            LocalDateTime existingEnd = plan.getEstimated_end_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();

            // 제안된 시작 시간이 기존 계획의 끝 이전에 있고, 제안된 끝 시간이 기존 계획의 시작 이후인 경우
            if (proposedStartTime.isBefore(existingEnd) && proposedStartTime.plusMinutes(cleaningTimeMinutes).isAfter(existingStart)) {
                // 제안된 시작 시간을 기존 계획 시작 시간 이전으로 조정
                proposedStartTime = existingStart.minusMinutes(cleaningTimeMinutes);
            }
        }
        return proposedStartTime;
    }

    // 여과기 시작 시간을 조정하는 메서드
    private LocalDateTime id9StartTime(LocalDateTime proposedStartTime, List<Plan_equipment> existingPlans) {
        for (Plan_equipment plan : existingPlans) {
            LocalDateTime existingStart = plan.getEstimated_start_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
            LocalDateTime existingEnd = plan.getEstimated_end_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime()
                    .plusHours(1); // 기존 계획 종료 시간에 1시간 추가

            // 제안된 시작 시간이 기존 계획의 끝 이전에 있고, 제안된 끝 시간이 기존 계획의 시작 이후인 경우
            if (proposedStartTime.isBefore(existingEnd) && proposedStartTime.plusHours(5).isAfter(existingStart)) {
                // 제안된 시작 시간을 기존 계획 시작 시간 이후로 조정
                proposedStartTime = existingEnd;
            }
        }
        return proposedStartTime;
    }

    // 살균기 시간을 조정하는 메서드
    private LocalDateTime id56StartTime(LocalDateTime proposedStartTime, List<Plan_equipment> existingPlans) {
        LocalDateTime adjustedTime = proposedStartTime;

        for (Plan_equipment plan : existingPlans) {
            LocalDateTime existingStart = plan.getEstimated_start_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
            LocalDateTime existingEnd = plan.getEstimated_end_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime().plusHours(1); // 기존 계획 종료 시간에 1시간 추가

            if (adjustedTime.isBefore(existingEnd) && adjustedTime.plusHours(3).isAfter(existingStart)) {
                adjustedTime = existingEnd;
            }
        }

        return adjustedTime;
    }

    // 충진기 시작 시간을 조정하는 메서드
    private LocalDateTime id10StartTime(LocalDateTime proposedStartTime, int id10Input, List<Plan_equipment> existingPlans) {
        for (Plan_equipment plan : existingPlans) {
            LocalDateTime existingStart = plan.getEstimated_start_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
            LocalDateTime existingEnd = plan.getEstimated_end_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();

            // 제안된 시작 시간이 기존 계획의 끝 이전에 있고, 제안된 끝 시간이 기존 계획의 시작 이후인 경우
            if (proposedStartTime.isBefore(existingEnd) && proposedStartTime.plusHours((int) Math.ceil((double) (id10Input/10) / 4000)).plusMinutes(20).isAfter(existingStart)) {
                // 제안된 시작 시간을 기존 계획 종료 시간 이후로 조정
                proposedStartTime = existingEnd.plusMinutes(20);
            }
        }
        return proposedStartTime;
    }

    // 검사기 시작 시간을 조정하는 메서드
    private LocalDateTime id13StartTime(LocalDateTime proposedStartTime, int id13Input, List<Plan_equipment> existingPlans) {
        for (Plan_equipment plan : existingPlans) {
            LocalDateTime existingStart = plan.getEstimated_start_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();
            LocalDateTime existingEnd = plan.getEstimated_end_date()
                    .atZone(ZoneId.of("UTC"))
                    .toLocalDateTime();

            // 제안된 시작 시간이 기존 계획의 끝 이전에 있고, 제안된 끝 시간이 기존 계획의 시작 이후인 경우
            if (proposedStartTime.isBefore(existingEnd) && proposedStartTime.plusHours((int) Math.ceil((double) id13Input / 5000)).plusMinutes(10).isAfter(existingStart)) {
                // 제안된 시작 시간을 기존 계획 종료 시간 이후로 조정
                proposedStartTime = existingEnd.plusMinutes(10);
            }
        }
        return proposedStartTime;
    }

    // 주어진 날짜를 10분 단위로 올림하여 반환하는 메서드
    private LocalDateTime roundUpToNearest10Minutes(LocalDateTime dateTime) {
        int minute = dateTime.getMinute();
        int remainder = minute % 10;
        if (remainder != 0) {
            dateTime = dateTime.plusMinutes(10 - remainder);
        }
        return dateTime.withSecond(0).withNano(0);
    }
    
    // 생산계획 데이터베이스 저장
    @Transactional
    public void saveNewPlan(){
        plansRepository.saveAll(Plans);
        Plans.clear();
    }

    // 최종적으로 엔티티를 데이터베이스에 저장
    @Transactional
    public void savePlanEquipments() {
        int maxPlanId = getMaxPlanId(); // 최대 Plan ID를 가져옴

        // temporaryPlans의 각 요소에 maxPlanId를 설정하고 Plans 객체를 연결합니다.
        for (Plan_equipment plan : temporaryPlans) {
            Plans plans = new Plans();
            plans.setPlan_id(maxPlanId); // 설정할 Plan ID
            plan.setPlan(plans); // Plan_equipment 객체의 Plans에 plan_id 설정
        }

        // temporaryPlans를 데이터베이스에 저장합니다.
        planEquipmentRepository.saveAll(temporaryPlans);

        temporaryPlans.clear(); // 저장 후 임시 리스트 초기화

        // 설비3인 칼럼에서 시작시간 가져오기
        LocalDateTime startDate = planEquipmentRepository.findStartDateByPlanIdAndEquipmentId(maxPlanId);

        // 설비12인 칼럼에서 종료시간 가져오기
        LocalDateTime endDate = planEquipmentRepository.findEndDateByPlanIdAndEquipmentId(maxPlanId);

        // Plans 엔티티 업데이트
        Plans planToUpdate = plansRepository.findByPlanId(maxPlanId);
        planToUpdate.setStart_date(startDate);
        planToUpdate.setCompletion_date(endDate);

        // 업데이트된 Plans 저장
        plansRepository.save(planToUpdate);
    }

    // 데이터베이스에 임시 계획을 한꺼번에 저장하는 메서드
    @Transactional
    public void savePlanEquipments2() {
        int maxPlanId = getMaxPlanId(); // 최대 Plan ID를 가져옴

        for (Plan_equipment plan2 : temporaryPlans2) {
            Plans plans = new Plans();
            plans.setPlan_id(maxPlanId); // 설정할 Plan ID
            plan2.setPlan(plans); // Plan_equipment 객체의 Plans에 plan_id 설정
        }

        planEquipmentRepository.saveAll(temporaryPlans2);

        // 저장 후에 임시 리스트 비우기
        temporaryPlans2.clear();

    }

    // 생산계획 가장 높은 id 값 가져오기
    public int getMaxPlanId() {
        Integer maxPlanId = plansRepository.getMaxPlanId();
        return maxPlanId != null ? maxPlanId : 0; // null 체크 후 처리
    }

    // 임시 계획 리스트를 초기화하는 메서드 추가
    public void clearTemporaryPlans() {

        temporaryPlans.clear();
        Plans.clear();
    }

    // 창현 : 오늘에 따른 장비별 계획 조회
    public List<Plan_equipment> getPlansByEquipmentIdAndStartDate(int equipmentId, LocalDateTime startOfDay) {
        return planEquipmentRepository.findPlansByEquipmentIdAndStartDate(equipmentId, startOfDay);
    }

    // 창현 : start 버튼 누르면 시간 나오도록
    public void start(Equipment_Dto equipmentDto) {
        Optional<Plan_equipment> equipmentPlan = planEquipmentRepository.findById(equipmentDto.getEquipmentPlanId());

        LocalDateTime date = timeService.getDateTimeFromDB().getTime();

        if (equipmentPlan.isPresent()) {
            Plan_equipment planEquipment = equipmentPlan.get();

            // 수정할 필드만 설정
            planEquipment.setStart_date(date);

            // 저장
            planEquipmentRepository.save(planEquipment);
        } else {
            throw new RuntimeException("planEquipment not found with id");
        }

    }

    // 창현 : start 버튼 누르면 시간 나오도록
    public void stop(Equipment_Dto equipmentDto) {
        Optional<Plan_equipment> equipmentPlan = planEquipmentRepository.findById(equipmentDto.getEquipmentPlanId());

        LocalDateTime date = timeService.getDateTimeFromDB().getTime();

        if (equipmentPlan.isPresent()) {
            Plan_equipment planEquipment = equipmentPlan.get();

            // 수정할 필드만 설정
            planEquipment.setEnd_date(date);

            // 저장
            planEquipmentRepository.save(planEquipment);
        } else {
            throw new RuntimeException("planEquipment not found with id");
        }

    }

    // 설비별 작동 여부
    public List<Plan_equipment> findUnfinishedPlansByEquipmentId(int equipmentId) {
        return planEquipmentRepository.findUnfinishedPlansByEquipmentId(equipmentId);
    }
    public boolean hasUnfinishedPlansByEquipmentId(int equipmentId) {
        return planEquipmentRepository.hasUnfinishedPlansByEquipmentId(equipmentId);
    }

}
