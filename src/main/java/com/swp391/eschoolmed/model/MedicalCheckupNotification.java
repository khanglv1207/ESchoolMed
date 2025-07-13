package com.swp391.eschoolmed.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "medical_checkup_notifications")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class MedicalCheckupNotification {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "checkup_title", nullable = false)
    private String checkupTitle;

    @Column(name = "checkup_date", nullable = false)
    private LocalDate checkupDate;

    @Column(columnDefinition = "TEXT")
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", nullable = false)
    private Parent parent;

    @Column(name = "is_confirmed")
    private Boolean isConfirmed = false;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(columnDefinition = "TEXT")
    private String resultSummary; // mô tả tổng quát kết quả

    @Column
    private Boolean isAbnormal; // có bất thường không

    @Column
    private String suggestion; // tư vấn hoặc hướng xử lý

}
