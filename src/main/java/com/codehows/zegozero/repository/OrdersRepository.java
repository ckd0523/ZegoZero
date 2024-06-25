package com.codehows.zegozero.repository;

import com.codehows.zegozero.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrdersRepository  extends JpaRepository<Orders, Integer> {

    // JPQL 쿼리를 사용하여 shipping_date가 null인 Orders만 찾는 메서드
    @Query("SELECT o FROM Orders o WHERE o.shipping_date IS NULL")
    List<Orders> findAllByShippingDateIsNull();

    // JPQL 쿼리를 사용하여 shipping_date가 null이 아닌 Orders만 찾는 메서드
    @Query("SELECT o FROM Orders o WHERE o.shipping_date IS NOT NULL")
    List<Orders> findAllByShippingDateIsNotNull();


    List<Orders> findByDeletable(Boolean deletable);
//    List<Orders> findAllByOrderIdIn(List<Integer> orderIds);

    Orders findByOrderId(Integer orderId);

}
