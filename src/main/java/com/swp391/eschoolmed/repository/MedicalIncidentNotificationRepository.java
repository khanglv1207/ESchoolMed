package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.MedicalIncidentNotification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalIncidentNotificationRepository extends JpaRepository<MedicalIncidentNotification, Long> {

    List<MedicalIncidentNotification> findAllBySentAtIsNull();
}

