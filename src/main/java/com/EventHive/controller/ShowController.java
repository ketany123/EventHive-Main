package com.EventHive.controller;


import com.EventHive.dto.ApiResponseDto;
import com.EventHive.dto.PagedApiResponseDto;
import com.EventHive.dto.ShowRequestDto;
import com.EventHive.entity.Events;
import com.EventHive.entity.Show;
import com.EventHive.service.EventService;
import com.EventHive.service.JwtService;
import com.EventHive.service.ShowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shows")
public class ShowController {

    private final ShowService showService;
    private final EventService eventService;
    private final JwtService jwtService;

    @Autowired
    public ShowController(ShowService showService, EventService eventService, JwtService jwtService) {
        this.showService = showService;
        this.eventService=eventService;
        this.jwtService = jwtService;
    }


    @GetMapping("/all")
    public ResponseEntity<PagedApiResponseDto> getAllShows(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<Show> showPage = showService.getllShows(page, size);
        return ResponseEntity.ok(
                PagedApiResponseDto.builder()
                        .currentCount(showPage.getNumberOfElements())
                        .currentPageData(showPage.getContent())
                        .totalElements(showPage.getTotalElements())
                        .totalPages(showPage.getTotalPages())
                        .build()
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<PagedApiResponseDto> filterShows(
            @RequestParam(required = false) Long theaterId,
            @RequestParam(required = false) Long movieId,
            @RequestParam(required = false) String showDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ){
        Page<Show> showPage = showService.filterShowsByTheaterIdAndMovieId(theaterId, movieId, PageRequest.of(page, size));
        return ResponseEntity.ok(
                PagedApiResponseDto.builder()
                        .currentCount(showPage.getNumberOfElements())
                        .currentPageData(showPage.getContent())
                        .totalElements(showPage.getTotalElements())
                        .totalPages(showPage.getTotalPages())
                        .build()
        );
    }

    @GetMapping("/show/{showId}")
    public ResponseEntity<ApiResponseDto> getShowById(@PathVariable long showId){
        Show show = showService.getShowById(showId);
        return ResponseEntity.ok(
                ApiResponseDto.builder()
                        .data(show)
                        .message("Fetched show with id: " + show.getId())
                        .build()
        );
    }

    @PostMapping("/show/create")
    public ResponseEntity<ApiResponseDto> createShow(@RequestBody ShowRequestDto showRequestDto,
                                                     @RequestHeader("Authorization") String token){

//        get movie by movieId;
        long movieid=showRequestDto.getMovieId();
        Events events= eventService.getMovieById(movieid);

        String jwt = token.substring(7); // Remove "Bearer " prefix
        String username = jwtService.extractUsername(jwt);

        if(!events.getCreatedBy().equals(username)){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            ApiResponseDto.builder()
                                    .message("You are not authorized to create a show for this event")
                                    .build()
                    );
        }

        Show show = showService.createNewShow(showRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponseDto.builder()
                                .message("Show created with id: " + show.getId())
                                .data(show)
                                .build()
                );
    }

    @PatchMapping("/show/update/movie/{showId}")
    public ResponseEntity<ApiResponseDto> updateMovie(){
        return null;
    }

    @PatchMapping("/show/update/theatre/{showId}")
    public ResponseEntity<ApiResponseDto> updateTheatre(){
        return null;
    }

    @PatchMapping("/show/update/timings/{showId}")
    public ResponseEntity<ApiResponseDto> updateShowTimings(){
        return null;
    }

    @DeleteMapping("/show/delete/{showId}")
    public ResponseEntity<?> deleteShowById(@PathVariable long showId){
        showService.deleteShowById(showId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


    @GetMapping("/all/eventname/{eventname}")
    public ResponseEntity<PagedApiResponseDto> getAllShowsbyEventName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String eventname
    ){
        Page<Show> showPage = showService.findbyeventName(eventname,PageRequest.of(page, size));
        return ResponseEntity.ok(
                PagedApiResponseDto.builder()
                        .currentCount(showPage.getNumberOfElements())
                        .currentPageData(showPage.getContent())
                        .totalElements(showPage.getTotalElements())
                        .totalPages(showPage.getTotalPages())
                        .build()
        );
    }


    @GetMapping("/all/venuename/{venuename}")
    public ResponseEntity<PagedApiResponseDto> getAllShowsbyVenueName(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @PathVariable String venuename
    ){
        Page<Show> showPage = showService.findbyvenueName(venuename,PageRequest.of(page, size));
        return ResponseEntity.ok(
                PagedApiResponseDto.builder()
                        .currentCount(showPage.getNumberOfElements())
                        .currentPageData(showPage.getContent())
                        .totalElements(showPage.getTotalElements())
                        .totalPages(showPage.getTotalPages())
                        .build()
        );
    }


}
