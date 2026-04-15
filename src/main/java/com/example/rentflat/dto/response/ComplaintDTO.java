package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Complaint;
import com.example.rentflat.enums.ComplaintStatus;
import com.example.rentflat.enums.ComplaintType;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder
public class ComplaintDTO {
    private UUID id;
    private UUID filedById;
    private UUID againstId;
    private UUID propertyId;
    private ComplaintType complaintType;
    private String description;
    private ComplaintStatus status;
    private String adminNotes;
    private UUID resolvedById;
    private OffsetDateTime resolvedAt;
    private List<String> evidenceUrls;
    private OffsetDateTime createdAt;

    public static ComplaintDTO from(Complaint c) {
        return ComplaintDTO.builder()
                .id(c.getId())
                .filedById(c.getFiledById())
                .againstId(c.getAgainstId())
                .propertyId(c.getPropertyId())
                .complaintType(c.getComplaintType())
                .description(c.getDescription())
                .status(c.getStatus())
                .adminNotes(c.getAdminNotes())
                .resolvedById(c.getResolvedById())
                .resolvedAt(c.getResolvedAt())
                .evidenceUrls(c.getEvidenceUrls())
                .createdAt(c.getCreatedAt())
                .build();
    }
}
