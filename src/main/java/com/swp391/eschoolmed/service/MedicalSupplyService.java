package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.CreateMedicalSupplyRequest;
import com.swp391.eschoolmed.dto.response.MedicalSupplyResponse;
import com.swp391.eschoolmed.model.MedicalSupply;
import com.swp391.eschoolmed.repository.MedicalSupplyRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MedicalSupplyService {

    @Autowired private MedicalSupplyRepository supplyRepo;
    @Autowired private ModelMapper modelMapper;

    public MedicalSupplyResponse create(CreateMedicalSupplyRequest request) {
        MedicalSupply supply = modelMapper.map(request, MedicalSupply.class);
        return modelMapper.map(supplyRepo.save(supply), MedicalSupplyResponse.class);
    }

    public List<MedicalSupplyResponse> getAll() {
        return supplyRepo.findAll()
                .stream()
                .map(s -> modelMapper.map(s, MedicalSupplyResponse.class))
                .collect(Collectors.toList());
    }

    public void delete(UUID id) {
        supplyRepo.deleteById(id);
    }
}
