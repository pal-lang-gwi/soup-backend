package com.palangwi.soup.controller.keyword;

import static com.palangwi.soup.utils.ApiUtils.success;

import com.palangwi.soup.dto.keyword.SubscribeKeywordRequestDto;
import com.palangwi.soup.dto.keyword.response.SubscribeKeywordResponseDto;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.security.JwtAuthentication;
import com.palangwi.soup.service.keyword.KeywordService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;

import lombok.RequiredArgsConstructor;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = "Keyword API", description = "키워드 관련 API")
@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @Operation(summary = "키워드 목록 조회", description = "키워드 목록을 조회합니다.")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping("/api/v1/keywords")
    public ApiResult<KeywordResponseDto> getKeywords() {
        return success(null);
    }

    @Operation(summary = "키워드 등록", description = "새로운 키워드를 등록합니다.")
    @ApiResponse(responseCode = "200", description = "등록 성공")
    @PostMapping("/api/v1/keywords")
    public ApiResult<SubscribeKeywordResponseDto> registerKeyword(
            @AuthenticationPrincipal JwtAuthentication userDetails,
            @Valid @RequestBody SubscribeKeywordRequestDto subscribeKeywordRequestDto) {
        return success(keywordService.subscribeKeywords(userDetails.id(), subscribeKeywordRequestDto));
    }
}