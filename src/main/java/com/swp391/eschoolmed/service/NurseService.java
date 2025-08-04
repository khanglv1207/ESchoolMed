package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.CreateHealthCheckupRequest;
import com.swp391.eschoolmed.dto.request.CreateNurseRequest;
import com.swp391.eschoolmed.dto.request.UpdateMedicationStatusRequest;
import com.swp391.eschoolmed.dto.request.UpdateNurseRequest;
import com.swp391.eschoolmed.dto.response.*;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static javax.swing.text.html.parser.DTDConstants.ID;

@Service
public class NurseService {

    @Autowired
    private MedicalCheckupNotificationRepository notificationRepository;

    @Autowired
    private StudentService studentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MedicationRequestRepository medicationRequestRepository;
    @Autowired
    private MedicationScheduleRepository medicationScheduleRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private HealthCheckupRepository healthCheckupRepository;
    @Autowired
    private NurseRepository nurseRepository;
    @Autowired
    private MedicalCheckupNotificationRepository medicalCheckupNotificationRepository;



    public void createNurseFromUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
        if (!"NURSE".equalsIgnoreCase(user.getRole())) {
            throw new RuntimeException("Người dùng không có vai trò y tá.");
        }
        if (nurseRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Người dùng này đã được thêm làm y tá.");
        }
        Nurse nurse = new Nurse();
        nurse.setFullName(user.getFullName());
        nurse.setEmail(user.getEmail());
        nurse.setPhone(null);
        nurse.setSpecialization("Y tế học đường");
        nurseRepository.save(nurse);
    }

    public List<ConfirmedStudentResponse> getConfirmedStudents() {
        List<MedicalCheckupNotification> notifications = notificationRepository.findByIsConfirmedTrue();

        return notifications.stream().map(n -> ConfirmedStudentResponse.builder()
                .notificationId(n.getId())
                .studentName(n.getStudentName())
                .className(n.getClassName())
                .gender(n.getGender())
                .isConfirmed(n.getIsConfirmed())
                .build()
        ).toList();
    }

    public void createHealthCheckup(CreateHealthCheckupRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh"));
        Nurse nurse = nurseRepository.findById(request.getNurseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy y tá"));
        MedicalCheckupNotification notification = medicalCheckupNotificationRepository.findById(request.getCheckupId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông báo khám sức khỏe"));

        HealthCheckup checkup = new HealthCheckup();
        checkup.setNotification(notification);
        checkup.setStudent(student);
        checkup.setNurse(nurse);
        checkup.setCheckupDate(request.getCheckupDate());
        checkup.setHeightCm(request.getHeightCm());
        checkup.setWeightKg(request.getWeightKg());
        checkup.setVisionLeft(request.getVisionLeft());
        checkup.setVisionRight(request.getVisionRight());
        checkup.setNotes(request.getNotes());

        healthCheckupRepository.save(checkup);
    }


    public void updateMedicationStatus(UpdateMedicationStatusRequest request) {

        MedicationRequest medicationRequest = medicationRequestRepository.findById(request.getRequestId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc"));

        medicationRequest.setStatus(request.getStatus());

        if (request.getNote() != null) {
            medicationRequest.setNote(request.getNote()); // ghi chú từ y tế
        }

        medicationRequestRepository.save(medicationRequest);
    }

    public List<MedicationRequestResponse> getPendingMedicationRequests() {
        List<MedicationRequest> requests = medicationRequestRepository.findByStatus("PENDING");

        return requests.stream().map(request ->
                MedicationRequestResponse.builder()
                        .requestId(request.getRequestId())
                        .note(request.getNote())
                        .requestDate(request.getRequestDate())
                        .status(request.getStatus())
                        .parentName(request.getParent().getFullName())
                        .studentName(request.getStudent().getFullName())
                        .build()
        ).toList();
    }


    public List<MedicationScheduleForNurse> getTodaySchedulesByStudent(UUID studentId) {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();

        List<MedicationSchedule> schedules = medicationScheduleRepository.findUnTakenSchedules(studentId, todayStart);

        return schedules.stream().map(sch -> {
            MedicationItem item = sch.getItem();
            MedicationRequest request = item.getRequest();
            return MedicationScheduleForNurse.builder()
                    .scheduleId(sch.getScheduleId())
                    .studentName(request.getStudent().getFullName())
                    .medicationName(item.getMedicationName())
                    .dosage(item.getDosage())
                    .timeOfDay(sch.getTimeOfDay())
                    .instruction(sch.getInstruction())
                    .hasTaken(Boolean.TRUE.equals(sch.getHasTaken()))
                    .takenTime(sch.getTakenTime())
                    .build();
        }).toList();


    }

    public void markScheduleAsTaken(UUID scheduleId) {
        MedicationSchedule schedule = medicationScheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lịch uống thuốc."));
        schedule.setHasTaken(true);
        schedule.setTakenTime(LocalDateTime.now());
        medicationScheduleRepository.save(schedule);
    }

    public List<GetAllNurseResponse> getAllNurses() {
        List<Nurse> nurses = nurseRepository.findAll();
        return nurses.stream()
                .map(nurse -> GetAllNurseResponse.builder()
                        .nurseId(nurse.getNurseId())
                        .fullName(nurse.getFullName())
                        .email(nurse.getEmail())
                        .phone(nurse.getPhone())
                        .specialization(nurse.getSpecialization())
                        .build())
                .toList();
    }

    public void updateNurse(UpdateNurseRequest request) {
        Nurse nurse = nurseRepository.findById(request.getNurseId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy y tá"));

        nurse.setFullName(request.getFullName());
        nurse.setEmail(request.getEmail());
        nurse.setPhone(request.getPhone());
        nurse.setSpecialization(request.getSpecialization());

        nurseRepository.save(nurse);
    }

    public void deleteNurse(UUID nurseId) {
        if (!nurseRepository.existsById(nurseId)) {
            throw new RuntimeException("Không tìm thấy y tá để xóa");
        }
        nurseRepository.deleteById(nurseId);
    }


    public List<CheckedStudentResponse> getAllCheckedStudents() {
        List<HealthCheckup> checkups = healthCheckupRepository.findAllByOrderByCheckupDateDesc();

        return checkups.stream().map(checkup -> {
            Student student = checkup.getStudent();
            return CheckedStudentResponse.builder()
                    .studentId(student.getStudentId())
                    .studentName(student.getFullName())
                    .className(student.getClassEntity() != null ? student.getClassEntity().getClassName() : null)
                    .checkupDate(checkup.getCheckupDate())
                    .heightCm(checkup.getHeightCm())
                    .weightKg(checkup.getWeightKg())
                    .nurseName(checkup.getNurse() != null ? checkup.getNurse().getFullName() : null)
                    .build();
        }).toList();
    }

    public List<MedicalCheckupNoticeResponse> getAllMedicalCheckupNoticesForAdminOrNurse() {
        List<MedicalCheckupNotification> notifications = medicalCheckupNotificationRepository.findAllByOrderByCheckupDateDesc();

        return notifications.stream()
                .map(n -> MedicalCheckupNoticeResponse.builder()
                        .id(n.getId())
                        .checkupTitle(n.getCheckupTitle())
                        .checkupDate(n.getCheckupDate())
                        .studentName(n.getStudentName())
                        .className(n.getClassName())
                        .isConfirmed(n.getIsConfirmed())
                        .sentAt(n.getSentAt())
                        .confirmedAt(n.getConfirmedAt())
                        .build())
                .toList();
    }


}

