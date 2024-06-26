package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
public class Orders {

    @Id
    @Column(name = "order_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int order_id;

    private String product_name;

    private int quantity;

    private int used_inventory;

    private int production_quantity;

    private Date order_date;

    private LocalDateTime expected_shipping_date;

    private String customer_name;

    private String delivery_address;

    private Date shipping_date;

    private Boolean deletable;

    private Boolean Delivery_available;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan")
    private Plans plan;

}
