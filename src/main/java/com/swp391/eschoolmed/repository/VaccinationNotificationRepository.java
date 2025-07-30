package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.model.VaccinationNotification;
import com.swp391.eschoolmed.model.VaccinationResult;
import com.swp391.eschoolmed.model.VaccineType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VaccinationNotificationRepository extends JpaRepository<VaccinationNotification, UUID> {

    boolean existsByStudentAndVaccineType(Student student, VaccineType vaccineType);

}
