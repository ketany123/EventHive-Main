package com.EventHive.dto;

import com.EventHive.entity.Seat;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ReservationResponseDto {

    private long userId;
    private String firstname;
    private List<Seat> reservedseats;


}