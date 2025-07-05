package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.UpdateMedicationStatusRequest;
import com.swp391.eschoolmed.dto.response.MedicationRequestResponse;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.model.MedicalCheckupNotification;
import com.swp391.eschoolmed.model.MedicationRequest;
import com.swp391.eschoolmed.repository.MedicalCheckupNotificationRepository;
import com.swp391.eschoolmed.repository.MedicationRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

        return requests.stream().map(request -> MedicationRequestResponse.builder()
                .requestId(request.getRequestId())
                .studentName(request.getStudent().getFullName())
                .medicationName(request.getMedicationName())
                .dosage(request.getDosage())
                .frequency(request.getFrequency())
                .note(request.getNote())
                .requestDate(request.getRequestDate())
                .status(request.getStatus())
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
}
