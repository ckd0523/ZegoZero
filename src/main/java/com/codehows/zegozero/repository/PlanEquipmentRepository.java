package com.codehows.zegozero.repository;

import com.codehows.zegozero.entity.Plan_equipment;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PlanEquipmentRepository extends JpaRepository<Plan_equipment, Integer> {

    // 설비3,4 계획
    @Query("SELECT pe " +
            "FROM Plan_equipment pe " +
            "WHERE pe.equipment.equipment_id = :equipmentId " +
            "AND pe.estimated_end_date = (" +
            "SELECT MAX(p.estimated_end_date) " +
            "FROM Plan_equipment p " +
            "WHERE p.equipment.equipment_id = :equipmentId" +
            ")")
    Optional<Plan_equipment> findLatestPlanEquipmentByEquipmentId(@Param("equipmentId") int equipmentId);

    // id에 해당하는 설비의 계획을 모두 조회하는 쿼리
    @Query("SELECT pe FROM Plan_equipment pe WHERE pe.equipment.equipment_id = :equipmentId")
    List<Plan_equipment> findAllByEquipmentEquipmentId(@Param("equipmentId") int equipmentId);

    // 오늘 id에 해당하는 설비의 계획을 모두 조회하는 쿼리
    @Query("SELECT pe FROM Plan_equipment pe WHERE pe.equipment.equipment_id = :equipmentId AND " +
            "pe.estimated_start_date >= :startOfDay AND pe.estimated_end_date < :endOfDay")
    List<Plan_equipment> findPlansByEquipmentIdAndDate(@Param("equipmentId") int equipmentId,
                                                       @Param("startOfDay") LocalDateTime startOfDay,
                                                       @Param("endOfDay") LocalDateTime endOfDay);

}
