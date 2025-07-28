package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.ReminderSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ReminderScheduleRepository extends JpaRepository<ReminderSchedule, UUID> {
}
