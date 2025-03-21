package com.EventHive.repository;

import com.EventHive.entity.Events;
import com.EventHive.enums.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Events, Long> {
    List<Events> findByEventype(EventType eventype);
}
