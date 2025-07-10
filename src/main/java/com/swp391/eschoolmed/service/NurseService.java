package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.UpdateMedicationStatusRequest;
import com.swp391.eschoolmed.dto.response.MedicationRequestResponse;
import com.swp391.eschoolmed.dto.response.MedicationScheduleForNurse;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.model.MedicationItem;
import com.swp391.eschoolmed.model.MedicationRequest;
import com.swp391.eschoolmed.model.MedicationSchedule;
import com.swp391.eschoolmed.repository.MedicalCheckupNotificationRepository;
import com.swp391.eschoolmed.repository.MedicationRequestRepository;
import com.swp391.eschoolmed.repository.MedicationScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NurseService {

    @Autowired
    private MedicalCheckupNotificationRepository notificationRepository;

    @Autowired
    private StudentService studentService;

    @Autowired
    private MedicationRequestRepository medicationRequestRepository;
    @Autowired
    private MedicationScheduleRepository medicationScheduleRepository;

    public List<StudentProfileResponse> confirmStudent(UUID checkupId) {
        List<MedicalCheckupNotification> notifications = notificationRepository.findByCheckupTitle(String.valueOf(checkupId));
        return notifications.stream()
                .map(MedicalCheckupNotification::getStudent)
                .map(studentService::toStudentProfileResponse)
                .collect(Collectors.toList());
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
                        .medications(request.getItems())
                        .build()
        ).toList();
    }

    public void processMedicationRequest(UUID requestId, String status) {
        MedicationRequest request = medicationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn thuốc."));

        if (!status.equalsIgnoreCase("APPROVED") && !status.equalsIgnoreCase("REJECTED")) {
            throw new RuntimeException("Trạng thái không hợp lệ.");
        }

        request.setStatus(status.toUpperCase());
        request.setProcessedTime(LocalDateTime.now());

        medicationRequestRepository.save(request);
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

}

