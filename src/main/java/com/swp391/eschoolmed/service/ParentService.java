package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.MedicalRequest;
import com.swp391.eschoolmed.dto.request.UpdateParentProfileRequest;
import com.swp391.eschoolmed.dto.response.CheckupResultResponse;
import com.swp391.eschoolmed.dto.response.MedicationRequestResponse;
import com.swp391.eschoolmed.dto.response.ParentProfileResponse;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
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

    public void updateParentProfile(UpdateParentProfileRequest request) {
        Parent parent = parentRepository.findByUserId(request.getUserid())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        parent.setFullName(request.getFullName());
        parent.setPhoneNumber(request.getPhoneNumber());
        parent.setAddress(request.getAddress());
        parent.setDateOfBirth(request.getDateOfBirth());

        parentRepository.save(parent);
    }

    public List<CheckupResultResponse> getCheckupResult(String username) {
        User parentUser = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + username));

        Parent parent = parentRepository.findByUser(parentUser)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin phụ huynh."));

        List<MedicalCheckupNotification> notifications =
                medicalCheckupNotificationRepository.findByParent(parent);

        return notifications.stream()
                .filter(MedicalCheckupNotification::getIsConfirmed)
                .map(notification -> CheckupResultResponse.builder()
                        .studentName(notification.getStudent().getFullName())
                        .checkupDate(notification.getCheckupDate())
                        .checkupTitle(notification.getCheckupTitle())
                        .resultSummary(notification.getResultSummary())
                        .isAbnormal(notification.getIsAbnormal())
                        .suggestion(notification.getSuggestion())
                        .build())
                .toList();

    }

    public MedicationRequest sendMedicalRequest(MedicalRequest request, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        // ktra hs có liên kết với ph k
        boolean isLinked = parentStudentRepository
                .findAllByParent_ParentId(parent.getParentId())
                .stream()
                .anyMatch(ps -> ps.getStudent().getStudentId().equals(request.getStudentId()));

        if (!isLinked) {
            throw new RuntimeException("Học sinh không được liên kết với phụ huynh này.");
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh."));

        MedicationRequest medicationRequest = new MedicationRequest();
        medicationRequest.setRequestId(UUID.randomUUID());
        medicationRequest.setParent(parent);
        medicationRequest.setStudent(student);
        medicationRequest.setMedicationName(request.getMedicationName());
        medicationRequest.setDosage(request.getDosage());
        medicationRequest.setFrequency(request.getFrequency());
        medicationRequest.setNote(request.getNote());
        medicationRequest.setRequestDate(LocalDateTime.now());
        medicationRequest.setStatus("PENDING");

        return medicationRequestRepository.save(medicationRequest);
    }

    public MedicationRequestResponse sendMedicalRequestByUserId(MedicalRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        boolean isLinked = parentStudentRepository
                .findAllByParent_ParentId(parent.getParentId())
                .stream()
                .anyMatch(ps -> ps.getStudent().getStudentId().equals(request.getStudentId()));

        if (!isLinked) {
            throw new RuntimeException("Học sinh không được liên kết với phụ huynh này.");
        }

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh."));

        MedicationRequest medicationRequest = MedicationRequest.builder()
                .requestId(UUID.randomUUID())
                .parent(parent)
                .student(student)
                .medicationName(request.getMedicationName())
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .note(request.getNote())
                .requestDate(LocalDateTime.now())
                .status("PENDING")
                .build();

        medicationRequestRepository.save(medicationRequest);

        return MedicationRequestResponse.builder()
                .requestId(medicationRequest.getRequestId())
                .medicationName(medicationRequest.getMedicationName())
                .dosage(medicationRequest.getDosage())
                .frequency(medicationRequest.getFrequency())
                .note(medicationRequest.getNote())
                .requestDate(medicationRequest.getRequestDate())
                .status(medicationRequest.getStatus())
                .parentName(parent.getFullName())
                .studentName(student.getFullName())
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

}

