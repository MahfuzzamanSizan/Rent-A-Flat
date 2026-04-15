package com.example.rentflat.controller;

import com.example.rentflat.dto.response.AuthResponseDTO;
import com.example.rentflat.dto.request.RefreshTokenRequestDTO;
import com.example.rentflat.dto.request.SendOtpRequestDTO;
import com.example.rentflat.dto.request.VerifyOtpRequestDTO;
import com.example.rentflat.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/send-otp")
    public ResponseEntity<AuthResponseDTO> sendOtp(@Valid @RequestBody SendOtpRequestDTO req) {
        return ResponseEntity.ok(authService.sendOtp(req));
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<AuthResponseDTO> verifyOtp(@Valid @RequestBody VerifyOtpRequestDTO req) {
        return ResponseEntity.ok(authService.verifyOtp(req));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO req) {
        return ResponseEntity.ok(authService.refreshToken(req));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @RequestHeader(value = "Authorization", required = false) String authorization) {
        authService.logout(authorization);
        return ResponseEntity.noContent().build();
    }
}
