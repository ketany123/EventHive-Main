package com.EventHive.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.EventHive.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Reservation {

    @Id
    private Long id;

    @ManyToOne
    @JsonIgnoreProperties({"password"})
    private User user;

    @ManyToOne
    @JsonIgnoreProperties({"seats"})
    private Show show;

    @OneToMany(fetch = FetchType.EAGER)
    private List<Seat> seatsReserved;

    private boolean isVisited;

    private double amountPaid;

    @Enumerated(value = EnumType.STRING)
    private ReservationStatus reservationStatus;

    private LocalDateTime createdAt;

    @Lob
    private byte[] qrImage;

}
