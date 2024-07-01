package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "finish_product")
@NoArgsConstructor
@AllArgsConstructor
public class Finish_product {

    @Id
    @Column(name = "finish_product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int finish_product_id;

    private String product_name;

    private int received_quantity;

    private LocalDateTime received_date;

    private int shipped_quantity;

    private LocalDateTime shipped_date;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    @JoinColumn(name="order_id")
    private Orders order_id;

}
