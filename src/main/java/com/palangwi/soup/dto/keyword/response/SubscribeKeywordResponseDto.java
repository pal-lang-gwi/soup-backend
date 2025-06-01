package com.palangwi.soup.dto.keyword.response;

import java.util.List;

public record SubscribeKeywordResponseDto(List<String> registeredKeywords) {
    public static SubscribeKeywordResponseDto of(List<String> registeredKeywords) {
        return new SubscribeKeywordResponseDto(registeredKeywords);
    }
}
