package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.SendVaccinationNoticeRequest;
import com.swp391.eschoolmed.dto.request.VaccinationConfirmationRequest;
import com.swp391.eschoolmed.dto.request.VaccinationResultRequest;
import com.swp391.eschoolmed.dto.response.StudentNeedVaccinationResponse;
import com.swp391.eschoolmed.dto.response.VaccinationResultResponse;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.VaccinationConfirmationRepository;
import com.swp391.eschoolmed.repository.VaccinationNotificationRepository;
import com.swp391.eschoolmed.repository.VaccinationResultRepository;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class VaccinationService {

    @Autowired
    private VaccinationConfirmationRepository  vaccinationConfirmationRepository;

    @Autowired
    private VaccinationNotificationRepository  vaccinationNotificationRepository;

    @Autowired
    private VaccinationResultRepository   vaccinationResultRepository;

    public void confirmVaccination(UUID parentId, VaccinationConfirmationRequest request) {
        VaccinationConfirmation confirmation = vaccinationConfirmationRepository.findById(request.getConfirmationId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xác nhận tiêm chủng."));

        UUID confirmationParentId = confirmation.getStudent()
                .getParent()
                .getUser()
                .getId();
        if (!confirmationParentId.equals(parentId)) {
            throw new SecurityException("Bạn không có quyền xác nhận thông tin này.");
        }
        if (confirmation.getStatus() != ConfirmationStatus.PENDING) {
            throw new IllegalStateException("Thông tin đã được xác nhận trước đó.");
        }
        confirmation.setStatus(request.getStatus());
        confirmation.setParentNote(request.getParentNote());
        confirmation.setConfirmedAt(LocalDateTime.now());
        vaccinationConfirmationRepository.save(confirmation);
    }

    public List<StudentNeedVaccinationResponse> getStudentsNeedVaccination() {
        List<VaccinationConfirmation> confirmations = vaccinationConfirmationRepository
                .findByStatusAndVaccinationResultIsNull(ConfirmationStatus.ACCEPTED);

        return confirmations.stream().map(conf -> {
            Student student = conf.getStudent();
            VaccineType vaccine = conf.getNotification().getVaccineType();

            return StudentNeedVaccinationResponse.builder()
                    .confirmationId(conf.getId())
                    .studentId(student.getStudentId())
                    .fullName(student.getFullName())
                    .className(student.getClass().getName())
                    .vaccineName(vaccine.getName())
                    .vaccinationDate(LocalDate.from(conf.getNotification().getScheduledDate()))
                    .build();
        }).toList();
    }

    public void recordVaccinationResult(VaccinationResultRequest request) {
        VaccinationConfirmation confirmation = vaccinationConfirmationRepository.findById(request.getConfirmationId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy yêu cầu xác nhận."));
        if (confirmation.getVaccinationResult() != null) {
            throw new IllegalStateException("Đã ghi nhận kết quả tiêm.");
        }
        VaccinationResult result = VaccinationResult.builder()
                .actualVaccinationDate(request.getVaccinationDate())
                .reactionNote(request.getNotes())
                .hasReaction(request.isHasReaction())
                .followUpNeeded(request.isFollowUpNeeded())
                .needsBooster(request.isNeedsBooster())
                .build();
        confirmation.setVaccinationResult(result);
        vaccinationConfirmationRepository.save(confirmation);
    }


    public List<VaccinationResultResponse> getVaccinationResultsByParent(UUID parentId) {
        List<VaccinationResult> results = vaccinationResultRepository
                .findAllByConfirmation_Student_Parent_UserId(parentId);

        return results.stream().map(result -> {
            VaccinationConfirmation conf = result.getConfirmation();
            Student student = conf.getStudent();

            return VaccinationResultResponse.builder()
                    .confirmationId(conf.getId())
                    .studentName(student.getFullName())
                    .className(student.getClass().getName())
                    .vaccineName(conf.getNotification().getVaccineType().getName())
                    .vaccinationDate(result.getActualVaccinationDate())
                    .hasReaction(result.getReactionNote() != null && !result.getReactionNote().isBlank())
                    .reactionNote(result.getReactionNote())
                    .needsBooster(result.isNeedsBooster())
                    .finalized(result.isFinalized())
                    .build();
        }).toList();
    }


}
