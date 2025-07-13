package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.MedicationRequest;
import com.swp391.eschoolmed.model.ParentStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationRequestRepository extends JpaRepository<MedicationRequest, UUID> {

    List<MedicationRequest> findByStudent_StudentId(UUID studentId);
    List<MedicationRequest> findAllByParent_ParentId(UUID parentId);
    List<MedicationRequest> findByStatus(String status);
}
