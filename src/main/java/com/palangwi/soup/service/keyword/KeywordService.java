package com.palangwi.soup.service.keyword;

import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.dto.keyword.MyKeywordListResponseDto;
import com.palangwi.soup.dto.keyword.RequestKeywordRequestDto;
import com.palangwi.soup.dto.keyword.SubscribeKeywordRequestDto;
import com.palangwi.soup.dto.keyword.response.KeywordUnsubscribeResponseDto;
import com.palangwi.soup.dto.keyword.response.RequestKeywordResponseDto;
import com.palangwi.soup.dto.keyword.response.SearchKeywordsResponseDto;
import com.palangwi.soup.dto.keyword.response.SubscribeKeywordResponseDto;
import org.springframework.data.domain.Pageable;

public interface KeywordService {

    KeywordResponseDto findKeywordByName(String name);

    KeywordResponseDto addKeyword(String name);

    KeywordResponseDto updateKeywordName(Long id, String name);

    SubscribeKeywordResponseDto subscribeKeywords(Long userId, SubscribeKeywordRequestDto subscribeKeywordRequestDto);

    void deleteKeyword(Long id);

    RequestKeywordResponseDto requestKeywords(Long userId, RequestKeywordRequestDto requestKeywordRequestDto);

    KeywordUnsubscribeResponseDto unsubscribeKeyword(Long id, Long keywordId);

    MyKeywordListResponseDto getMyKeywords(Long userId, Pageable pageable);

    SearchKeywordsResponseDto searchKeywords(Long userId, String keyword);

}