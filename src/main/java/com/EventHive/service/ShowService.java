package com.EventHive.service;


import com.EventHive.dto.ShowRequestDto;
import com.EventHive.entity.Seat;
import com.EventHive.entity.Show;
import com.EventHive.exception.MovieNotFoundException;
import com.EventHive.exception.ShowNotFoundException;
import com.EventHive.exception.TheaterNotFoundException;
import com.EventHive.repository.EventRepository;
import com.EventHive.repository.ShowRepository;
import com.EventHive.repository.VenueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static com.EventHive.constant.ExceptionMessages.*;

@Service
public class ShowService {

    private final ShowRepository showRepository;
    private final EventRepository movieRepository;
    private final VenueRepository theaterRepository;
    private final SeatService seatService;

    @Autowired
    public ShowService(ShowRepository showRepository, EventRepository movieRepository, VenueRepository theaterRepository, SeatService seatService) {
        this.showRepository = showRepository;
        this.movieRepository = movieRepository;
        this.theaterRepository = theaterRepository;
        this.seatService = seatService;
    }

    public Show createNewShow(ShowRequestDto showRequestDto) {

        if(showRepository.existsOverlappingShow(
                showRequestDto.getTheaterId(),
                LocalDateTime.parse(showRequestDto.getStartTime()),
                LocalDateTime.parse(showRequestDto.getEndTime()))){
            throw new RuntimeException("Time slot is already booked for this venue");
        }

        return movieRepository.findById(showRequestDto.getMovieId())
                .map(movie -> theaterRepository.findById(showRequestDto.getTheaterId())
                        .map(theater -> {
                            List<Seat> seats = new ArrayList<>();
                            showRequestDto.getSeats()
                                    .forEach(seatStructure ->
                                            seats.addAll(
                                                    seatService.createSeatsWithGivenPrice(
                                                            seatStructure.getSeatCount(),
                                                            seatStructure.getSeatPrice(),
                                                            seatStructure.getArea()
                                                    )
                                            )
                                    );

                            Show show = Show.builder()
                                    .events(movie)
                                    .venue(theater)
                                    .startTime(LocalDateTime.parse(showRequestDto.getStartTime()))
                                    .endTime(LocalDateTime.parse(showRequestDto.getEndTime()))
                                    .seats(seats)
                                    .build();
                            return showRepository.save(show);
                        })
                        .orElseThrow(() -> new TheaterNotFoundException(THEATER_NOT_FOUND, HttpStatus.BAD_REQUEST)))
                .orElseThrow(() -> new MovieNotFoundException(MOVIE_NOT_FOUND, HttpStatus.BAD_REQUEST));
    }

    public Page<Show> getllShows(int page, int size) {
        return showRepository.findAll(PageRequest.of(page, size));
    }

    public void deleteShowById(long showId) {
        showRepository.deleteById(showId);
    }
    public  Page<Show> findbyeventName(String eventname,PageRequest pageRequest){
        return showRepository.findByEventsEventName(eventname,pageRequest);
    }

    public  Page<Show> findbyvenueName(String venuename,PageRequest pageRequest){
        return showRepository.findByVenueName(venuename,pageRequest);
    }

    public Page<Show> filterShowsByTheaterIdAndMovieId(Long theaterId, Long movieId, PageRequest pageRequest) {
        if(theaterId == null && movieId == null){
            return showRepository.findAll(pageRequest);
        } else if(theaterId == null){
            return showRepository.findByEventsId(movieId, pageRequest);
        }
        return showRepository.findByVenueIdAndEventsId(theaterId, movieId, pageRequest);
    }

    public Show getShowById(long showId) {
        return showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException(SHOW_NOT_FOUND, HttpStatus.NOT_FOUND));
    }
}
