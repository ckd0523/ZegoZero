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
    @Query("SELECT pe FROM Plan_equipment pe WHERE pe.equipment.equipment_id = :equipmentId AND pe.estimated_start_date >= :startOfDay")
    List<Plan_equipment> findPlansByEquipmentIdAndStartDate(@Param("equipmentId") int equipmentId,
                                                            @Param("startOfDay") LocalDateTime startOfDay);

    //가동여부
    @Query("SELECT pe FROM Plan_equipment pe " +
            "WHERE pe.equipment.equipment_id = :equipmentId " +
            "AND pe.start_date IS NOT NULL " +
            "AND pe.end_date IS NULL")
    List<Plan_equipment> findUnfinishedPlansByEquipmentId(@Param("equipmentId") int equipmentId);

    @Query("SELECT CASE WHEN COUNT(pe) > 0 THEN true ELSE false END " +
            "FROM Plan_equipment pe " +
            "WHERE pe.equipment.equipment_id = :equipmentId " +
            "AND pe.start_date IS NOT NULL " +
            "AND pe.end_date IS NULL")
    boolean hasUnfinishedPlansByEquipmentId(@Param("equipmentId") int equipmentId);


    // 설비1에서 시작 시간 가져오기
    @Query("SELECT pe.estimated_start_date FROM Plan_equipment pe JOIN pe.plan p JOIN pe.equipment e WHERE p.plan_id = :planId AND e.equipment_id = 1")
    LocalDateTime findStartDateByPlanIdAndEquipmentId(@Param("planId") int planId);

    // 설비12에서 종료 시간 가져오기
    @Query("SELECT pe.estimated_end_date FROM Plan_equipment pe JOIN pe.plan p JOIN pe.equipment e WHERE p.plan_id = :planId AND e.equipment_id = 12")
    LocalDateTime findEndDateByPlanIdAndEquipmentId(@Param("planId") int planId);

    @Query("SELECT pe FROM Plan_equipment pe JOIN FETCH pe.plan p JOIN FETCH pe.equipment e")
    List<Plan_equipment> findAllWithDetails();

    // 시작시간이 있고 종료시간이 없는 데이터 가져오기
    @Query("SELECT pe FROM Plan_equipment pe WHERE pe.start_date IS NOT NULL AND pe.end_date IS NULL")
    List<Plan_equipment> findRunningEquipments();

}
