package com.example.rentflat.dto.response;

import com.example.rentflat.entity.RentPayment;
import com.example.rentflat.enums.PaymentMethod;
import com.example.rentflat.enums.RentPaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder
public class RentPaymentDTO {
    private UUID id;
    private UUID leaseId;
    private BigDecimal amount;
    private LocalDate dueDate;
    private OffsetDateTime paidAt;
    private PaymentMethod paymentMethod;
    private String transactionReference;
    private RentPaymentStatus status;
    private String receiptUrl;
    private String notes;
    private OffsetDateTime createdAt;

    public static RentPaymentDTO from(RentPayment r) {
        return RentPaymentDTO.builder()
                .id(r.getId())
                .leaseId(r.getLeaseId())
                .amount(r.getAmount())
                .dueDate(r.getDueDate())
                .paidAt(r.getPaidAt())
                .paymentMethod(r.getPaymentMethod())
                .transactionReference(r.getTransactionReference())
                .status(r.getStatus())
                .receiptUrl(r.getReceiptUrl())
                .notes(r.getNotes())
                .createdAt(r.getCreatedAt())
                .build();
    }
}
