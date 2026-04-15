package com.example.rentflat.dto.response;

import com.example.rentflat.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponseDTO {

    @JsonProperty("userId")
    private UUID userId;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("role")
    private UserRole role;

    @JsonProperty("newUser")
    private boolean newUser;

    @JsonProperty("accessToken")
    private String accessToken;

    @JsonProperty("refreshToken")
    private String refreshToken;

    // Dev-only: remove before production
    @JsonProperty("devOtp")
    private String devOtp;
}
