package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.CreateVaccineTypeRequest;
import com.swp391.eschoolmed.dto.request.SendVaccinationNoticeRequest;
import com.swp391.eschoolmed.dto.request.VaccinationConfirmationRequest;
import com.swp391.eschoolmed.dto.request.VaccinationResultRequest;
import com.swp391.eschoolmed.dto.response.GetAllVaccineTypesResponse;
import com.swp391.eschoolmed.dto.response.StudentNeedVaccinationResponse;
import com.swp391.eschoolmed.dto.response.VaccinationResultResponse;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.naming.Context;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VaccinationService {

    @Autowired
    private VaccinationConfirmationRepository vaccinationConfirmationRepository;

    @Autowired
    private VaccinationNotificationRepository vaccinationNotificationRepository;

    @Autowired
    private VaccinationResultRepository vaccinationResultRepository;

    @Autowired
    private VaccineTypeRepository vaccineTypeRepository;
    @Autowired
    private StudentRepository studentRepository;
    @Autowired
    private ParentStudentRepository  parentStudentRepository;
    @Autowired
    private ParentRepository parentRepository;

    public List<StudentNeedVaccinationResponse> findEligibleStudentsForNotification(String vaccineName) {
        VaccineType vaccineType = vaccineTypeRepository.findByNameIgnoreCaseTrimmed(vaccineName)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy loại vaccine: " + vaccineName));

        List<Student> students = studentRepository.findEligibleStudentsByVaccine(vaccineName);

        return students.stream()
                .filter(student -> !vaccinationNotificationRepository.existsByStudentAndVaccineType(student, vaccineType))
                .map(student -> {
                    String parentEmail = "N/A";

                    List<ParentStudent> links = parentStudentRepository.findByStudent_StudentId(student.getStudentId());
                    if (links != null && !links.isEmpty()) {
                        Parent parent = links.get(0).getParent();
                        if (parent != null && parent.getEmail() != null) {
                            parentEmail = parent.getEmail();
                        }
                    }
                    return StudentNeedVaccinationResponse.builder()
                            .studentId(student.getStudentId())
                            .studentCode(student.getStudentCode())
                            .studentName(student.getFullName())
                            .className(student.getClassEntity() != null ? student.getClassEntity().getClassName() : "N/A")
                            .parentEmail(parentEmail)
                            .vaccineName(vaccineType.getName())
                            .build();
                }).toList();
    }



    public void createVaccineType(CreateVaccineTypeRequest request) {
        if (vaccineTypeRepository.findByNameIgnoreCaseTrimmed(request.getName()).isPresent()) {
            throw new RuntimeException("Loại vaccine đã tồn tại");
        }

        VaccineType vaccineType = new VaccineType();
        vaccineType.setName(request.getName());
        vaccineType.setDescription(request.getDescription());
        vaccineType.setDosesRequired(request.getDosesRequired());
        vaccineType.setIntervalDays(request.getIntervalDays());

        vaccineTypeRepository.save(vaccineType);
    }

    public List<GetAllVaccineTypesResponse> getAllVaccineTypes() {
        List<VaccineType> vaccineTypes = vaccineTypeRepository.findAll();

        return vaccineTypes.stream().map(vt -> GetAllVaccineTypesResponse.builder()
                        .id(vt.getId())
                        .name(vt.getName())
                        .description(vt.getDescription())
                        .dosesRequired(vt.getDosesRequired())
                        .intervalDays(vt.getIntervalDays())
                        .build())
                .toList();
    }

    public void confirmVaccination(UUID userId, VaccinationConfirmationRequest request) {
        VaccinationConfirmation confirmation = vaccinationConfirmationRepository.findById(request.getConfirmationId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xác nhận tiêm chủng."));

        Student student = confirmation.getStudent();
        if (student == null) {
            throw new IllegalStateException("Xác nhận không gắn với học sinh.");
        }
        ParentStudent parentStudent = parentStudentRepository.findFirstByStudent_StudentId(student.getStudentId())
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy phụ huynh của học sinh."));

        Parent parent = parentStudent.getParent();
        if (parent == null || parent.getUser() == null) {
            throw new IllegalStateException("Thông tin người dùng phụ huynh không hợp lệ.");
        }
        UUID confirmationUserId = parent.getUser().getId();
        if (!confirmationUserId.equals(userId)) {
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
                    .studentName(student.getFullName())
                    .className(student.getClass().getName())
                    .vaccineName(vaccine.getName())
                    .vaccinationDate(LocalDate.from(conf.getNotification().getScheduledDate()))
                    .build();
        }).toList();
    }

    public void createVaccinationResult(VaccinationResultRequest request) {
        VaccinationConfirmation confirmation = vaccinationConfirmationRepository
                .findById(request.getConfirmationId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xác nhận tiêm."));

        Optional<VaccinationResult> existing = vaccinationResultRepository.findByConfirmation(confirmation);
        if (existing.isPresent()) {
            throw new IllegalStateException("Kết quả đã được nhập cho xác nhận này.");
        }

        VaccinationResult result = VaccinationResult.builder()
                .confirmation(confirmation)
                .actualVaccinationDate(request.getVaccinationDate())
                .hasReaction(request.isHasReaction())
                .followUpNeeded(request.isFollowUpNeeded())
                .needsBooster(request.isNeedsBooster())
                .reactionNote(request.getNotes())
                .successful(true)
                .finalized(false)
                .updatedAt(LocalDateTime.now())
                .build();

        vaccinationResultRepository.save(result);
    }

    public List<VaccinationResultResponse> getVaccinationResultsForParent(UUID userId) {
        Parent parent = parentRepository.findByUser_Id(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        List<ParentStudent> parentStudents = parentStudentRepository.findByParent_ParentId(parent.getParentId());
        List<UUID> studentIds = parentStudents.stream()
                .map(ps -> ps.getStudent().getStudentId())
                .collect(Collectors.toList());

        if (studentIds.isEmpty()) return Collections.emptyList();

        List<VaccinationResult> results = vaccinationResultRepository
                .findAllByConfirmation_Student_StudentIdIn(studentIds);

        return results.stream()
                .map(result -> {
                    VaccinationConfirmation conf = result.getConfirmation();
                    Student student = conf.getStudent();

                    return VaccinationResultResponse.builder()
                            .confirmationId(conf.getId())
                            .studentName(student.getFullName())
                            .className(student.getClass_id().getClass().getName())
                            .vaccineName(conf.getNotification().getVaccineType().getName())
                            .vaccinationDate(result.getActualVaccinationDate())
                            .hasReaction(result.getReactionNote() != null && !result.getReactionNote().isBlank())
                            .reactionNote(result.getReactionNote())
                            .needsBooster(result.isNeedsBooster())
                            .finalized(result.isFinalized())
                            .build();
                })
                .collect(Collectors.toList());
    }





}
