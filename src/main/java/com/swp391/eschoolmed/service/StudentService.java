package com.swp391.eschoolmed.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.UUID;

import com.swp391.eschoolmed.model.ClassEntity;
import com.swp391.eschoolmed.repository.ClassRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.swp391.eschoolmed.dto.request.ParentStudentUpdateRequest;
import com.swp391.eschoolmed.dto.request.StudentProfileRequest;
import com.swp391.eschoolmed.dto.response.StudentProfileResponse;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.ParentStudent;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.repository.ParentRepository;
import com.swp391.eschoolmed.repository.ParentStudentRepository;
import com.swp391.eschoolmed.repository.StudentRepository;
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

    @Autowired
    private ClassRepository classRepository;




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
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy học sinh với id: " + request.getStudentId()));
        record.setStudent(student);
        parentStudentRepository.save(record);
    }

    public void importParentStudentFromExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            if (rowIterator.hasNext()) rowIterator.next(); // Bỏ qua dòng tiêu đề

            int rowIndex = 1;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                rowIndex++;

                try {
                    // Đọc và gán các giá trị từ file
                    String tempStudentCode = getString(row.getCell(0));
                    if (tempStudentCode == null || tempStudentCode.isBlank()) {
                        tempStudentCode = generateNextStudentCode();
                    }
                    final String studentCode = tempStudentCode;

                    final String studentName = getString(row.getCell(1));
                    final String className = getString(row.getCell(2));
                    final LocalDate studentDob = getDate(row.getCell(3));
                    final String gender = getString(row.getCell(4));
                    final String parentCode = getString(row.getCell(5));
                    final String parentName = getString(row.getCell(6));
                    final String parentEmail = getString(row.getCell(7));
                    final String parentPhone = getString(row.getCell(8));
                    final LocalDate parentDob = getDate(row.getCell(9));
                    final String parentAddress = getString(row.getCell(10));
                    final String relationship = getString(row.getCell(11));
                    final String status = getString(row.getCell(12));

                    // Tạo hoặc lấy class_id
                    UUID classId = classRepository.findByClassName(className)
                            .map(ClassEntity::getClassId)
                            .orElseGet(() -> {
                                ClassEntity newClass = new ClassEntity();
                                newClass.setClassId(UUID.randomUUID());
                                newClass.setClassName(className);
                                return classRepository.save(newClass).getClassId();
                            });

                    // Tạo hoặc lấy student
                    Student student = studentRepository.findByStudentCode(studentCode)
                            .orElseGet(() -> {
                                Student s = new Student();
                                s.setStudentId(UUID.randomUUID());
                                s.setStudentCode(studentCode);
                                s.setFullName(studentName);
                                s.setDate_of_birth(studentDob);
                                s.setGender(gender);
                                s.setClass_id(classId);
                                return studentRepository.save(s);
                            });

                    // Tạo hoặc lấy parent (chưa sinh user tại đây)
                    final String finalParentCode = (parentCode == null || parentCode.isBlank())
                            ? generateNextParentCode()
                            : parentCode;

                    Parent parent = parentRepository.findByEmail(parentEmail)
                            .orElseGet(() -> {
                                Parent p = new Parent();
                                p.setParentId(null); // Không sinh ID vì sẽ gán khi đổi mật khẩu lần đầu
                                p.setEmail(parentEmail);
                                p.setFullName(parentName);
                                p.setPhoneNumber(parentPhone);
                                p.setAddress(parentAddress);
                                p.setDateOfBirth(parentDob != null ? parentDob.toString() : null);
                                p.setCode(finalParentCode);
                                return parentRepository.save(p);
                            });

                    // Tạo bản ghi ParentStudent
                    ParentStudent ps = new ParentStudent();
                    ps.setStudent(student);
                    ps.setStudentCode(studentCode);
                    ps.setStudentName(studentName);
                    ps.setClassName(className);
                    ps.setStudentDob(studentDob);
                    ps.setGender(gender);

                    ps.setParent(parent);
                    ps.setParentCode(finalParentCode);
                    ps.setParentName(parentName);
                    ps.setParentEmail(parentEmail);
                    ps.setParentPhone(parentPhone);
                    ps.setParentDob(parentDob);
                    ps.setParentAddress(parentAddress);
                    ps.setRelationship(relationship);
                    ps.setStatus(status != null ? status : "PENDING");

                    parentStudentRepository.save(ps);
                    System.out.printf("Dòng %d: Đã import học sinh %s (%s)%n", rowIndex, studentName, studentCode);

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


    public StudentProfileResponse toStudentProfileResponse(Student student) {
        return StudentProfileResponse.builder()
                .studentId(student.getStudentId())
                .fullName(student.getFullName())
                .class_id(student.getClass_id())
                .date_of_birth(student.getDate_of_birth())
                .gender(student.getGender())
                .build();
    }



}

