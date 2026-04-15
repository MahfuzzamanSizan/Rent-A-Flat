package com.example.rentflat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshTokenRequestDTO {

    @JsonProperty("userId")
    @NotBlank(message = "User ID is required")
    private String userId;

    @JsonProperty("refreshToken")
    @NotBlank(message = "Refresh token is required")
    private String refreshToken;
}
