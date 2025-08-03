package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.model.VaccinationNotification;
import com.swp391.eschoolmed.model.VaccinationResult;
import com.swp391.eschoolmed.model.VaccineType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface VaccinationNotificationRepository extends JpaRepository<VaccinationNotification, UUID> {
    @Query("SELECT vn FROM VaccinationNotification vn " +
            "JOIN ParentStudent ps ON ps.student = vn.student " +
            "JOIN ps.parent p " +
            "JOIN p.user u " +
            "WHERE u.id = :userId")
    List<VaccinationNotification> findNotificationsByUserId(@Param("userId") UUID userId);
    boolean existsByStudentAndVaccineType(Student student, VaccineType vaccineType);

}
