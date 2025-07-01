package com.swp391.eschoolmed.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import com.swp391.eschoolmed.model.Parent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swp391.eschoolmed.model.MedicalCheckupNotification;

@Repository
public interface MedicalCheckupNotificationRepository extends JpaRepository<MedicalCheckupNotification, UUID> {
    List<MedicalCheckupNotification> findByParent_ParentId(UUID parentId);
    List<MedicalCheckupNotification> findByStudent_StudentId(UUID studentId);

    @Query("SELECT n FROM MedicalCheckupNotification n WHERE n.checkupDate = :date AND n.isConfirmed = true")
    List<MedicalCheckupNotification> findConfirmedByDate(@Param("date") LocalDate date);

    List<MedicalCheckupNotification> findByCheckupTitleAndIsConfirmedTrue(String checkupTitle);

    List<MedicalCheckupNotification> findByParent(Parent parent);
}
