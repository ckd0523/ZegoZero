package com.codehows.zegozero.repository;

import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Plans;
import com.codehows.zegozero.entity.Purchase_matarial;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface OrdersRepository  extends JpaRepository<Orders, Integer> {

    // JPQL 쿼리를 사용하여 shipping_date가 null인 Orders만 찾는 메서드
    @Query("SELECT o FROM Orders o WHERE o.shipping_date IS NULL")
    List<Orders> findAllByShippingDateIsNull();

    // JPQL 쿼리를 사용하여 shipping_date가 null이 아닌 Orders만 찾는 메서드
    @Query("SELECT o FROM Orders o WHERE o.shipping_date IS NOT NULL")
    List<Orders> findAllByShippingDateIsNotNull();

    List<Orders> findByDeletable(Boolean deletable);


    @Query("SELECT o FROM Orders o WHERE o.orderId = :orderId")
    Orders findByOrderId(@Param("orderId") Integer orderId);

    // 가장 큰 order_id를 가져오는 JPQL 쿼리
    @Query("SELECT MAX(o.orderId) FROM Orders o")
    Integer findMaxOrderId();

}
