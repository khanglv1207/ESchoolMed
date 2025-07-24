package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.CreateStudentParentRequest;
import com.swp391.eschoolmed.dto.request.MedicalCheckupCreateRequest;
import com.swp391.eschoolmed.dto.request.UpdateStudentParentRequest;
import com.swp391.eschoolmed.dto.response.ParentStudentResponse;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private MedicalCheckupNotificationRepository medicalCheckupNotificationRepository;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private ClassRepository classRepository;

    public List<ParentStudentResponse> getAllParentStudent() {
        List<ParentStudent> list = parentStudentRepository.findAll();

        return list.stream().map(ps -> {
            Student s = ps.getStudent();
            Parent p = ps.getParent();
            return ParentStudentResponse.builder()
                    .StudentCode(s.getStudentCode())
                    .studentName(s.getFullName())
                    .studentDob(s.getDate_of_birth())
                    .gender(s.getGender())
                    .className(ps.getClassName())
                    .ParentCode(ps.getParentCode())
                    .parentName(p.getFullName())
                    .parentEmail(p.getEmail())
                    .parentPhone(p.getPhoneNumber())
                    .relationship(ps.getRelationship())
                    .parentAddress(p.getAddress())
                    .parentDob(s.getDate_of_birth())
                    .build();
        }).collect(Collectors.toList());
    }

    public void createStudentAndParent(CreateStudentParentRequest request) {
        Student student = new Student();
        UUID studentId = UUID.randomUUID();
        student.setStudentId(studentId);
        student.setStudentCode(generateNextStudentCode());
        student.setFullName(request.getStudentName());
        student.setDate_of_birth(request.getStudentDob());
        student.setGender(request.getGender());

        Optional<ClassEntity> clazz = classRepository.findByClassName(request.getClassName());
        if (clazz.isPresent()) {
            student.setClass_id(clazz.get().getClassId());
        } else {
            throw new RuntimeException("Không tìm thấy lớp: " + request.getClassName());
        }

        studentRepository.save(student);

        Parent parent = new Parent();
        UUID parentId = UUID.randomUUID();
        parent.setParentId(parentId);
        parent.setCode(generateNextParentCode());
        parent.setEmail(request.getParentEmail());
        parent.setFullName(request.getParentName());
        parent.setDateOfBirth(request.getParentDob());
        parent.setPhoneNumber(request.getParentPhone());
        parent.setAddress(request.getParentAddress());
        parentRepository.save(parent);

        ParentStudent ps = new ParentStudent();
        ps.setId(UUID.randomUUID());
        ps.setStudent(student);
        ps.setParent(parent);
        ps.setRelationship(request.getRelationship());

        ps.setStudentCode(student.getStudentCode());
        ps.setStudentName(student.getFullName());
        ps.setStudentDob(student.getDate_of_birth());
        ps.setGender(student.getGender());
        ps.setClassName(request.getClassName());

        ps.setParentCode(parent.getCode());
        ps.setParentName(parent.getFullName());
        ps.setParentEmail(parent.getEmail());
        ps.setParentPhone(parent.getPhoneNumber());
        ps.setParentDob(parent.getDateOfBirth());
        ps.setParentAddress(parent.getAddress());

        parentStudentRepository.save(ps);
    }


    private String generateNextStudentCode() {
        long count = studentRepository.count();
        return String.format("HS%04d", count + 1);
    }

    private String generateNextParentCode() {
        long count = parentRepository.count();
        return String.format("PH%04d", count + 1);
    }

    public void deleteStudentParent(UUID id) {
        ParentStudent ps = parentStudentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi"));

        UUID parentId = ps.getParent().getParentId();
        UUID studentId = ps.getStudent().getStudentId();
        parentStudentRepository.delete(ps);
        parentRepository.deleteById(parentId);
        studentRepository.deleteById(studentId);
    }


    public void updateStudentAndParent(UpdateStudentParentRequest request) {
        ParentStudent ps = parentStudentRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy parent_student"));

        ps.setRelationship(request.getRelationship());
        ps.setStatus(request.getStatus());
        parentStudentRepository.save(ps);

        Student student = ps.getStudent();
        student.setFullName(request.getStudentName());
        student.setStudentCode(request.getStudentCode());
        student.setDate_of_birth(request.getStudentDob());
        student.setGender(request.getGender());
        ClassEntity clazz = classRepository.findByClassName(request.getClassName())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lớp học"));
        student.setClass_id(clazz.getClassId());
        studentRepository.save(student);

        Parent parent = ps.getParent();
        parent.setFullName(request.getParentName());
        parent.setPhoneNumber(request.getParentPhone());
        parent.setEmail(request.getParentEmail());
        parent.setCode(request.getParentCode());
        parent.setAddress(request.getParentAddress());
        parent.setDateOfBirth(request.getParentDob());
        parentRepository.save(parent);
    }


    public void createMedicalCheckup(MedicalCheckupCreateRequest request) {
        for (UUID studentId : request.getStudentIds()) {
            Student student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh"));

            Parent parent = parentStudentRepository
                    .findFirstByStudent_StudentId(student.getStudentId())
                    .map(ParentStudent::getParent)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh"));

            MedicalCheckupNotification notification = new MedicalCheckupNotification();
            notification.setId(UUID.randomUUID());
            notification.setCheckupTitle(request.getCheckupTitle());
            notification.setCheckupDate(request.getCheckupDate());
            notification.setContent(request.getContent());

            notification.setStudent(student);
            notification.setStudentName(student.getFullName());
            notification.setClassName(student.getClassEntity().getClassName());
            notification.setGender(student.getGender());

            notification.setParent(parent);
            notification.setSentAt(LocalDateTime.now());
            notification.setIsConfirmed(null);

            medicalCheckupNotificationRepository.save(notification);
        }
    }
}
