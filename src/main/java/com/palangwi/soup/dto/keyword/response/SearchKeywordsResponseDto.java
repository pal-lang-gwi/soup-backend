package com.palangwi.soup.dto.keyword.response;

import java.util.List;

public record SearchKeywordsResponseDto(
        List<SearchKeywordDto> keywords,
        long totalElements,
        int totalPages,
        int currentPage) {

    public static SearchKeywordsResponseDto from(List<SearchKeywordDto> keywords, long totalElements, int totalPage,
            int currentPage) {
        return new SearchKeywordsResponseDto(keywords, totalElements, totalPage, currentPage);
    }
}
