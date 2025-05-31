package com.palangwi.soup.dto.admin.keyword;

import com.palangwi.soup.domain.keyword.Status;
import java.time.LocalDateTime;

public record AdminKeywordResponseDto(Long requestId, String keyword, String requestedEmail, Status status, LocalDateTime requestedAt, String rejectionReason) {
}
