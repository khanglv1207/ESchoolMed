package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.CreateVaccinationRecordRequest;
import com.swp391.eschoolmed.dto.response.VaccinationRecordResponse;
import com.swp391.eschoolmed.model.Student;
import com.swp391.eschoolmed.model.Vaccine;
import com.swp391.eschoolmed.model.VaccinationRecord;
import com.swp391.eschoolmed.repository.StudentRepository;
import com.swp391.eschoolmed.repository.VaccinationRecordRepository;
import com.swp391.eschoolmed.repository.VaccineRepository;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.UUID;
@Service
public class VaccinationService {

    private final ModelMapper modelMapper;
    private final VaccinationRecordRepository vaccinationRepository;
    private final StudentRepository studentRepository;
    private final VaccineRepository vaccineRepository;

    @Autowired
    public VaccinationService(
            ModelMapper modelMapper,
            VaccinationRecordRepository vaccinationRepository,
            StudentRepository studentRepository,
            VaccineRepository vaccineRepository) {
        this.modelMapper = modelMapper;
        this.vaccinationRepository = vaccinationRepository;
        this.studentRepository = studentRepository;
        this.vaccineRepository = vaccineRepository;
    }

    // Create vaccination record
    public VaccinationRecordResponse createRecord(CreateVaccinationRecordRequest request) {
        // Lấy thông tin học sinh
        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        // Lấy thông tin vaccine
        Vaccine vaccine = vaccineRepository.findById(request.getVaccineId())
                .orElseThrow(() -> new RuntimeException("Vaccine not found"));

        // Tạo entity
        VaccinationRecord record = new VaccinationRecord();
        record.setStudent(student);
        record.setVaccine(vaccine);
        record.setVaccinationDate(request.getVaccinationDate());
        record.setDose(request.getDose());
        record.setNote(request.getNote());

        // Lưu DB
        VaccinationRecord saved = vaccinationRepository.save(record);

        // Chuyển sang DTO để trả về
        return convertToResponse(saved);
    }

    // Convert Entity -> DTO
    private VaccinationRecordResponse convertToResponse(VaccinationRecord record) {
        VaccinationRecordResponse response = modelMapper.map(record, VaccinationRecordResponse.class);

        // Gán thêm các giá trị không tự động map
        response.setStudentId(record.getStudent().getStudentId());
        response.setStudentName(record.getStudent().getFullName());
        response.setVaccineId(record.getVaccine().getId());
        response.setVaccineName(record.getVaccine().getName());

        return response;
    }

    // Get all vaccination records
    public List<VaccinationRecordResponse> getAll() {
        return vaccinationRepository.findAll()
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Get by student
    public List<VaccinationRecordResponse> getByStudent(UUID studentId) {
        return vaccinationRepository.findByStudent_StudentId(studentId)
                .stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // Delete a record
    public void deleteRecord(Long id) {
        vaccinationRepository.deleteById(   id);
    }
}
