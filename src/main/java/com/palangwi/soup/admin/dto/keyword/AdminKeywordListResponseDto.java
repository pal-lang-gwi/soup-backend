package com.palangwi.soup.admin.dto.keyword;

import java.util.List;

public record AdminKeywordListResponseDto(
        List<AdminKeywordListItemDto> keywordResponseDtos,
        long totalElements,
        int totalPages,
        int currentPage
) {
}
