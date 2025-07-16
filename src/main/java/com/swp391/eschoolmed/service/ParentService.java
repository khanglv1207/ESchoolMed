package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.MedicalRequest;
import com.swp391.eschoolmed.dto.request.UpdateParentProfileRequest;
import com.swp391.eschoolmed.dto.response.CheckupResultResponse;
import com.swp391.eschoolmed.dto.response.MedicationRequestResponse;
import com.swp391.eschoolmed.dto.response.ParentProfileResponse;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import jakarta.transaction.Transactional;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
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
    private MedicationRequestRepository  medicationRequestRepository;
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

        UUID parentStudentId = parentStudent.getId();

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
            MedicationItem item = MedicationItem.builder()
                    .itemId(UUID.randomUUID())
                    .request(medicationRequest)
                    .medicationName(itemReq.getMedicationName())
                    .dosage(itemReq.getDosage())
                    .note(itemReq.getNote())
                    .build();
            medicationItemRepository.save(item);

            for (String timeOfDay : itemReq.getSchedule()) {
                MedicationSchedule schedule = MedicationSchedule.builder()
                        .scheduleId(UUID.randomUUID())
                        .item(item)
                        .timeOfDay(timeOfDay)
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

        List<ParentProfileResponse.ChildInfo> children = linkedStudents.stream().map(ps -> {
            Student student = ps.getStudent();
            return ParentProfileResponse.ChildInfo.builder()
                    .studentCode(student.getStudentCode())
                    .studentName(student.getFullName())
                    .className(student.getClassEntity() != null
                            ? student.getClassEntity().getClassName()
                            : "Chưa cập nhật")
                    .studentDob(student.getDate_of_birth())
                    .gender(student.getGender())
                    .build();
        }).collect(Collectors.toList());

        String dobStr = parent.getDateOfBirth();
        LocalDate dob = null;
        if (dobStr != null && !dobStr.isBlank()) {
            dob = LocalDate.parse(dobStr);
        }

        return ParentProfileResponse.builder()
                .parentName(parent.getFullName())
                .email(parent.getUser().getEmail())
                .phoneNumber(parent.getPhoneNumber())
                .address(parent.getAddress())
                .dateOfBirth(dob)
                .children(children)
                .build();
    }

    public List<MedicationRequestResponse> getRequestsByStudentId(UUID studentId) {
        List<MedicationRequest> requests = medicationRequestRepository.findByStudent_StudentId(studentId);

        return requests.stream().map(request -> MedicationRequestResponse.builder()
                .requestId(request.getRequestId())
                .note(request.getNote())
                .requestDate(request.getRequestDate())
                .status(request.getStatus())
                .parentName(request.getParent().getFullName())
                .studentName(request.getStudent().getFullName())
                .build()
        ).collect(Collectors.toList());
    }

}

