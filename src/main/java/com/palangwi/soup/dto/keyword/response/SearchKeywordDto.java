package com.palangwi.soup.dto.keyword.response;

import com.palangwi.soup.domain.keyword.Keyword;

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