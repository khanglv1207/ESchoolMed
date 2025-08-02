package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.dto.response.GetAllNurseResponse;
import com.swp391.eschoolmed.model.Nurse;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NurseRepository extends JpaRepository<Nurse, UUID> {
    Optional<Nurse> findByEmail(String email);
    boolean existsByEmail(String email);

}
