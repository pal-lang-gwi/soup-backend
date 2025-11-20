package com.palangwi.soup.keyword.dto.response;

import com.palangwi.soup.keyword.domain.Keyword;

public record SearchKeywordDto(
        Long id,
        String name,
        String normalizedName,
        boolean isSubscribed) {

    public static SearchKeywordDto from(Keyword keyword, boolean isSubscribed) {
        return new SearchKeywordDto(
                keyword.getId(),
                keyword.getName(),
                keyword.getNormalizedName(),
                isSubscribed);
    }
}