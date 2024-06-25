package com.codehows.zegozero.dto;

import com.codehows.zegozero.entity.Orders;
import com.codehows.zegozero.entity.Purchase_matarial;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.Hibernate;

import java.time.LocalDateTime;
import java.util.Date;

@Getter
@ToString
public class responsePurchaseMaterial_Dto {

    private Integer purchase_matarial_id;

    private String raw_material;

    private Integer order_quantity;

    private LocalDateTime purchase_date=LocalDateTime.now();

    private LocalDateTime delivery_completion_date;

    private String delivery_status;

    private Orders order_id;

    public responsePurchaseMaterial_Dto(Purchase_matarial purchase_matarial){

        //Hibernate.initialize(purchase_matarial);

        this.purchase_matarial_id = purchase_matarial.getPurchase_matarial_id();
        this.raw_material=purchase_matarial.getRaw_material();
        this.order_quantity=purchase_matarial.getOrder_quantity();
        this.purchase_date=purchase_matarial.getPurchase_date();
       // this.delivery_completion_date=purchase_matarial.getDelivery_completion_date();
        this.delivery_status=purchase_matarial.getDelivery_status();
        this.order_id=purchase_matarial.getOrder_id();
    }



}
