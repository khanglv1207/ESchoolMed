package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.VaccinationRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VaccinationRecordRepository extends JpaRepository<VaccinationRecord, Long> {
    List<VaccinationRecord> findByStudent_StudentId(UUID studentId);
}
