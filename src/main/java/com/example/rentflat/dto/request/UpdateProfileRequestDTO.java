package com.example.rentflat.dto.request;

import jakarta.validation.constraints.Email;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateProfileRequestDTO {
    private String fullName;
    @Email(message = "Invalid email address")
    private String email;
    private String occupation;
    private String incomeRange;
    private String fcmToken;
}
