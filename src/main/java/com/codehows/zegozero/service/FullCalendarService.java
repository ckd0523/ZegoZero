package com.codehows.zegozero.service;

import com.codehows.zegozero.entity.Plan_equipment;
import com.codehows.zegozero.repository.PlanEquipmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class FullCalendarService {

    private final PlanEquipmentRepository planEquipmentRepository;

    public List<Plan_equipment> getAllPlanEquipments(){
        return planEquipmentRepository.findAllWithDetails();
    }


}
