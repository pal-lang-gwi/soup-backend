package com.palangwi.soup.dto.keyword;

import java.util.List;

public record KeywordListResponseDto(
        List<KeywordResponseDto> keywordResponseDtos,
        long totalElements,
        int totalPages,
        int currentPage
) {
}
