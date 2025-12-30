package com.palangwi.soup.keyword.controller;

import com.palangwi.soup.common.security.JwtAuthentication;
import com.palangwi.soup.common.utils.ApiUtils.ApiResult;
import com.palangwi.soup.keyword.dto.KeywordListResponseDto;
import com.palangwi.soup.keyword.dto.RequestKeywordRequestDto;
import com.palangwi.soup.keyword.dto.SubscribeKeywordRequestDto;
import com.palangwi.soup.keyword.dto.response.KeywordUnsubscribeResponseDto;
import com.palangwi.soup.keyword.dto.response.RequestKeywordResponseDto;
import com.palangwi.soup.keyword.dto.response.SearchKeywordsResponseDto;
import com.palangwi.soup.keyword.dto.response.SubscribeKeywordResponseDto;
import com.palangwi.soup.keyword.service.KeywordService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static com.palangwi.soup.common.utils.ApiUtils.success;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/keywords")
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping
    public ApiResult<KeywordListResponseDto> getKeywordList(
            @AuthenticationPrincipal JwtAuthentication userDetails,
            @PageableDefault(size = 20, sort = { "createdDate",
                    "name" }, direction = Direction.DESC) Pageable pageable) {
        return success(keywordService.getKeywordList(pageable));
    }

    @GetMapping("/search")
    public ApiResult<SearchKeywordsResponseDto> searchKeywords(
            @AuthenticationPrincipal JwtAuthentication userDetails,
            @Valid @RequestParam(name = "keyword") @NotBlank @Size(min = 1, max = 100) String keyword,
            @PageableDefault(size = 20, direction = Direction.DESC) Pageable pageable) {
        return success(keywordService.searchKeywords(userDetails.id(), keyword, pageable));
    }

    @PostMapping("/subscriptions")
    public ApiResult<SubscribeKeywordResponseDto> subscribeKeyword(
            @AuthenticationPrincipal JwtAuthentication userDetails,
            @Valid @RequestBody SubscribeKeywordRequestDto subscribeKeywordRequestDto) {
        return success(keywordService.subscribeKeyword(userDetails.id(), subscribeKeywordRequestDto));
    }

    @PostMapping("/subscriptions/{subscriptionId}")
    public ApiResult<KeywordUnsubscribeResponseDto> unsubscribeKeyword(
            @AuthenticationPrincipal JwtAuthentication userDetails,
            @PathVariable(name = "subscriptionId") Long subscriptionId) {
        return success(keywordService.unsubscribeKeyword(subscriptionId));
    }

    @PostMapping("/request")
    public ApiResult<RequestKeywordResponseDto> requestKeywords(
            @AuthenticationPrincipal JwtAuthentication userDetails,
            @Valid @RequestBody RequestKeywordRequestDto requestKeywordRequestDto) {
        return success(keywordService.requestKeywords(userDetails.id(), requestKeywordRequestDto));
    }
}