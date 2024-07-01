package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "plans")
@NoArgsConstructor
@AllArgsConstructor
public class Plans {

    @Id
    @Column(name = "plan_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int plan_id;

    private String product_name;

    private int planned_quantity;

    private LocalDateTime start_date;

    private LocalDateTime completion_date;

    private String status = "planned";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

//    @OneToMany(mappedBy = "plan", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
//    private List<Plan_equipment> planEquipments;

}
