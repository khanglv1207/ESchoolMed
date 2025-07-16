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

}
