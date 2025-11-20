package com.palangwi.soup.keyword.dto;

import com.palangwi.soup.keyword.domain.Keyword;

public record KeywordResponseDto(
        Long id,
        String name,
        String normalizedName) {

    public static KeywordResponseDto from(Keyword keyword) {
        return new KeywordResponseDto(keyword.getId(), keyword.getName(), keyword.getNormalizedName());
    }
}