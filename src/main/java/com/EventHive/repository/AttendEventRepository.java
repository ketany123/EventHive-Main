package com.EventHive.repository;

import com.EventHive.entity.AttendEvent;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AttendEventRepository extends JpaRepository<AttendEvent,Long> {
}
