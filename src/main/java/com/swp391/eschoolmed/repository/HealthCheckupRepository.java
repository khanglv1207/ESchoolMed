package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.HealthCheckup;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HealthCheckupRepository extends JpaRepository<HealthCheckup, UUID> {
    List<HealthCheckup> findAllByOrderByCheckupDateDesc();

    List<HealthCheckup> findByStudent_StudentIdIn(List<UUID> studentIds);
    boolean existsByNotification(MedicalCheckupNotification notification);


}

