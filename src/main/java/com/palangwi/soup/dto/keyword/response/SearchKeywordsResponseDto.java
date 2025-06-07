package com.palangwi.soup.dto.keyword.response;

import java.util.List;


public record SearchKeywordsResponseDto(
        List<SearchKeywordDto> keywords) {

    public static SearchKeywordsResponseDto from(List<SearchKeywordDto> keywords) {
        return new SearchKeywordsResponseDto(keywords);
    }
}
