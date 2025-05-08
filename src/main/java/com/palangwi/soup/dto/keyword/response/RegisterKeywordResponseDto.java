package com.palangwi.soup.dto.keyword.response;

import java.util.List;

public record RegisterKeywordResponseDto(List<String> registeredKeywords) {
    public static RegisterKeywordResponseDto of(List<String> registeredKeywords) {
        return new RegisterKeywordResponseDto(registeredKeywords);
    }
}
