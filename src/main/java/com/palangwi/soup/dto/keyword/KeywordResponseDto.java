package com.palangwi.soup.dto.keyword;

public record KeywordResponseDto(
        Long id,
        String name,
        String normalizedName
) {
}
