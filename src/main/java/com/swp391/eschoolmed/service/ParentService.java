package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.ConfirmCheckupRequest;
import com.swp391.eschoolmed.dto.request.HealthProfileRequest;
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
    @Autowired
    private HealthProfileRepository healthProfileRepository;
    @Autowired
    private ClassRepository  classRepository;

    @Transactional
    public void updateParentProfile(UpdateParentProfileRequest request) {
        Parent parent = parentRepository.findByUserId(request.getUserid())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));

        parent.setFullName(request.getFullName());
        parent.setPhoneNumber(request.getPhoneNumber());
        parent.setAddress(request.getAddress());
        parent.setDateOfBirth(request.getDateOfBirth());
        if (parent.getUser() != null) {
            parent.getUser().setEmail(request.getEmail());
        }
        parentRepository.save(parent);

        List<ParentStudent> links = parentStudentRepository.findAllByParent_ParentId(parent.getParentId());
        for (UpdateParentProfileRequest.ChildUpdateRequest childReq : request.getChildren()) {
            ParentStudent ps = links.stream()
                    .filter(l -> l.getStudent().getStudentId().equals(childReq.getStudentId()))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh liên kết với phụ huynh"));

            Student student = ps.getStudent();
            student.setFullName(childReq.getStudentName());
            student.setDate_of_birth(childReq.getStudentDob());
            student.setGender(childReq.getGender());
            ClassEntity classEntity = classRepository.findByClassNameIgnoreCase(childReq.getClassName().trim())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học tên: " + childReq.getClassName()));
            student.setClassEntity(classEntity);
            studentRepository.save(student);
        }
    }


    public List<CheckupResultResponse> getCheckupResult(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        List<ParentStudent> parentStudents = parentStudentRepository.findByParent_ParentId(parent.getParentId());

        List<UUID> studentIds = parentStudents.stream()
                .map(ps -> ps.getStudent().getStudentId())
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) return Collections.emptyList();

        List<HealthCheckup> checkups = healthCheckupRepository.findByStudent_StudentIdIn(studentIds);

        return checkups.stream()
                .map(checkup -> {
                    Student student = checkup.getStudent();
                    return CheckupResultResponse.builder()
                            .studentId(student.getStudentId())
                            .studentName(student.getFullName())
                            .className(student.getClassEntity() != null ? student.getClassEntity().getClassName() : null)
                            .hasChecked(true)
                            .heightCm(checkup.getHeightCm())
                            .weightKg(checkup.getWeightKg())
                            .visionLeft(checkup.getVisionLeft())
                            .visionRight(checkup.getVisionRight())
                            .notes(checkup.getNotes())
                            .build();
                }).collect(Collectors.toList());
    }




    @Transactional
    public MedicationRequestResponse sendMedicalRequestByUserId(MedicalRequest request, UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng."));

        Parent parent = parentRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ phụ huynh."));

        if (request.getStudentCode() == null || request.getStudentCode().isBlank()) {
            throw new IllegalArgumentException("Thiếu mã học sinh (studentCode).");
        }

        Student student = studentRepository.findByStudentCode(request.getStudentCode())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh với mã: " + request.getStudentCode()));

        ParentStudent parentStudent = parentStudentRepository
                .findByParent_ParentIdAndStudent_StudentId(parent.getParentId(), student.getStudentId())
                .orElseThrow(() -> new RuntimeException("Học sinh không được liên kết với phụ huynh này."));
        List<MedicalRequest.MedicationItemRequest> medicationItems = request.getMedications();
        if (medicationItems == null || medicationItems.isEmpty()) {
            throw new IllegalArgumentException("Phải có ít nhất một loại thuốc trong đơn.");
        }

        MedicationRequest medicationRequest = MedicationRequest.builder()
                .requestId(UUID.randomUUID())
                .parent(parent)
                .student(student)
                .studentCode(student.getStudentCode())
                .note(request.getNote())
                .requestDate(LocalDateTime.now())
                .status("PENDING")
                .build();

        medicationRequestRepository.save(medicationRequest);

        for (MedicalRequest.MedicationItemRequest itemReq : medicationItems) {
            List<String> schedules = itemReq.getSchedule();

            if (schedules == null || schedules.isEmpty()) {
                throw new IllegalArgumentException("Bạn phải chọn ít nhất một buổi uống thuốc (VD: Sáng, Chiều) cho thuốc: " + itemReq.getMedicationName());
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


    public List<ParentStudentResponse> getStudentsOfLoggedInParent(UUID userId) {
        Parent parent = parentRepository.findByUser_id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ phụ huynh."));
        List<ParentStudent> parentStudentList = parentStudentRepository.findAllByParent_ParentId(parent.getParentId());

        if (parentStudentList.isEmpty()) {
            throw new RuntimeException("Không có học sinh nào được liên kết với tài khoản này.");
        }
        return parentStudentList.stream()
                .map(ps -> ParentStudentResponse.builder()
                        .StudentCode(ps.getStudentCode())
                        .studentName(ps.getStudentName())
                        .studentDob(ps.getStudentDob())
                        .gender(ps.getGender())
                        .className(ps.getClassName())

                        .ParentCode(ps.getParentCode())
                        .parentName(ps.getParentName())
                        .parentEmail(ps.getParentEmail())
                        .parentPhone(ps.getParentPhone())
                        .relationship(ps.getRelationship())
                        .parentDob(ps.getParentDob())
                        .parentAddress(ps.getParentAddress())
                        .build()
                )
                .toList();
    }


    public ParentProfileResponse getParentProfileFromJwt(Jwt jwt) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Parent parent = parentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ phụ huynh"));
        UUID parentId = parent.getParentId();

        List<ParentStudent> linkedStudents = parentStudentRepository.findAllByParent_ParentId(parentId);
        List<ParentProfileResponse.ChildInfo> children = linkedStudents.stream()
                .map(ps -> ParentProfileResponse.ChildInfo.builder()
                        .studentCode(ps.getStudentCode())
                        .studentName(ps.getStudentName())
                        .className(ps.getClassName())
                        .studentDob(ps.getStudentDob())
                        .gender(ps.getGender())
                        .relationship(ps.getRelationship())
                        .build())
                .toList();
        return ParentProfileResponse.builder()
                .parentName(parent.getFullName())
                .parentEmail(parent.getUser().getEmail())
                .parentPhone(parent.getPhoneNumber())
                .parentAddress(parent.getAddress())
                .parentCode(linkedStudents.isEmpty() ? null : linkedStudents.get(0).getParentCode())
                .parentDob(parent.getDateOfBirth())
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

    public void confirmCheckup(UUID userId, ConfirmCheckupRequest request) {
        MedicalCheckupNotification notification = medicalCheckupNotificationRepository
                .findById(request.getNotificationId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt kiểm tra"));
        Parent parent = notification.getParent();
        if (parent == null || parent.getUser() == null) {
            throw new IllegalStateException("Thông tin người dùng phụ huynh không hợp lệ.");
        }
        UUID notificationUserId = parent.getUser().getId();
        if (!notificationUserId.equals(userId)) {
            throw new SecurityException("Bạn không có quyền xác nhận thông báo này.");
        }
        if (notification.getIsConfirmed() != null) {
            throw new IllegalStateException("Thông báo này đã được xác nhận.");
        }
        notification.setIsConfirmed(request.isConfirmed());
        notification.setConfirmedAt(LocalDateTime.now());
        medicalCheckupNotificationRepository.save(notification);
    }



    @Transactional
    public void createOrUpdateHealthProfile(UUID userId, HealthProfileRequest request) {
        Parent parent = parentRepository.findByUser_id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        List<ParentStudent> parentStudents = parentStudentRepository.findByParent_ParentId(parent.getParentId());
        if (parentStudents.isEmpty()) {
            throw new RuntimeException("Không tìm thấy học sinh liên kết với phụ huynh.");
        }
        Student student = parentStudents.get(0).getStudent();
        Optional<HealthProfile> optionalProfile = healthProfileRepository.findByStudent_StudentId(student.getStudentId());

        HealthProfile profile;
        if (optionalProfile.isPresent()) {
            profile = optionalProfile.get();
            profile.setAllergies(request.getAllergies());
            profile.setChronicDiseases(request.getChronicDiseases());
            profile.setMedicalHistory(request.getMedicalHistory());
            profile.setEyesight(request.getEyesight());
            profile.setHearing(request.getHearing());
            profile.setVaccinationRecord(request.getVaccinationRecord());
        } else {
            profile = HealthProfile.builder()
                    .student(student)
                    .allergies(request.getAllergies())
                    .chronicDiseases(request.getChronicDiseases())
                    .medicalHistory(request.getMedicalHistory())
                    .eyesight(request.getEyesight())
                    .hearing(request.getHearing())
                    .vaccinationRecord(request.getVaccinationRecord())
                    .createdAt(LocalDateTime.now())
                    .build();
        }
        healthProfileRepository.save(profile);
    }

    @Transactional
    public HealthProfileResponse getLatestHealthProfile(UUID userId) {
        Parent parent = parentRepository.findByUser_id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        List<ParentStudent> parentStudents = parentStudentRepository.findByParent_ParentId(parent.getParentId());
        if (parentStudents.isEmpty()) {
            throw new RuntimeException("Không tìm thấy học sinh liên kết.");
        }
        Student student = parentStudents.get(0).getStudent();

        HealthProfile profile = healthProfileRepository
                .findFirstByStudent_StudentIdOrderByUpdatedAtDesc(student.getStudentId())
                .orElseThrow(() -> new RuntimeException("Chưa có hồ sơ sức khỏe."));

        return HealthProfileResponse.builder()
                .studentName(student.getFullName())
                .allergies(profile.getAllergies())
                .chronicDiseases(profile.getChronicDiseases())
                .medicalHistory(profile.getMedicalHistory())
                .eyesight(profile.getEyesight())
                .hearing(profile.getHearing())
                .vaccinationRecord(profile.getVaccinationRecord())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }




}

