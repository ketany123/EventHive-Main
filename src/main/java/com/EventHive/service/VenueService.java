package com.EventHive.service;


import com.EventHive.dto.VenueRequestDto;
import com.EventHive.entity.Venue;
import com.EventHive.exception.TheaterNotFoundException;
import com.EventHive.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import static com.EventHive.constant.ExceptionMessages.THEATER_NOT_FOUND;

@Service
public class VenueService {

    private final VenueRepository theaterRepository;

    @Autowired
    public VenueService(VenueRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    public Venue createNewTheater(VenueRequestDto theaterRequestDto) {
        Venue theater = Venue.builder()
                .name(theaterRequestDto.getName())
                .location(theaterRequestDto.getLocation())
                .build();
        return theaterRepository.save(theater);
    }

    public Page<Venue> getAllTheaters(int page, int size) {
        return theaterRepository.findAll(PageRequest.of(page, size));
    }

    public Page<Venue> getAllTheatersByLocation(int page, int size, String location) {
        return theaterRepository.findAllByLocation(location, PageRequest.of(page, size));
    }

    public Venue getTheaterById(long theaterId) {
        return theaterRepository.findById(theaterId)
                .orElseThrow(() -> new TheaterNotFoundException(THEATER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }

    public void deleteTheaterById(long theaterId) {
        theaterRepository.deleteById(theaterId);
    }

    public Venue updateTheaterById(long theaterId, VenueRequestDto theaterRequestDto) {
        return theaterRepository.findById(theaterId)
                .map(theater -> {
                    theater.setName(theaterRequestDto.getName());
                    theater.setLocation(theater.getLocation());
                    return theaterRepository.save(theater);
                })
                .orElseThrow(() -> new TheaterNotFoundException(THEATER_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}

