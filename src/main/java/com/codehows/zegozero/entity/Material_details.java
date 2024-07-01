package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "material_details")
@NoArgsConstructor
@AllArgsConstructor
public class Material_details {

    @Id
    @Column(name = "matarial_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int matarial_id;

    private int received_quantity;

    private LocalDateTime received_date;

    private int shipped_quantity;

    private LocalDateTime shipped_date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="purchase_matarial")
    private Purchase_matarial purchase_matarial;


}
