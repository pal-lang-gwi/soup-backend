package com.palangwi.soup.service.keyword;

import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.dto.keyword.RegisterKeywordRequestDto;
import com.palangwi.soup.dto.keyword.response.RegisterKeywordResponseDto;

public interface KeywordService {

    KeywordResponseDto getKeywordByName(String name);

    KeywordResponseDto createKeyword(String name);

    KeywordResponseDto updateKeyword(Long id, String name);

    RegisterKeywordResponseDto registerKeyword(Long userId, RegisterKeywordRequestDto registerKeywordRequestDto);

    void deleteKeyword(Long id);
}