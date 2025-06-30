package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MedicalCheckupNotificationRepository extends JpaRepository<MedicalCheckupNotification, UUID> {
    List<MedicalCheckupNotification> findByParent_ParentId(UUID parentId);
    List<MedicalCheckupNotification> findByStudent_StudentId(UUID studentId);
}
