package com.codehows.zegozero.repository;

import com.codehows.zegozero.entity.Plans;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface PlansRepository extends JpaRepository<Plans, Integer> {

    @Query("SELECT p FROM Plans p " +
            "WHERE p.product_name = :productName " +
            "AND p.status = 'planned' " +
            "ORDER BY p.plan_id DESC")
    Page<Plans> findLatestPlanByProductName(@Param("productName") String productName, Pageable pageable);

    @Query("SELECT MAX(p.plan_id) FROM Plans p")
    Integer getMaxPlanId();

    @Query("SELECT p FROM Plans p WHERE p.plan_id = :planId")
    Plans findByPlanId(@Param("planId") int planId);



}
