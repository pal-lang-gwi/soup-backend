package com.palangwi.soup.service.keyword;

import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.dto.keyword.SubscribeKeywordRequestDto;
import com.palangwi.soup.dto.keyword.response.SubscribeKeywordResponseDto;

public interface KeywordService {

    KeywordResponseDto findKeywordByName(String name);

    KeywordResponseDto addKeyword(String name);

    KeywordResponseDto updateKeywordName(Long id, String name);

    SubscribeKeywordResponseDto subscribeKeywords(Long userId, SubscribeKeywordRequestDto subscribeKeywordRequestDto);

    void deleteKeyword(Long id);
}