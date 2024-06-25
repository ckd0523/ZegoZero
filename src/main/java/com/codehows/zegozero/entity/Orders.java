package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Orders {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    private String product_name;

    private int quantity;

    private int used_inventory;

    private int production_quantity;

    private LocalDateTime order_date;

    private LocalDateTime expected_shipping_date;

    private String customer_name;

    private String delivery_address;

    private LocalDateTime shipping_date;

    private Boolean deletable;

    private Boolean Delivery_available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan")
    private Plans plan;

    //원자재 발주 여부 추가.
    private Boolean ordered;

    @Builder
    public Orders(int orderId,String product_name,int quantity,
                  int used_inventory,int production_quantity,LocalDateTime order_date,LocalDateTime expected_shipping_date,
                    String customer_name,String delivery_address, Boolean deletable) {
        this. orderId= orderId;
        this.product_name = product_name;
        this.used_inventory = used_inventory;
        this.order_date = order_date;
        this.expected_shipping_date = expected_shipping_date;
        this.customer_name = customer_name;
        this.delivery_address = delivery_address;
        this.deletable = deletable;
    }
}
