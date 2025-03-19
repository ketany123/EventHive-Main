package com.EventHive.entity;

import com.EventHive.enums.EventType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;


@Data
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Events{

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    long id;

    String eventName;

    @Enumerated(value = EnumType.STRING)
    EventType eventype;


    int eventLength;
    LocalDate releaseDate;
    String createdBy;



}