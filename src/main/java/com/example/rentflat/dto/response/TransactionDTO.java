package com.example.rentflat.dto.response;

import com.example.rentflat.entity.Transaction;
import com.example.rentflat.enums.PaymentGateway;
import com.example.rentflat.enums.TransactionStatus;
import com.example.rentflat.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data @Builder
public class TransactionDTO {
    private UUID id;
    private UUID userId;
    private TransactionType type;
    private BigDecimal amount;
    private String currency;
    private PaymentGateway gateway;
    private String gatewayReference;
    private TransactionStatus status;
    private UUID parentTransactionId;
    private String metadata;
    private String description;
    private OffsetDateTime createdAt;

    public static TransactionDTO from(Transaction t) {
        return TransactionDTO.builder()
                .id(t.getId())
                .userId(t.getUserId())
                .type(t.getType())
                .amount(t.getAmount())
                .currency(t.getCurrency())
                .gateway(t.getGateway())
                .gatewayReference(t.getGatewayReference())
                .status(t.getStatus())
                .parentTransactionId(t.getParentTransactionId())
                .metadata(t.getMetadata())
                .description(t.getDescription())
                .createdAt(t.getCreatedAt())
                .build();
    }
}
