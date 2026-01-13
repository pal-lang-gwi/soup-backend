package com.palangwi.soup.keyword.controller;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.keyword.dto.SubscribeKeywordRequestDto;
import com.palangwi.soup.keyword.dto.response.SearchKeywordDto;
import com.palangwi.soup.keyword.dto.response.SearchKeywordsResponseDto;
import com.palangwi.soup.keyword.dto.response.SubscribeKeywordResponseDto;
import com.palangwi.soup.keyword.exception.AlreadySubscribedKeywordException;
import com.palangwi.soup.keyword.service.KeywordService;
import com.palangwi.soup.common.security.WithMockJwtAuthentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class KeywordControllerTest extends IntegrationTestSupport {

    @MockitoBean
    private KeywordService keywordService;

    @Test
    @DisplayName("키워드 검색에 성공한다.")
    @WithMockJwtAuthentication(id = 1L)
    void searchKeywords_success() throws Exception {
        // given
        String searchKeyword = "자바";
        Pageable pageable = PageRequest.of(0, 20);

        List<SearchKeywordDto> keywords = Arrays.asList(
                new SearchKeywordDto(1L, "자바", "java", true),
                new SearchKeywordDto(2L, "자바스크립트", "javascript", false));
        SearchKeywordsResponseDto response = SearchKeywordsResponseDto.from(keywords, 2, 1, 1);

        given(keywordService.searchKeywords(any(Long.class), any(String.class), any(Pageable.class)))
                .willReturn(response);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/keywords/search")
                .param("keyword", searchKeyword)
                .param("page", "0")
                .param("size", "20")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.keywords[0].id").value(1))
                .andExpect(jsonPath("$.data.keywords[0].name").value("자바"))
                .andExpect(jsonPath("$.data.keywords[0].normalizedName").value("java"))
                .andExpect(jsonPath("$.data.keywords[0].isSubscribed").value(true))
                .andExpect(jsonPath("$.data.keywords[1].id").value(2))
                .andExpect(jsonPath("$.data.keywords[1].name").value("자바스크립트"))
                .andExpect(jsonPath("$.data.keywords[1].normalizedName").value("javascript"))
                .andExpect(jsonPath("$.data.keywords[1].isSubscribed").value(false))
                .andExpect(jsonPath("$.data.totalElements").value(2))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(1));

        verify(keywordService).searchKeywords(any(Long.class), any(String.class), any(Pageable.class));
    }

    @Test
    @DisplayName("키워드 등록에 성공한다.")
    @WithMockJwtAuthentication(id = 1L)
    void registerKeyword_success() throws Exception {
        // given
        SubscribeKeywordRequestDto request = new SubscribeKeywordRequestDto(1L);
        SubscribeKeywordResponseDto response = new SubscribeKeywordResponseDto(1L, "AI");

        given(keywordService.subscribeKeyword(1L, request))
                .willReturn(response);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/keywords/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.keywordId").value(1L))
                .andExpect(jsonPath("$.data.keywordName").value("AI"));

        verify(keywordService).subscribeKeyword(1L, request);
    }

    @Test
    @DisplayName("이미 구독한 키워드를 등록하면 예외가 발생한다.")
    @WithMockJwtAuthentication(id = 1L)
    void registerKeyword_alreadySubscribed() throws Exception {
        // given
        SubscribeKeywordRequestDto request = new SubscribeKeywordRequestDto(1L);
        given(keywordService.subscribeKeyword(1L, request))
                .willThrow(new AlreadySubscribedKeywordException("AI"));

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/keywords/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("이미 등록된 키워드입니다.: AI"));
    }
}