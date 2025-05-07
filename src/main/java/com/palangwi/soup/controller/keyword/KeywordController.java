package com.palangwi.soup.controller.keyword;

import static com.palangwi.soup.utils.ApiUtils.success;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.dto.keyword.RegisterKeywordRequestDto;
import com.palangwi.soup.dto.keyword.response.RegisterKeywordResponseDto;
import com.palangwi.soup.security.JwtAuthentication;
import com.palangwi.soup.service.keyword.KeywordService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("/api/v1/keywords")
    public ApiResult<KeywordResponseDto> getKeywords() {
        return success(null);
    }

    @PostMapping("/api/v1/keywords")
    public ApiResult<RegisterKeywordResponseDto> registerKeyword(
            @AuthenticationPrincipal JwtAuthentication userDetails,
            @RequestBody RegisterKeywordRequestDto registerKeywordRequestDto) {
        return success(keywordService.registerKeyword(userDetails.id(), registerKeywordRequestDto));
    }
}