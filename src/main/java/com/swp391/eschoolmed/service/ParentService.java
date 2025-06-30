package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.UpdateParentProfileRequest;
import com.swp391.eschoolmed.dto.response.ParentProfileResponse;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.ParentStudent;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.ParentRepository;
import com.swp391.eschoolmed.repository.ParentStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ParentStudentRepository parentStudentRepository;

    public void updateParentProfile(UpdateParentProfileRequest request) {
        Parent parent = parentRepository.findByUserId(request.getUserid())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        parent.setFullName(request.getFullName());
        parent.setPhoneNumber(request.getPhoneNumber());
        parent.setAddress(request.getAddress());
        parent.setDateOfBirth(request.getDateOfBirth());

        parentRepository.save(parent);
    }

    public ParentProfileResponse getParentProfile(UUID userId) {
        Parent parent = parentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ phụ huynh"));
        User user = parent.getUser();

        List<ParentStudent> linkedStudents = parentStudentRepository.findAllByParent_ParentId(parent.getParentId());

        List<ParentProfileResponse.ChildInfo> children = linkedStudents.stream().map(ps -> {
            Student student = ps.getStudent();
            return ParentProfileResponse.ChildInfo.builder()
                    .studentCode(student.getStudentCode())
                    .studentName(student.getFullName())
                    .className(String.valueOf(student.getClass_id()))
                    .studentDob(student.getDate_of_birth())
                    .gender(student.getGender())
                    .build();
        }).collect(Collectors.toList());

        return ParentProfileResponse.builder()
                .parentName(parent.getFullName())
                .email(user.getEmail())
                .phoneNumber(parent.getPhoneNumber())
                .address(parent.getAddress())
                .dateOfBirth(LocalDate.parse(parent.getDateOfBirth()))
                .children(children)
                .build();
    }


    static StudentProfileResponse getStudentProfileResponse(Student student) {
        StudentProfileResponse s = new StudentProfileResponse();
        s.setStudentId(student.getStudentId());
        s.setFullName(student.getFullName());
        s.setClass_id(student.getClass_id());
        s.setDate_of_birth(student.getDate_of_birth());
        s.setGender(student.getGender());
        return s;
    }



}

