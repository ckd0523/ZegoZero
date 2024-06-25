package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Data
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

}
