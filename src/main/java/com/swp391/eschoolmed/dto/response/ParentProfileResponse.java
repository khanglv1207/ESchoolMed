package com.swp391.eschoolmed.dto.response;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PUBLIC)
public class ParentProfileResponse {
    private String parentName;
    private String email;
    private String phoneNumber;
    private String address;
    private String relationship;
    private LocalDate dateOfBirth;

    private List<ChildInfo> children;

    @Data
    @Builder
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class ChildInfo {
        private String studentName;
        private String className;
        private LocalDate studentDob;
        private String gender;
    }
}


