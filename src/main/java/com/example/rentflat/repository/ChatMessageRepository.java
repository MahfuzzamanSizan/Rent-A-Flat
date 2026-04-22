package com.example.rentflat.repository;

import com.example.rentflat.entity.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, UUID> {

    List<ChatMessage> findByInquiryIdOrderBySentAtAsc(UUID inquiryId);

    @Modifying
    @Query("UPDATE ChatMessage m SET m.read = true WHERE m.inquiryId = :inquiryId AND m.senderId != :readerId")
    void markAllReadForInquiry(@Param("inquiryId") UUID inquiryId, @Param("readerId") UUID readerId);

    long countByInquiryIdAndReadFalseAndSenderIdNot(UUID inquiryId, UUID senderId);
}
