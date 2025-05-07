package com.palangwi.soup.dto.keyword;

import com.palangwi.soup.domain.keyword.Keyword;

public record KeywordResponseDto(
        Long id,
        String name,
        String normalizedName) {

    public static KeywordResponseDto from(Keyword keyword) {
        return new KeywordResponseDto(keyword.getId(), keyword.getName(), keyword.getNormalizedName());
    }
}