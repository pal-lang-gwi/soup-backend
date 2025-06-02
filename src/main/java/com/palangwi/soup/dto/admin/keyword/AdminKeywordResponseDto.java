package com.palangwi.soup.dto.admin.keyword;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.keyword.Status;
import java.time.LocalDateTime;

public record AdminKeywordResponseDto(Long requestId, Keyword keyword, String requestedEmail, Status status, LocalDateTime requestedAt, String rejectionReason) {
    public static AdminKeywordResponseDto from(PendingKeywordRequest request) {
        return new AdminKeywordResponseDto(
                request.getId(),
                request.getKeyword(),
                request.getUser().getEmail(),
                request.getKeyword().getStatus(),
                request.getCreatedDate(),
                request.getKeyword().getRejectReason()
        );
    }
}
