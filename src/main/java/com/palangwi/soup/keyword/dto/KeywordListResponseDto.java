package com.palangwi.soup.keyword.dto;

import java.util.List;

public record KeywordListResponseDto(
        List<KeywordResponseDto> keywordResponseDtos,
        long totalElements,
        int totalPages,
        int currentPage
) {
}
