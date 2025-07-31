package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.MedicalIncident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MedicalIncidentRepository extends JpaRepository<MedicalIncident, Long> {
    List<MedicalIncident> findAllByParentNotifiedFalse();
}

