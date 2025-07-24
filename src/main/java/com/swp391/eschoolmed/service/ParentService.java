package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.ConfirmCheckupRequest;
import com.swp391.eschoolmed.dto.request.MedicalRequest;
import com.swp391.eschoolmed.dto.request.UpdateParentProfileRequest;
import com.swp391.eschoolmed.dto.response.*;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Builder
@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicalCheckupNotificationRepository medicalCheckupNotificationRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private MedicationRequestRepository medicationRequestRepository;
    @Autowired
    private MedicationScheduleRepository medicationScheduleRepository;
    @Autowired
    private MedicationItemRepository medicationItemRepository;
    @Autowired
    private HealthCheckupRepository healthCheckupRepository;

    public void updateParentProfile(UpdateParentProfileRequest request) {
        Parent parent = parentRepository.findByUserId(request.getUserid())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        parent.setFullName(request.getFullName());
        parent.setPhoneNumber(request.getPhoneNumber());
        parent.setAddress(request.getAddress());
        parent.setDateOfBirth(request.getDateOfBirth());

        parentRepository.save(parent);
    }

    public List<CheckupResultResponse> getCheckupResult(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        List<MedicalCheckupNotification> notifications = medicalCheckupNotificationRepository.findByParent(parent);

        return notifications.stream()
                .filter(MedicalCheckupNotification::getIsConfirmed)
                .map(notification -> {
                    Student student = notification.getStudent();

                    Optional<HealthCheckup> optionalCheckup = healthCheckupRepository.findByNotification(notification);

                    HealthCheckup checkup = optionalCheckup.orElse(null);

                    return CheckupResultResponse.builder()
                            .studentId(student.getStudentId())
                            .studentName(student.getFullName())
                            .className(student.getClassEntity() != null ? student.getClassEntity().getClassName() : null)
                            .hasChecked(checkup != null)
                            .heightCm(checkup != null ? checkup.getHeightCm() : null)
                            .weightKg(checkup != null ? checkup.getWeightKg() : null)
                            .visionLeft(checkup != null ? checkup.getVisionLeft() : null)
                            .visionRight(checkup != null ? checkup.getVisionRight() : null)
                            .notes(checkup != null ? checkup.getNotes() : null)
                            .build();
                })
                .toList();
    }


    @Transactional
    public MedicationRequestResponse sendMedicalRequestByUserId(MedicalRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        ParentStudent parentStudent = parentStudentRepository
                .findByParent_ParentIdAndStudent_StudentId(parent.getParentId(), request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Học sinh không được liên kết với phụ huynh này."));

        Student student = parentStudent.getStudent();

        MedicationRequest medicationRequest = MedicationRequest.builder()
                .requestId(UUID.randomUUID())
                .parent(parent)
                .student(student)
                .note(request.getNote())
                .requestDate(LocalDateTime.now())
                .status("PENDING")
                .build();

        medicationRequestRepository.save(medicationRequest);

        for (MedicalRequest.MedicationItemRequest itemReq : request.getMedications()) {
            List<String> schedules = itemReq.getSchedule();
            if (schedules == null || schedules.isEmpty()) {
                throw new IllegalArgumentException("Bạn phải chọn ít nhất một buổi uống thuốc (ví dụ: Sáng hoặc Chiều) cho thuốc: " + itemReq.getMedicationName());
            }

            MedicationItem item = MedicationItem.builder()
                    .itemId(UUID.randomUUID())
                    .request(medicationRequest)
                    .medicationName(itemReq.getMedicationName())
                    .dosage(itemReq.getDosage())
                    .note(itemReq.getNote())
                    .build();

            medicationItemRepository.save(item);

            for (String timeOfDayRaw : schedules) {
                if (timeOfDayRaw == null || timeOfDayRaw.isBlank()) continue;

                String mappedTimeOfDay = mapTimeOfDay(timeOfDayRaw);

                MedicationSchedule schedule = MedicationSchedule.builder()
                        .scheduleId(UUID.randomUUID())
                        .item(item)
                        .timeOfDay(mappedTimeOfDay)
                        .instruction(itemReq.getNote())
                        .hasTaken(false)
                        .build();

                medicationScheduleRepository.save(schedule);
            }
        }

        return MedicationRequestResponse.builder()
                .requestId(medicationRequest.getRequestId())
                .requestDate(medicationRequest.getRequestDate())
                .parentName(parent.getFullName())
                .studentName(student.getFullName())
                .note(request.getNote())
                .build();
    }



    public ParentProfileResponse getParentProfileFromJwt(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());

        Parent parent = parentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ phụ huynh"));

        UUID parentId = parent.getParentId();

        List<ParentStudent> linkedStudents = parentStudentRepository.findAllByParent_ParentId(parentId);

        String relationship = linkedStudents.isEmpty() ? null : linkedStudents.get(0).getRelationship();

        List<ParentProfileResponse.ChildInfo> children = linkedStudents.stream().map(ps -> {
            Student student = ps.getStudent();
            return ParentProfileResponse.ChildInfo.builder()
                    .studentName(student.getFullName())
                    .className(student.getClassEntity() != null
                            ? student.getClassEntity().getClassName()
                            : "Chưa cập nhật")
                    .studentDob(student.getDate_of_birth())
                    .gender(student.getGender())
                    .build();
        }).collect(Collectors.toList());

        LocalDate dob = parent.getDateOfBirth();

        return ParentProfileResponse.builder()
                .parentName(parent.getFullName())
                .email(parent.getUser().getEmail())
                .phoneNumber(parent.getPhoneNumber())
                .address(parent.getAddress())
                .relationship(relationship)
                .dateOfBirth(dob)
                .children(children)
                .build();
    }


    @Transactional
    public List<MedicationScheduleResponse> getSchedulesForLoggedInParent(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        List<ParentStudent> links = parentStudentRepository.findAllByParent_ParentId(parent.getParentId());
        List<UUID> studentIds = links.stream()
                .map(link -> link.getStudent().getStudentId())
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) {
            return Collections.emptyList();
        }

        List<MedicationSchedule> schedules = medicationScheduleRepository
                .findAllByItem_Request_Student_StudentIdIn(studentIds);

        return schedules.stream().map(schedule -> MedicationScheduleResponse.builder()
                .scheduleId(schedule.getScheduleId())
                .medicationName(schedule.getItem().getMedicationName())
                .timeOfDay(schedule.getTimeOfDay())
                .instruction(schedule.getInstruction())
                .hasTaken(schedule.getHasTaken())
                .takenTime(schedule.getTakenTime())
                .build()
        ).collect(Collectors.toList());
    }


    private String mapTimeOfDay(String time) {
        return switch (time.trim().toLowerCase()) {
            case "sáng" -> "morning";
            case "trưa" -> "noon";
            case "chiều" -> "evening";
            case "tối" -> "night";
            default -> throw new IllegalArgumentException("Thời điểm uống thuốc không hợp lệ: " + time);
        };
    }

    public void confirmCheckup(ConfirmCheckupRequest request) {
        MedicalCheckupNotification notification = medicalCheckupNotificationRepository
                .findById(request.getNotificationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt kiểm tra"));

        notification.setIsConfirmed(request.isConfirmed());
        notification.setConfirmedAt(LocalDateTime.now());

        medicalCheckupNotificationRepository.save(notification);
    }

}

