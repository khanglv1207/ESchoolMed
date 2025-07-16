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

    List<MedicationSchedule> findByItem_ItemId(UUID itemId);

    List<MedicationSchedule> findAllByItem_Request_Student_StudentId(UUID studentId);
    @Query("""
        SELECT s FROM MedicationSchedule s
        WHERE s.item.request.student.studentId = :studentId
    """)
    List<MedicationSchedule> findAllByStudentId(@Param("studentId") UUID studentId);

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

    List<MedicationSchedule> findAllByItem_Request_Student_StudentIdIn(List<UUID> studentIds);


}
