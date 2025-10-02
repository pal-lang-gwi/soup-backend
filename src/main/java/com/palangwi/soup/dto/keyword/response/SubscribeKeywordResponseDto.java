package com.palangwi.soup.dto.keyword.response;

public record SubscribeKeywordResponseDto(Long keywordId, String keywordName) {
    public static SubscribeKeywordResponseDto of(Long keywordId, String keywordName) {
        return new SubscribeKeywordResponseDto(keywordId, keywordName);
    }
}
