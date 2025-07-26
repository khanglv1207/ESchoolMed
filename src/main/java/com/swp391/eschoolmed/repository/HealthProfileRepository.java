package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.HealthProfile;
import com.swp391.eschoolmed.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HealthProfileRepository extends JpaRepository<HealthProfile, UUID> {
    Optional<HealthProfile> findByStudent_StudentId(UUID studentId);

    Optional<HealthProfile> findFirstByStudent_StudentIdOrderByUpdatedAtDesc(UUID studentId);

}
