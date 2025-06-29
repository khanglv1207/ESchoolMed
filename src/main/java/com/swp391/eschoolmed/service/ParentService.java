package com.swp391.eschoolmed.service;

import com.swp391.eschoolmed.dto.request.UpdateParentProfileRequest;
import com.swp391.eschoolmed.dto.response.ParentProfileResponse;
import com.swp391.eschoolmed.model.Parent;
import com.swp391.eschoolmed.model.User;
import com.swp391.eschoolmed.repository.ParentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ParentService {

    @Autowired
    private ParentRepository parentRepository;

    public void updateParentProfile(UpdateParentProfileRequest request) {
        Parent parent = parentRepository.findByUserId(request.getUserid())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy user"));
        parent.setFullName(request.getFullName());
        parent.setPhoneNumber(request.getPhoneNumber());
        parent.setAddress(request.getAddress());
        parent.setDateOfBirth(request.getDateOfBirth());

        parentRepository.save(parent);
    }

    public ParentProfileResponse getParentProfile(UUID userId) {
        Parent parent = parentRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hồ sơ phụ huynh"));

        User user = parent.getUser();

        ParentProfileResponse response = new ParentProfileResponse();
        response.setUserId(user.getId().toString());
        response.setFullName(parent.getFullName());
        response.setEmail(user.getEmail());
        response.setPhoneNumber(parent.getPhoneNumber());
        response.setAddress(parent.getAddress());
        response.setDateOfBirth(parent.getDateOfBirth());

        return response;
    }
}

