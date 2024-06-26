package com.codehows.zegozero.dto;

import com.codehows.zegozero.entity.Finish_product;
import com.codehows.zegozero.entity.Orders;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Finished_product_management_Dto {
    private Integer order_id;
    private String product_name;
    private Integer received_quantity;
    private LocalDateTime received_date;
    private Integer shipped_quantity;
    private LocalDateTime shipped_date;

    public Finished_product_management_Dto(Finish_product finish_product) {

        if (finish_product.getOrder_id() != null) {
            this.order_id = finish_product.getOrder_id().getOrderId();
        }
        this.product_name = finish_product.getProduct_name();
        this.received_quantity = finish_product.getReceived_quantity();
        this.received_date = finish_product.getReceived_date();
        this.shipped_quantity = finish_product.getShipped_quantity();
        this.shipped_date = finish_product.getShipped_date();
    }
}


