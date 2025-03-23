package com.EventHive.dto;

import com.EventHive.enums.EventType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class MovieRequestDto {

    String eventName;
    EventType eventype;
    int eventLength;
    String eventLanguage;
    LocalDate releaseDate;

}
