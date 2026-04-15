package com.example.rentflat.service;

import com.example.rentflat.dto.request.UpdateKycStatusRequestDTO;
import com.example.rentflat.dto.request.UpdateProfileRequestDTO;
import com.example.rentflat.dto.request.UpdateUserStatusRequestDTO;
import com.example.rentflat.dto.response.PageResponseDTO;
import com.example.rentflat.dto.response.UserProfileDTO;
import com.example.rentflat.entity.User;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.UserRepository;
import com.example.rentflat.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserProfileDTO getMyProfile() {
        String phone = SecurityUtils.getCurrentPhone();
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return UserProfileDTO.from(user);
    }

    @Transactional
    public UserProfileDTO updateMyProfile(UpdateProfileRequestDTO req) {
        String phone = SecurityUtils.getCurrentPhone();
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (req.getFullName() != null) user.setFullName(req.getFullName());
        if (req.getEmail() != null) user.setEmail(req.getEmail());
        if (req.getOccupation() != null) user.setOccupation(req.getOccupation());
        if (req.getIncomeRange() != null) user.setIncomeRange(req.getIncomeRange());
        if (req.getFcmToken() != null) user.setFcmToken(req.getFcmToken());

        return UserProfileDTO.from(userRepository.save(user));
    }

    public UserProfileDTO getUserProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        return UserProfileDTO.from(user);
    }

    public PageResponseDTO<UserProfileDTO> listUsers(Pageable pageable) {
        return PageResponseDTO.from(userRepository.findAll(pageable).map(UserProfileDTO::from));
    }

    @Transactional
    public UserProfileDTO updateUserStatus(UUID userId, UpdateUserStatusRequestDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        user.setActive(req.getActive());
        return UserProfileDTO.from(userRepository.save(user));
    }

    @Transactional
    public UserProfileDTO updateKycStatus(UUID userId, UpdateKycStatusRequestDTO req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
        user.setKycStatus(req.getKycStatus());
        user.setKycRejectionReason(req.getRejectionReason());
        return UserProfileDTO.from(userRepository.save(user));
    }

    public User getCurrentUser() {
        String phone = SecurityUtils.getCurrentPhone();
        return userRepository.findByPhone(phone)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));
    }
}
