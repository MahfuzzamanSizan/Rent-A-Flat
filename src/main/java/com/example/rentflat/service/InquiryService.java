package com.example.rentflat.service;

import com.example.rentflat.dto.request.CreateInquiryRequestDTO;
import com.example.rentflat.dto.request.RespondInquiryRequestDTO;
import com.example.rentflat.dto.response.InquiryDTO;
import com.example.rentflat.dto.response.PageResponseDTO;
import com.example.rentflat.entity.Inquiry;
import com.example.rentflat.entity.Property;
import com.example.rentflat.entity.User;
import com.example.rentflat.enums.InquiryStatus;
import com.example.rentflat.exception.ApiException;
import com.example.rentflat.repository.InquiryRepository;
import com.example.rentflat.repository.PropertyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final PropertyRepository propertyRepository;
    private final UserService userService;

    @Transactional
    public InquiryDTO createInquiry(CreateInquiryRequestDTO req) {
        User tenant = userService.getCurrentUser();
        Property property = propertyRepository.findByIdAndDeletedAtIsNull(req.getPropertyId())
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Property not found"));

        // Prevent duplicate inquiry
        inquiryRepository.findByTenantIdAndPropertyId(tenant.getId(), property.getId())
                .ifPresent(i -> { throw new ApiException(HttpStatus.CONFLICT, "Inquiry already sent"); });

        Inquiry inquiry = Inquiry.builder()
                .tenantId(tenant.getId())
                .propertyId(property.getId())
                .ownerId(property.getOwnerId())
                .message(req.getMessage())
                .build();

        propertyRepository.incrementInquiryCount(property.getId());
        return InquiryDTO.from(inquiryRepository.save(inquiry));
    }

    @Transactional
    public InquiryDTO acceptInquiry(UUID inquiryId, RespondInquiryRequestDTO req) {
        return respond(inquiryId, req, InquiryStatus.ACCEPTED);
    }

    @Transactional
    public InquiryDTO rejectInquiry(UUID inquiryId, RespondInquiryRequestDTO req) {
        return respond(inquiryId, req, InquiryStatus.REJECTED);
    }

    private InquiryDTO respond(UUID inquiryId, RespondInquiryRequestDTO req, InquiryStatus newStatus) {
        User owner = userService.getCurrentUser();
        Inquiry inquiry = inquiryRepository.findById(inquiryId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Inquiry not found"));
        if (!inquiry.getOwnerId().equals(owner.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Not your inquiry");
        }
        if (inquiry.getStatus() != InquiryStatus.PENDING) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Inquiry already responded");
        }
        inquiry.setStatus(newStatus);
        inquiry.setOwnerResponseMessage(req.getMessage());
        inquiry.setRespondedAt(OffsetDateTime.now());
        return InquiryDTO.from(inquiryRepository.save(inquiry));
    }

    public PageResponseDTO<InquiryDTO> getMyInquiriesAsOwner(Pageable pageable) {
        User owner = userService.getCurrentUser();
        return PageResponseDTO.from(
                inquiryRepository.findByOwnerIdAndDeletedAtIsNull(owner.getId(), pageable)
                        .map(InquiryDTO::from));
    }

    public PageResponseDTO<InquiryDTO> getMyInquiriesAsTenant(Pageable pageable) {
        User tenant = userService.getCurrentUser();
        return PageResponseDTO.from(
                inquiryRepository.findByTenantIdAndDeletedAtIsNull(tenant.getId(), pageable)
                        .map(InquiryDTO::from));
    }

    public InquiryDTO getInquiry(UUID id) {
        User user = userService.getCurrentUser();
        Inquiry inquiry = inquiryRepository.findById(id)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "Inquiry not found"));
        if (!inquiry.getOwnerId().equals(user.getId()) && !inquiry.getTenantId().equals(user.getId())) {
            throw new ApiException(HttpStatus.FORBIDDEN, "Access denied");
        }
        return InquiryDTO.from(inquiry);
    }
}
