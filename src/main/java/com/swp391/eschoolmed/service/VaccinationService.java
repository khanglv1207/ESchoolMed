package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.*;
import com.swp391.eschoolmed.dto.response.*;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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


    public List<VaccinationNotificationResponse> getVaccinationNotifications(UUID userId) {
        return vaccinationNotificationRepository.findNotificationsByUserId(userId)
                .stream()
                .map(n -> new VaccinationNotificationResponse(
                        n.getId(),
                        n.getVaccineType().getName(),
                        n.getLocation(),
                        n.getNote(),
                        n.getScheduledDate()
                ))
                .collect(Collectors.toList());
    }

    public List<VaccinationConfirmationResponse> getVaccinationConfirmations(UUID userId) {
        Parent parent = parentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh."));

        List<VaccinationConfirmation> confirmations = vaccinationConfirmationRepository
                .findByStudent_Parent(parent);

        return confirmations.stream()
                .map(confirmation -> {
                    Student student = confirmation.getStudent();
                    VaccinationNotification notification = confirmation.getNotification();
                    return VaccinationConfirmationResponse.builder()
                            .studentId(student.getStudentId())
                            .studentName(student.getFullName())
                            .vaccineName(notification.getVaccineType().getName())
                            .scheduledDate(notification.getScheduledDate().toLocalDate())
                            .status(confirmation.getStatus().name())
                            .confirmedAt(confirmation.getConfirmedAt())
                            .build();
                }).toList();
    }


    @Transactional
    public void confirmVaccinationFromEmail(UUID confirmationId, ConfirmationStatus status) {
        VaccinationConfirmation confirmation = vaccinationConfirmationRepository.findById(confirmationId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy xác nhận"));

        if (confirmation.getStatus() != ConfirmationStatus.PENDING) {
            throw new IllegalStateException("Đã xác nhận trước đó");
        }

        confirmation.setStatus(status);
        confirmation.setConfirmedAt(LocalDateTime.now());
        vaccinationConfirmationRepository.save(confirmation);
    }






}
