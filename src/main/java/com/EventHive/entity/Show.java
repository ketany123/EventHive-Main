package com.EventHive.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Show {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long id;

    @ManyToOne(targetEntity = Events.class)
    @JoinColumn(referencedColumnName = "id", nullable = false)
    Events events;

    @ManyToOne()
    Venue venue;
    LocalDateTime startTime;
    LocalDateTime endTime;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE)
    List<Seat> seats;

}
