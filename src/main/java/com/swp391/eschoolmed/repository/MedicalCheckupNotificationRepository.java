package com.swp391.eschoolmed.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.swp391.eschoolmed.model.MedicalCheckupNotification;

@Repository
public interface MedicalCheckupNotificationRepository extends JpaRepository<MedicalCheckupNotification, UUID> {
    List<MedicalCheckupNotification> findByParent_ParentId(UUID parentId);
    List<MedicalCheckupNotification> findByStudent_StudentId(UUID studentId);
}
