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
    private String parentEmail;
    private String parentPhone;
    private String parentAddress;
    private String parentCode;
    private LocalDate parentDob;

    private List<ChildInfo> children;

    @Data
    @Builder
    @AllArgsConstructor(access = AccessLevel.PUBLIC)
    public static class ChildInfo {
        private String studentCode;
        private String studentName;
        private String className;
        private LocalDate studentDob;
        private String gender;
        private String relationship;
    }
}
