package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.VaccinationResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VaccinationResultRepository extends JpaRepository<VaccinationResult, UUID> {
    List<VaccinationResult> findAllByFinalizedFalse();
    List<VaccinationResult> findAllByConfirmation_Student_Parent_UserId(UUID parentId);

}
