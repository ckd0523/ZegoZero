package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "purchase_matarial")
@NoArgsConstructor
public class Purchase_matarial {

    @Id
    @Column(name = "purchase_matarial_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int purchase_matarial_id;

    private String raw_material;

    private int order_quantity;

    private LocalDateTime purchase_date;

    private LocalDateTime delivery_completion_date;

    private String delivery_status;

    @ManyToOne(cascade = CascadeType.REMOVE)
    @JoinColumn(name="order_id")
    private Orders order_id;


    @Builder
    public Purchase_matarial( int purchase_matarial_id, String raw_material, int order_quantity,
                              LocalDateTime purchase_date,LocalDateTime delivery_completion_date,String delivery_status,
                              Orders order_id){
        this.purchase_matarial_id = purchase_matarial_id;
        this.raw_material = raw_material;
        this.order_quantity = order_quantity;
        this.purchase_date = purchase_date;
        this.delivery_completion_date = delivery_completion_date;
        this.delivery_status = delivery_status;
        this.order_id = order_id;
    }

}
