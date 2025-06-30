package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.ParentStudentUpdateRequest;
import com.swp391.eschoolmed.dto.request.StudentProfileRequest;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.exception.AppException;
import com.swp391.eschoolmed.exception.ErrorCode;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.ParentStudent;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.repository.ParentRepository;
import com.swp391.eschoolmed.repository.ParentStudentRepository;
import com.swp391.eschoolmed.repository.StudentRepository;
import com.swp391.eschoolmed.repository.UserRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import static com.swp391.eschoolmed.service.ParentService.getStudentProfileResponse;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ParentRepository parentRepository;

    @Autowired
    private ParentStudentRepository  parentStudentRepository;




    public void updateStudentProfile(UUID studentId, StudentProfileRequest request, String token) {
        UUID userId = userService.extractUserIdFromToken(token);

        Parent parent = parentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phụ huynh"));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        student.setFullName(request.getFullName());
        student.setClass_id(request.getClass_id());
        student.setGender(request.getGender());
        student.setDate_of_birth(request.getDate_of_birth());

        studentRepository.save(student);

    }

    public StudentProfileResponse getStudentProfile(UUID studentId) {
        Student student = studentRepository.findStudentByStudentId(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ học sinh"));

        return getStudentProfileResponse(student);
    }


    public void updateImportedParentStudent(ParentStudentUpdateRequest request) {
        ParentStudent record = parentStudentRepository.findById(request.getId())
                .orElseThrow(() -> new RuntimeException( "Không tìm thấy bản ghi"));

        record.setStudentName(request.getStudentName());
        record.setClassName(request.getClassName());
        record.setStudentDob(request.getStudentDob());
        record.setGender(request.getGender());
        record.setParentName(request.getParentName());
        record.setParentPhone(request.getParentPhone());
        record.setParentDob(request.getParentDob());
        record.setParentAddress(request.getParentAddress());
        record.setRelationship(request.getRelationship());
        parentStudentRepository.save(record);
    }

    public void importParentStudentFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) rowIterator.next(); // Bỏ header

            int rowIndex = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowIndex++;

                try {
                    String studentCode = getString(row.getCell(0));
                    String studentName = getString(row.getCell(1));
                    String className = getString(row.getCell(2));
                    LocalDate studentDob = getDate(row.getCell(3));
                    String gender = getString(row.getCell(4));
                    String parentCode = getString(row.getCell(5));
                    String parentName = getString(row.getCell(6));
                    String parentEmail = getString(row.getCell(7));
                    String parentPhone = getString(row.getCell(8));
                    LocalDate parentDob = getDate(row.getCell(9));
                    String parentAddress = getString(row.getCell(10));
                    String relationship = getString(row.getCell(11));
                    String status = getString(row.getCell(12));

                    // Nếu thiếu mã → tự sinh
                    if (studentCode == null || studentCode.isBlank()) {
                        studentCode = generateNextStudentCode();
                    }
                    if (parentCode == null || parentCode.isBlank()) {
                        parentCode = generateNextParentCode();
                    }

                    ParentStudent ps = new ParentStudent();
                    ps.setStudentCode(studentCode);
                    ps.setStudentName(studentName);
                    ps.setClassName(className);
                    ps.setStudentDob(studentDob);
                    ps.setGender(gender);
                    ps.setParentCode(parentCode);
                    ps.setParentName(parentName);
                    ps.setParentEmail(parentEmail);
                    ps.setParentPhone(parentPhone);
                    ps.setParentDob(parentDob);
                    ps.setParentAddress(parentAddress);
                    ps.setRelationship(relationship);
                    ps.setStatus(status != null ? status : "PENDING");

                    parentStudentRepository.save(ps);

                    System.out.printf("✅ Dòng %d: Đã import học sinh %s (%s)%n", rowIndex, studentName, studentCode);

                } catch (Exception e) {
                    System.err.printf("Lỗi tại dòng %d: %s%n", rowIndex, e.getMessage());
                }
            }

        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
    }


    private String getString(Cell cell) {
        if (cell == null) return null;
        cell.setCellType(CellType.STRING);
        return cell.getStringCellValue().trim();
    }

    private LocalDate getDate(Cell cell) {
        if (cell == null || cell.getCellType() == CellType.BLANK) return null;
        if (cell.getCellType() == CellType.NUMERIC) {
            return cell.getLocalDateTimeCellValue().toLocalDate();
        } else {
            try {
                return LocalDate.parse(cell.getStringCellValue().trim(), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            } catch (Exception e) {
                return null;
            }
        }
    }

    private String generateNextStudentCode() {
        String latest = parentStudentRepository.findLatestStudentCode(); // e.g., "HS0020"
        int next = 1;
        if (latest != null && latest.startsWith("HS")) {
            try {
                next = Integer.parseInt(latest.substring(2)) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("HS%05d", next);
    }

    private String generateNextParentCode() {
        String latest = parentStudentRepository.findLatestParentCode(); // e.g., "PH0008"
        int next = 1;
        if (latest != null && latest.startsWith("PH")) {
            try {
                next = Integer.parseInt(latest.substring(2)) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("PH%06d", next);
    }


}

