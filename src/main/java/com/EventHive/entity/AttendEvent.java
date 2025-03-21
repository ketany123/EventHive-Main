package com.EventHive.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class AttendEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Reservation reservation;


}