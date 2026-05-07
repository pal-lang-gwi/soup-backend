package com.palangwi.soup.admin.dto.keyword;

import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.keyword.domain.Status;

public record AdminKeywordListItemDto(
        Long id,
        String name,
        String normalizedName,
        Status status
) {
    public static AdminKeywordListItemDto from(Keyword keyword) {
        return new AdminKeywordListItemDto(
                keyword.getId(),
                keyword.getName(),
                keyword.getNormalizedName(),
                keyword.getStatus()
        );
    }
}
