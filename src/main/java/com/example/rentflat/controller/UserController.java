package com.example.rentflat.controller;

import com.example.rentflat.dto.request.UpdateProfileRequestDTO;
import com.example.rentflat.dto.response.UserProfileDTO;
import com.example.rentflat.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileDTO> getMyProfile() {
        return ResponseEntity.ok(userService.getMyProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileDTO> updateMyProfile(@Valid @RequestBody UpdateProfileRequestDTO req) {
        return ResponseEntity.ok(userService.updateMyProfile(req));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfileDTO> getUserProfile(@PathVariable UUID userId) {
        return ResponseEntity.ok(userService.getUserProfile(userId));
    }
}
