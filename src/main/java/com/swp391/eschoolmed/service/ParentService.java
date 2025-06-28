package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.ParentProfileRequest;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    public void updateParentProfile(ParentProfileRequest request) {
        Parent parent = parentRepository.findById(request.getParentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        parent.setFullName(request.getFullName());
        parent.setPhoneNumber(request.getPhoneNumber());
        parent.setAddress(request.getAddress());
        parent.setDateOfBirth(request.getDateOfBirth());

        parentRepository.save(parent);
    }
}
