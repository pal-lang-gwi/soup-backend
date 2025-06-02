package com.palangwi.soup.controller;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.palangwi.soup.dto.keyword.SubscribeKeywordRequestDto;
import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.dto.keyword.response.SubscribeKeywordResponseDto;
import com.palangwi.soup.service.keyword.KeywordService;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.palangwi.soup.exception.keyword.AlreadySubscribedKeywordException;
import com.palangwi.soup.security.WithMockJwtAuthentication;

class KeywordControllerTest extends IntegrationTestSupport {

    @MockitoBean
    private KeywordService keywordService;

    @Test
    @DisplayName("키워드 등록에 성공한다.")
    @WithMockJwtAuthentication(id = 1L)
    void registerKeyword_success() throws Exception {
        // given

        SubscribeKeywordRequestDto request = new SubscribeKeywordRequestDto(Arrays.asList("키워드1", "키워드2"));
        SubscribeKeywordResponseDto response = new SubscribeKeywordResponseDto(Arrays.asList("키워드1", "키워드2"));

        given(keywordService.subscribeKeywords(1L, request))
                .willReturn(response);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print()) // 실제 응답 내용을 보기 위해 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.registeredKeywords[0]").value("키워드1"))
                .andExpect(jsonPath("$.data.registeredKeywords[1]").value("키워드2"));

        verify(keywordService).subscribeKeywords(1L, request);
    }

    @Test
    @DisplayName("이미 구독한 키워드를 등록하면 예외가 발생한다.")
    @WithMockJwtAuthentication(id = 1L)
    void registerKeyword_alreadySubscribed() throws Exception {
        // given
        SubscribeKeywordRequestDto request = new SubscribeKeywordRequestDto(Arrays.asList("키워드1"));
        // 예외 발생 설정
        given(keywordService.subscribeKeywords(1L, request))
                .willThrow(new AlreadySubscribedKeywordException(List.of("키워드1")));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/keywords")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("이미 등록된 키워드입니다.: 키워드1"));
    }
}