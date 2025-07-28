package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.ConfirmationStatus;
import com.swp391.eschoolmed.model.VaccinationConfirmation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VaccinationConfirmationRepository extends JpaRepository<VaccinationConfirmation, UUID> {
    List<VaccinationConfirmation> findByStatusAndVaccinationResultIsNull(ConfirmationStatus status);

}
