package com.palangwi.soup.dto.keyword.response;

public record SubscribeKeywordResponseDto(Long keywordId, String registeredKeyword) {
    public static SubscribeKeywordResponseDto of(Long keywordId, String registeredKeyword) {
        return new SubscribeKeywordResponseDto(keywordId, registeredKeyword);
    }
}
