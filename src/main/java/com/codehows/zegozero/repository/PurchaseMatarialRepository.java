package com.codehows.zegozero.repository;

import com.codehows.zegozero.entity.Purchase_matarial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface PurchaseMatarialRepository  extends JpaRepository<Purchase_matarial, Integer> {
//    List<Purchase_matarial> findByDelivery_status(String deliveryStatus);

    // JPQL을 사용하여 deliveryStatus 필드를 기준으로 PurchaseMaterial을 조회하는 메서드
    @Query("SELECT p FROM Purchase_matarial p WHERE p.delivery_status = :deliveryStatus")
    List<Purchase_matarial> findByDeliveryStatusJPQL(String deliveryStatus);

    @Query("SELECT p FROM Purchase_matarial p WHERE p.purchase_matarial_id = :id")
    Purchase_matarial findByPurchaseMaterialId(Integer id);
}
