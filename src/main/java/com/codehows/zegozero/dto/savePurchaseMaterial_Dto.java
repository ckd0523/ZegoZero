package com.codehows.zegozero.dto;

import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Purchase_matarial;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.query.Order;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@ToString
public class savePurchaseMaterial_Dto {

    private Integer purchase_matarial_id;

    private String raw_material;

    private Integer order_quantity;

    private LocalDateTime purchase_date;

    private LocalDateTime delivery_completion_date;

    private String delivery_status = "배송중";

    private Integer order_id;


    public Purchase_matarial toEntity(Orders orders) {
        return Purchase_matarial.builder()
                .raw_material(raw_material)
                .order_quantity(order_quantity)
                .purchase_date(purchase_date)
                .delivery_status(delivery_status)
                .delivery_completion_date(delivery_completion_date)
                .order_id(orders)
                .build();
    }
}
