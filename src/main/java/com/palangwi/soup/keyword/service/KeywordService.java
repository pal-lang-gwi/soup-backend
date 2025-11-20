package com.palangwi.soup.keyword.service;

import com.palangwi.soup.keyword.dto.*;
import com.palangwi.soup.keyword.dto.response.KeywordUnsubscribeResponseDto;
import com.palangwi.soup.keyword.dto.response.RequestKeywordResponseDto;
import com.palangwi.soup.keyword.dto.response.SearchKeywordsResponseDto;
import com.palangwi.soup.keyword.dto.response.SubscribeKeywordResponseDto;
import org.springframework.data.domain.Pageable;

public interface KeywordService {

    KeywordResponseDto findKeywordByName(String name);

    KeywordResponseDto addKeyword(String name);

    KeywordResponseDto updateKeywordName(Long id, String name);

    SubscribeKeywordResponseDto subscribeKeyword(Long userId, SubscribeKeywordRequestDto subscribeKeywordRequestDto);

    void deleteKeyword(Long id);

    RequestKeywordResponseDto requestKeywords(Long userId, RequestKeywordRequestDto requestKeywordRequestDto);

    KeywordUnsubscribeResponseDto unsubscribeKeyword(Long subscriptionId);

    MyKeywordListResponseDto getMyKeywords(Long userId, Pageable pageable);

    SearchKeywordsResponseDto searchKeywords(Long userId, String keyword, Pageable pageable);

    KeywordListResponseDto getKeywordList(Pageable pageable);
}