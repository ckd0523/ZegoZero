package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "material_details")
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Material_details {

    @Id
    @Column(name = "matarial_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int matarial_id;

    private int received_quantity;

    private LocalDateTime received_date;

    private int shipped_quantity;

    private LocalDateTime shipped_date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="purchase_matarial")
    private Purchase_matarial purchase_matarial;

}
