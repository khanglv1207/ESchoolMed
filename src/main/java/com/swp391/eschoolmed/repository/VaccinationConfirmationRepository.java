package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.ConfirmationStatus;
import com.swp391.eschoolmed.model.VaccinationConfirmation;
import com.swp391.eschoolmed.model.VaccineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VaccinationConfirmationRepository extends JpaRepository<VaccinationConfirmation, UUID> {
    List<VaccinationConfirmation> findByStatusAndVaccinationResultIsNull(ConfirmationStatus status);

    List<VaccinationConfirmation> findByStatusAndVaccinationResultIsNullAndNotification_VaccineType(
            ConfirmationStatus status,
            VaccineType vaccineType
    );

}
