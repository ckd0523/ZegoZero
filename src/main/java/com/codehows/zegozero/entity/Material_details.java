package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@Data
@Table(name = "material_details")
@NoArgsConstructor
@AllArgsConstructor
public class Material_details {

    @Id
    @Column(name = "matarial_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int matarial_id;

    private int received_quantity;

    private Date received_date;

    private int shipped_quantity;

    private Date shipped_date;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="purchase_matarial")
    private Purchase_matarial purchase_matarial;

}
