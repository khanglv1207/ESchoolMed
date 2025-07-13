package com.swp391.eschoolmed.repository;

import com.swp391.eschoolmed.model.MedicationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MedicationScheduleRepository extends JpaRepository<MedicationSchedule, UUID> {

    // Lấy lịch uống thuốc theo item_id
    List<MedicationSchedule> findByItem_ItemId(UUID itemId);

    // Lấy tất cả lịch uống thuốc theo studentId (truy ngược qua MedicationItem → MedicationRequest → Student)
    @Query("""
        SELECT s FROM MedicationSchedule s
        WHERE s.item.request.student.studentId = :studentId
    """)
    List<MedicationSchedule> findAllByStudentId(@Param("studentId") UUID studentId);

    // Lấy các lịch chưa uống cho 1 học sinh vào 1 ngày
    @Query("""
        SELECT s FROM MedicationSchedule s
        WHERE s.item.request.student.studentId = :studentId
          AND s.item.request.requestDate >= :from
          AND s.hasTaken = false
    """)
    List<MedicationSchedule> findUnTakenSchedules(
            @Param("studentId") UUID studentId,
            @Param("from") LocalDateTime from
    );


}
