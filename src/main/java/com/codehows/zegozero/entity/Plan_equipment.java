package com.codehows.zegozero.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Data
@Table(name = "plan_equipment")
@Setter
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="plan")
    private Plans plan;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="equipment")
    private Equipment equipment;

}
