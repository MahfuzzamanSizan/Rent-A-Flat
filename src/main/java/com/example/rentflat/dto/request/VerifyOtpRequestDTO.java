package com.example.rentflat.dto.request;

import com.example.rentflat.enums.UserRole;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VerifyOtpRequestDTO {

    @JsonProperty("phone")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?880[0-9]{10}$|^01[0-9]{9}$",
             message = "Invalid Bangladeshi phone number")
    private String phone;

    @JsonProperty("otp")
    @NotBlank(message = "OTP is required")
    private String otp;

    // Required only for new users; ignored if user already exists
    @JsonProperty("fullName")
    private String fullName;

    @JsonProperty("role")
    @NotNull(message = "Role is required")
    private UserRole role;
}
