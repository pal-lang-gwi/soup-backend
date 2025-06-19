package com.palangwi.soup.dto.keyword.response;

import java.util.List;

public record SearchKeywordsResponseDto(
        List<SearchKeywordDto> keywords,
        long totalElements,
        int totalPages,
        int currentPages) {

    public static SearchKeywordsResponseDto from(List<SearchKeywordDto> keywords, long totalElements, int totalPages,
            int currentPages) {
        return new SearchKeywordsResponseDto(keywords, totalElements, totalPages, currentPages);
    }
}
