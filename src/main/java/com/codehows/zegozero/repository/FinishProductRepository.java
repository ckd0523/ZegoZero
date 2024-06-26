package com.codehows.zegozero.repository;

import com.codehows.zegozero.entity.Finish_product;
import com.codehows.zegozero.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FinishProductRepository extends JpaRepository<Finish_product, Integer> {


    // received_date 가 not null (입고)
    @Query("SELECT fp FROM Finish_product fp WHERE fp.received_date IS NOT NULL")
    List<Finish_product> findAllWithReceivedDateNotNull();

    // shipped_date 가 not null (출고)
    @Query("SELECT fp FROM Finish_product fp WHERE fp.shipped_date IS NOT NULL")
    List<Finish_product> findAllWithShippedDateNotNull();

    // order_id가 null이고 주어진 product_name과 일치하는 Finish_product의 received_quantity 합을 조회합니다.
    @Query("SELECT COALESCE(SUM(fp.received_quantity), 0) FROM Finish_product fp " +
            "WHERE fp.order_id IS NULL AND fp.product_name = :productName")
    Integer sumReceivedQuantityByProductNameAndNullOrderId(String productName);

    // order_id가 null이고 product_name이 일치하는 Finish_product 엔티티의 shipped_quantity 총합을 조회하는 JPQL 쿼리
    @Query("SELECT SUM(fp.shipped_quantity) FROM Finish_product fp WHERE fp.order_id IS NULL AND fp.product_name = :productName")
    Integer sumShippedQuantityByProductNameAndNullOrderId(String productName);

    @Query("SELECT SUM(fp.received_quantity) FROM Finish_product fp WHERE fp.order_id.orderId = :orderId")
    Integer sumReceivedQuantityByOrderId(int orderId);

}
