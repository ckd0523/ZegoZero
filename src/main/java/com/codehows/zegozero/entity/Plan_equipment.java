package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Getter
@Setter
@Table(name = "plan_equipment")
@NoArgsConstructor
@AllArgsConstructor
public class Plan_equipment {

    @Id
    @Column(name = "equipment_plan_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int equipment_plan_id;

    private LocalDateTime estimated_start_date;

    private LocalDateTime estimated_end_date;

    private LocalDateTime start_date;

    private LocalDateTime end_date;

    private int input;

    private int output;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="plan")
    private Plans plan;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name="equipment")
    private Equipment equipment;

}
