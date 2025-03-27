package com.EventHive.controller;


import com.EventHive.dto.ApiResponseDto;
import com.EventHive.dto.MovieRequestDto;
import com.EventHive.dto.PagedApiResponseDto;
import com.EventHive.entity.Events;
import com.EventHive.entity.User;
import com.EventHive.enums.EventType;
import com.EventHive.repository.UserRepository;
import com.EventHive.service.EventService;
import com.EventHive.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_SUPER_ADMIN')")
@RestController
@RequestMapping("/api/v1/movies")
public class EventController {

    private final EventService movieService;
    private final JwtService jwtService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    public EventController(EventService movieService,JwtService jwtService) {
        this.movieService = movieService;
        this.jwtService=jwtService;
    }

    @GetMapping("/all")
    public ResponseEntity<PagedApiResponseDto> getAllMovies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int pageSize
    ){
        Page<Events> moviePage = movieService.getAllMovies(page, pageSize);
        List<Events> movies = moviePage.getContent();
        return ResponseEntity.ok(
                PagedApiResponseDto.builder()
                        .totalPages(moviePage.getTotalPages())
                        .totalElements(moviePage.getTotalElements())
                        .currentPageData(movies)
                        .build()
        );
    }

    @GetMapping("/movie/{movieId}")
    public ResponseEntity<ApiResponseDto> getMovieById(@PathVariable long movieId){
        Events movie = movieService.getMovieById(movieId);
        return ResponseEntity.ok(
                ApiResponseDto.builder()
                        .message("Fetched movie with id: " + movieId)
                        .data(movie)
                        .build()
        );
    }

    @PostMapping("/create")
    public ResponseEntity<ApiResponseDto> createNewMovie(@RequestBody MovieRequestDto movieRequestDto,
                                                         @RequestHeader("Authorization") String token){
        String jwt = token.substring(7); // Remove "Bearer " prefix
        String username = jwtService.extractUsername(jwt);

        Events movie = movieService.createNewMovie(movieRequestDto,username);
        movie.setCreatedBy(username);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponseDto.builder()
                                .message("Movie created")
                                .data(movie)
                                .build()
                );
    }

    @PutMapping("/movie/update/{movieId}")
    public ResponseEntity<ApiResponseDto> updateMovieById(@PathVariable long movieId, @RequestBody MovieRequestDto movieRequestDto){
        Events movie = movieService.updateMovieById(movieId, movieRequestDto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponseDto.builder()
                                .message("Movie updated")
                                .data(movie)
                                .build()
                );
    }

    @DeleteMapping("/movie/delete/{movieId}")
    public ResponseEntity<?> deleteMovieById(@PathVariable long movieId){
        movieService.deleteMovieById(movieId);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

    @GetMapping("/events/type/{eventType}")
    public List<Events> getEventsByType(@PathVariable EventType eventType){
        return movieService.getEventsByType(eventType);
    }

    // User should be able to filter events by genres and languages


}
