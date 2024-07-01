package com.codehows.zegozero.dto;

import com.codehows.zegozero.entity.Material_details;
import com.codehows.zegozero.entity.Purchase_matarial;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Material_inventory_management_Dto {
    private Integer purchase_order_id;
    private Integer material_id;
    private String raw_material;
    private Integer received_quantity;
    private LocalDateTime received_date;
    private Integer shipped_quantity;
    private LocalDateTime shipped_date;

    public Material_inventory_management_Dto(Material_details material_details) {
        this.raw_material = material_details.getPurchase_matarial().getRaw_material();
        this.received_quantity = material_details.getReceived_quantity();
        this.received_date = material_details.getReceived_date();
        this.shipped_quantity = material_details.getShipped_quantity();
        this.shipped_date = material_details.getShipped_date();
        if(material_details.getPurchase_matarial().getOrder_id() != null) {
            this.purchase_order_id = material_details.getPurchase_matarial().getOrder_id().getOrderId();
        }else{
            this.purchase_order_id = null;
        }
        this.material_id = material_details.getMatarial_id();

    }
}
