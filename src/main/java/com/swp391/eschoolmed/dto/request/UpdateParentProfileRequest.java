package com.swp391.eschoolmed.dto.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class UpdateParentProfileRequest {
    private UUID userid;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String address;
    private LocalDate dateOfBirth;

    private List<ChildUpdateRequest> children;

    @Data
    public static class ChildUpdateRequest {
        private UUID studentId;
        private String studentName;
        private String className;
        private LocalDate studentDob;
        private String gender;
    }
}
