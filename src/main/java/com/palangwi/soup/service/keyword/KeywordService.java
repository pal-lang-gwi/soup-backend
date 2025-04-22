package com.palangwi.soup.service.keyword;

import com.palangwi.soup.dto.keyword.KeywordResponseDto;

public interface KeywordService {

    KeywordResponseDto getKeywords();

    KeywordResponseDto getKeywordByName(String name);

    KeywordResponseDto createKeyword(String name);

    KeywordResponseDto updateKeyword(Long id, String name);

    void deleteKeyword(Long id);
}