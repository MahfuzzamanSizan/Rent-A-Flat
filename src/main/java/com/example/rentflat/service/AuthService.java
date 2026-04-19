package com.example.rentflat.service;

import com.example.rentflat.dto.response.AuthResponseDTO;
import com.example.rentflat.dto.request.RefreshTokenRequestDTO;
import com.example.rentflat.dto.request.SendOtpRequestDTO;
import com.example.rentflat.dto.request.VerifyOtpRequestDTO;
import com.example.rentflat.entity.User;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final OtpService otpService;
    private final JwtService jwtService;
    private final UserRepository userRepository;

    public AuthResponseDTO sendOtp(SendOtpRequestDTO req) {
        String otp = otpService.generateAndStore(req.getPhone());
        return AuthResponseDTO.builder()
                .phone(req.getPhone())
                .devOtp(otp)
                .build();
    }

    @Transactional
    public AuthResponseDTO verifyOtp(VerifyOtpRequestDTO req) {
        otpService.verify(req.getPhone(), req.getOtp());

        boolean isNew = !userRepository.existsByPhone(req.getPhone());

        if (isNew && (req.getFullName() == null || req.getFullName().isBlank())) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Full name is required for new users");
        }

        User user = isNew
                ? createUser(req)
                : userRepository.findByPhone(req.getPhone())
                        .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.isEnabled()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Account is suspended");
        }

        // Consume OTP only after everything succeeds
        otpService.consume(req.getPhone());

        String accessToken  = jwtService.generateAccessToken(user.getPhone(), user.getRole(), user.getId());
        String refreshToken = jwtService.generateRefreshToken(user.getId());

        return AuthResponseDTO.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .role(user.getRole())
                .newUser(isNew)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

    public AuthResponseDTO refreshToken(RefreshTokenRequestDTO req) {
        UUID userId = parseUuid(req.getUserId());

        if (!jwtService.isRefreshTokenValid(userId, req.getRefreshToken())) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

        if (!user.isEnabled()) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Account is suspended");
        }

        jwtService.deleteRefreshToken(userId);
        String newAccessToken  = jwtService.generateAccessToken(user.getPhone(), user.getRole(), user.getId());
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());

        return AuthResponseDTO.builder()
                .userId(user.getId())
                .phone(user.getPhone())
                .fullName(user.getFullName())
                .role(user.getRole())
                .accessToken(newAccessToken)
                .refreshToken(newRefreshToken)
                .build();
    }

    public void logout(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            String token = bearerToken.substring(7);
            String userIdStr = jwtService.extractUserId(token);
            jwtService.blacklistAccessToken(token);
            jwtService.deleteRefreshToken(UUID.fromString(userIdStr));
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────────

    private User createUser(VerifyOtpRequestDTO req) {
        return userRepository.save(User.builder()
                .phone(req.getPhone())
                .fullName(req.getFullName())
                .role(req.getRole())
                .build());
    }

    private UUID parseUuid(String id) {
        try {
            return UUID.fromString(id);
        } catch (IllegalArgumentException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Invalid user ID format");
        }
    }
}
