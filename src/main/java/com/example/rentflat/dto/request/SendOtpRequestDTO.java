package com.example.rentflat.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendOtpRequestDTO {

    @JsonProperty("phone")
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?880[0-9]{10}$|^01[0-9]{9}$",
             message = "Invalid Bangladeshi phone number")
    private String phone;
}
