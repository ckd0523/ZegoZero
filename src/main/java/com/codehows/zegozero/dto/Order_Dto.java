package com.codehows.zegozero.dto;

import com.codehows.zegozero.entity.Orders;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order_Dto {

    private int order_id;

    @NotNull(message = "제품명을 입력해주세요.")
    @Size(min = 1, message = "제품명을 입력해주세요.")
    private String product_name;

    @NotNull(message = "주문 수량을 입력해주세요.")
    @Min(value = 1, message = "최소 주문은 1박스 이상 입니다.")
    @Max(value = 1000, message = "최대 주문은 1000박스 입니다.")
    private int quantity;

    private int used_inventory;

    private int production_quantity;

    private LocalDateTime order_date;

    private LocalDateTime expected_shipping_date;

    @NotNull(message = "고객사를 입력해주세요.")
    @Size(min = 1, message = "고객사를 입력해주세요.")
    private String customer_name;

    @NotNull(message = "배송지를 입력해주세요.")
    @Size(min = 1, message = "배송지를 입력해주세요.")
    private String delivery_address;

    private LocalDateTime shipping_date;

    private Boolean deletable;

    private Boolean Delivery_available;

    public Order_Dto(Orders orders) {

        this.order_id = orders.getOrderId();
        this.product_name = orders.getProduct_name();
        this.quantity = orders.getQuantity();
        this.used_inventory = orders.getUsed_inventory();
        this.production_quantity = orders.getProduction_quantity();
        this.order_date = orders.getOrder_date();
        this.expected_shipping_date = orders.getExpected_shipping_date();
        this.customer_name = orders.getCustomer_name();
        this.delivery_address = orders.getDelivery_address();
        this.deletable = orders.getDeletable();
        this.shipping_date = orders.getShipping_date();
        this.Delivery_available = orders.getDelivery_available();
    }
}
