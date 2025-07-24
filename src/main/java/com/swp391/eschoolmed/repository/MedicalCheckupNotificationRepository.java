package com.swp391.eschoolmed.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.ParentStudent;
import com.swp391.eschoolmed.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.swp391.eschoolmed.model.MedicalCheckupNotification;

@Repository
public interface MedicalCheckupNotificationRepository extends JpaRepository<MedicalCheckupNotification, UUID> {
    List<MedicalCheckupNotification> findByParent(Parent parent);
    List<MedicalCheckupNotification> findByIsConfirmedTrue();
    List<MedicalCheckupNotification> findAll();
    List<MedicalCheckupNotification> findAllBySentAtIsNotNullAndIsConfirmedIsNull();



}
