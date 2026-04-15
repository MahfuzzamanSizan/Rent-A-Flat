package com.example.rentflat.dto.request;

import com.example.rentflat.enums.ComplaintStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data @NoArgsConstructor @AllArgsConstructor
public class UpdateComplaintStatusRequestDTO {
    @NotNull(message = "Status is required")
    private ComplaintStatus status;
    private String adminNotes;
}
