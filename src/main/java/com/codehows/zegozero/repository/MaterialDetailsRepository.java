package com.codehows.zegozero.repository;

import com.codehows.zegozero.entity.Material_details;
import com.codehows.zegozero.entity.Purchase_matarial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaterialDetailsRepository  extends JpaRepository<Material_details, Integer> {

    @Query("SELECT p FROM Material_details p WHERE p.purchase_matarial.purchase_matarial_id = :id")
    Material_details findByPurchaseId(Integer id);

    @Query("SELECT p FROM Purchase_matarial p WHERE p.order_id.orderId = :orderId")
    List<Purchase_matarial> findByOrderId(@Param("orderId") Integer orderId);

}
