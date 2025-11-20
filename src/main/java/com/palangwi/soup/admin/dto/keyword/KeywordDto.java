package com.palangwi.soup.admin.dto.keyword;

import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.keyword.domain.Status;

import java.time.LocalDateTime;

public record KeywordDto(
        Long keywordId,
        String name,
        Status status,
        LocalDateTime requestedDate,
        String rejectionReason
) {
    public static KeywordDto from(Keyword keyword) {
        return new KeywordDto(
                keyword.getId(),
                keyword.getName(),
                keyword.getStatus(),
                keyword.getCreatedDate(),
                keyword.getRejectReason()
        );
    }
}
