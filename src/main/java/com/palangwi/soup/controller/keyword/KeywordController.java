package com.palangwi.soup.controller.keyword;

import static com.palangwi.soup.utils.ApiUtils.success;

import com.palangwi.soup.dto.keyword.KeywordResponseDto;
import com.palangwi.soup.service.keyword.KeywordService;
import com.palangwi.soup.utils.ApiUtils.ApiResult;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class KeywordController {

    private final KeywordService keywordService;

    @GetMapping("/api/v1/keywords")
    public ApiResult<KeywordResponseDto> getKeywords() {
        return success(keywordService.getKeywords());
    }
}