package com.EventHive.repository;


import com.EventHive.entity.Show;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ShowRepository extends JpaRepository<Show, Long> {
    Page<Show> findByEventsId(long eventId, Pageable pageable);
    Page<Show> findByEventsEventName(String eventName, Pageable pageable);
    Page<Show> findByVenueName(String name, Pageable pageable);
    Page<Show> findByVenueIdAndEventsId(long venueId,long eventId, Pageable pageable);

    @Query("""
        SELECT COUNT(s) > 0 FROM Show s 
        WHERE s.venue.id = :venueId 
        AND (
            (:startTime BETWEEN s.startTime AND s.endTime) OR
            (:endTime BETWEEN s.startTime AND s.endTime) OR
            (s.startTime BETWEEN :startTime AND :endTime)
        )
    """)
    boolean existsOverlappingShow(@Param("venueId") long venueId,
                                  @Param("startTime") LocalDateTime startTime,
                                  @Param("endTime") LocalDateTime endTime);
}
