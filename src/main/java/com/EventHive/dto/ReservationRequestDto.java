package com.EventHive.dto;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReservationRequestDto {

    @Id
    private Long id;
    private long showId;
    private List<Long> seatIdsToReserve;


}