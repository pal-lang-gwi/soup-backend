package com.palangwi.soup.keyword.controller;

import com.palangwi.soup.RestDocsSupport;
import com.palangwi.soup.keyword.dto.KeywordListResponseDto;
import com.palangwi.soup.keyword.dto.KeywordResponseDto;
import com.palangwi.soup.keyword.dto.RequestKeywordRequestDto;
import com.palangwi.soup.keyword.dto.SubscribeKeywordRequestDto;
import com.palangwi.soup.keyword.dto.response.KeywordUnsubscribeResponseDto;
import com.palangwi.soup.keyword.dto.response.RequestKeywordResponseDto;
import com.palangwi.soup.keyword.dto.response.SearchKeywordDto;
import com.palangwi.soup.keyword.dto.response.SearchKeywordsResponseDto;
import com.palangwi.soup.keyword.dto.response.SubscribeKeywordResponseDto;
import com.palangwi.soup.keyword.service.KeywordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class KeywordControllerDocsTest extends RestDocsSupport {

    private final KeywordService keywordService = mock(KeywordService.class);

    @Override
    protected Object initController() {
        return new KeywordController(keywordService);
    }

    @DisplayName("키워드 목록 조회 API")
    @Test
    void getKeywordList() throws Exception {
        // given
        KeywordResponseDto keywordDto = new KeywordResponseDto(1L, "테스트 키워드", "테스트키워드");
        KeywordListResponseDto response = new KeywordListResponseDto(
                List.of(keywordDto),
                1,
                1,
                0);
        given(keywordService.getKeywordList(any(Pageable.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/keywords")
                .param("page", "0")
                .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("keyword-list",
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                                parameterWithName("size").description("페이지 크기").optional(),
                                parameterWithName("sort").description("정렬 기준 (createdDate, name)").optional()),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.keywordResponseDtos").type(JsonFieldType.ARRAY)
                                        .description("키워드 목록"),
                                fieldWithPath("data.keywordResponseDtos[].id").type(JsonFieldType.NUMBER)
                                        .description("키워드 ID"),
                                fieldWithPath("data.keywordResponseDtos[].name").type(JsonFieldType.STRING)
                                        .description("키워드 이름"),
                                fieldWithPath("data.keywordResponseDtos[].normalizedName").type(JsonFieldType.STRING)
                                        .description("정규화된 키워드 이름"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)"))));
    }

    @DisplayName("키워드 검색 API")
    @Test
    void searchKeywords() throws Exception {
        // given
        SearchKeywordDto searchKeywordDto = new SearchKeywordDto(1L, "테스트 키워드", "테스트키워드", false);
        SearchKeywordsResponseDto response = new SearchKeywordsResponseDto(
                List.of(searchKeywordDto),
                1,
                1,
                0);
        given(keywordService.searchKeywords(anyLong(), anyString(), any(Pageable.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/keywords/search")
                .param("keyword", "테스트")
                .param("page", "0")
                .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("keyword-search",
                        queryParameters(
                                parameterWithName("keyword").description("검색할 키워드 (1-100자)"),
                                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                                parameterWithName("size").description("페이지 크기").optional()),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.keywords").type(JsonFieldType.ARRAY).description("검색된 키워드 목록"),
                                fieldWithPath("data.keywords[].id").type(JsonFieldType.NUMBER).description("키워드 ID"),
                                fieldWithPath("data.keywords[].name").type(JsonFieldType.STRING).description("키워드 이름"),
                                fieldWithPath("data.keywords[].normalizedName").type(JsonFieldType.STRING)
                                        .description("정규화된 키워드 이름"),
                                fieldWithPath("data.keywords[].isSubscribed").type(JsonFieldType.BOOLEAN)
                                        .description("구독 여부"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)"))));
    }

    @DisplayName("키워드 구독 API")
    @Test
    void subscribeKeyword() throws Exception {
        // given
        SubscribeKeywordResponseDto response = new SubscribeKeywordResponseDto(1L, "테스트 키워드");
        given(keywordService.subscribeKeyword(anyLong(), any(SubscribeKeywordRequestDto.class))).willReturn(response);

        SubscribeKeywordRequestDto requestDto = new SubscribeKeywordRequestDto(1L);

        // when & then
        mockMvc.perform(post("/api/v1/keywords/subscriptions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("keyword-subscribe",
                        requestFields(
                                fieldWithPath("keywordId").type(JsonFieldType.NUMBER).description("구독할 키워드 ID")),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.keywordId").type(JsonFieldType.NUMBER).description("구독한 키워드 ID"),
                                fieldWithPath("data.keywordName").type(JsonFieldType.STRING).description("구독한 키워드 이름"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)"))));
    }

    @DisplayName("키워드 구독 해제 API")
    @Test
    void unsubscribeKeyword() throws Exception {
        // given
        KeywordUnsubscribeResponseDto response = new KeywordUnsubscribeResponseDto(1L, "테스트 키워드");
        given(keywordService.unsubscribeKeyword(anyLong())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/keywords/subscriptions/{subscriptionId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("keyword-unsubscribe",
                        pathParameters(
                                parameterWithName("subscriptionId").description("구독 ID")),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("data.keywordName").type(JsonFieldType.STRING)
                                        .description("구독 해제된 키워드 이름"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)"))));
    }

    @DisplayName("키워드 요청 API")
    @Test
    void requestKeywords() throws Exception {
        // given
        RequestKeywordResponseDto response = new RequestKeywordResponseDto(1L, "새로운 키워드");
        given(keywordService.requestKeywords(anyLong(), any(RequestKeywordRequestDto.class))).willReturn(response);

        RequestKeywordRequestDto requestDto = new RequestKeywordRequestDto("새로운 키워드");

        // when & then
        mockMvc.perform(post("/api/v1/keywords/request")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("keyword-request",
                        requestFields(
                                fieldWithPath("keyword").type(JsonFieldType.STRING)
                                        .description("요청할 키워드 (필수, 비어있지 않아야 함)")),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("data.keyword").type(JsonFieldType.STRING).description("요청된 키워드"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)"))));
    }
}
