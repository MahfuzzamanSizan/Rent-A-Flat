package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Inquiry;
import com.example.rentflat.enums.InquiryStatus;
import lombok.Builder;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder
public class InquiryDTO {
    private UUID id;
    private UUID tenantId;
    private UUID propertyId;
    private UUID ownerId;
    private String message;
    private InquiryStatus status;
    private String ownerResponseMessage;
    private OffsetDateTime respondedAt;
    private String chatThreadId;
    private OffsetDateTime createdAt;

    public static InquiryDTO from(Inquiry i) {
        return InquiryDTO.builder()
                .id(i.getId())
                .tenantId(i.getTenantId())
                .propertyId(i.getPropertyId())
                .ownerId(i.getOwnerId())
                .message(i.getMessage())
                .status(i.getStatus())
                .ownerResponseMessage(i.getOwnerResponseMessage())
                .respondedAt(i.getRespondedAt())
                .chatThreadId(i.getChatThreadId())
                .createdAt(i.getCreatedAt())
                .build();
    }
}
