package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.CreateStudentParentRequest;
import com.swp391.eschoolmed.dto.request.UpdateStudentParentRequest;
import com.swp391.eschoolmed.dto.response.ParentStudentResponse;
import com.swp391.eschoolmed.model.*;
import com.swp391.eschoolmed.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class AdminService {
    @Autowired
    private ParentStudentRepository parentStudentRepository;

    @Autowired
    private UserRepository userRepository;

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
                    .studentId(s.getStudentId())
                    .studentName(s.getFullName())
                    .studentDob(s.getDate_of_birth())
                    .gender(s.getGender())
                    .classId(String.valueOf(s.getClass_id()))
                    .parentName(p.getFullName())
                    .parentEmail(p.getEmail())
                    .parentPhone(p.getPhoneNumber())
                    .relationship(ps.getRelationship())
                    .build();
        }).collect(Collectors.toList());
    }

    public void createStudentAndParent(CreateStudentParentRequest request) {
        Student student = new Student();
        UUID studentId = UUID.randomUUID();
        student.setStudentId(studentId);
        student.setStudentCode(generateNextStudentCode());
        student.setFullName(request.getStudentName());
        student.setDate_of_birth(LocalDate.parse(request.getStudentDob()));
        student.setGender(request.getGender());

        Optional<ClassEntity> clazz = classRepository.findByClassName(request.getClassName());
        if (clazz.isPresent()) {
            student.setClass_id(clazz.get().getClassId());
        } else {
            throw new RuntimeException("Không tìm thấy lớp: " + request.getClassName());
        }

        studentRepository.save(student);

        ParentStudent ps = new ParentStudent();
        ps.setId(UUID.randomUUID());
        ps.setStudent(student);
        ps.setRelationship(request.getRelationship());

        ps.setParentName(request.getParentName());
        ps.setParentEmail(request.getParentEmail());
        ps.setParentPhone(request.getParentPhone());
        ps.setParentAddress(request.getParentAddress());
        ps.setParentDob(LocalDate.parse(request.getParentDob()));
        ps.setParentCode(generateNextParentCode());

        ps.setStudentCode(student.getStudentCode());
        ps.setStudentName(student.getFullName());
        ps.setStudentDob(student.getDate_of_birth());
        ps.setGender(student.getGender());
        ps.setClassName(request.getClassName());

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

        UUID parentId = UUID.fromString(ps.getParentCode());
        UUID studentId = UUID.fromString(ps.getStudentCode());

        parentStudentRepository.delete(ps);

        // Sau khi xoá liên kết, xoá luôn student & parent
        parentRepository.deleteById(parentId);
        studentRepository.deleteById(studentId);
    }


    public void updateStudentAndParent(UpdateStudentParentRequest request) {
        ParentStudent ps = parentStudentRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy parent_student"));

        ps.setRelationship(request.getRelationship());
        ps.setStatus(request.getStatus());
        parentStudentRepository.save(ps);

        Student student = studentRepository.findById(UUID.fromString(request.getStudentCode()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy student"));

        student.setFullName(request.getStudentName());
        student.setStudentCode(request.getStudentCode());
        student.setDate_of_birth(request.getStudentDob());
        student.setGender(request.getGender());
        student.setClass_id(UUID.fromString(request.getClassName()));
        studentRepository.save(student);

        Parent parent = parentRepository.findById(UUID.fromString(request.getParentCode()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy parent"));

        parent.setFullName(request.getParentName());
        parent.setPhoneNumber(request.getParentPhone());
        parent.setEmail(request.getParentEmail());
        parent.setCode(request.getParentCode());
        parent.setAddress(request.getParentAddress());
        parent.setDateOfBirth(String.valueOf(request.getParentDob()));
        parentRepository.save(parent);
    }
}
