package com.codehows.zegozero.repository;

import com.codehows.zegozero.entity.Material_details;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialDetailsRepository  extends JpaRepository<Material_details, Integer> {

    @Query("SELECT p FROM Material_details p WHERE p.purchase_matarial.purchase_matarial_id = :id")
    Material_details findByPurchaseId(Integer id);

}
