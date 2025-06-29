package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.StudentProfileRequest;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.repository.StudentRepository;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Date;
import java.util.UUID;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public void importExcel(MultipartFile file) {
        try (Workbook workbook = new XSSFWorkbook(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null)
                    continue;

                Student student = new Student();
                student.setStudentId(UUID.randomUUID());
                student.setFullName(row.getCell(0).getStringCellValue());
                student.setClass_id(UUID.fromString(row.getCell(1).getStringCellValue()));

                Date dob = row.getCell(2).getDateCellValue();
                student.setDate_of_birth(dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());

                student.setGender(row.getCell(3).getStringCellValue());

                studentRepository.save(student);
            }
        } catch (IOException e) {
            throw new RuntimeException("Lỗi đọc file Excel: " + e.getMessage(), e);
        }
    }

    public void studentProfile(StudentProfileRequest request) {
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        student.setFullName(request.getFullName());
        student.setClass_id(request.getClass_id());
        student.setGender(request.getGender());
        student.setDate_of_birth(request.getDate_of_birth());

        studentRepository.save(student);


    }
}
