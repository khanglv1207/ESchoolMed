package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.dto.response.GetAllNurseResponse;
import com.swp391.eschoolmed.model.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NurseRepository extends JpaRepository<Nurse, UUID> {
    List<GetAllNurseResponse> findAll(UUID id);
    boolean existsByEmail(String email);
}
