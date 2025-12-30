package com.palangwi.soup.news.controller;

import com.palangwi.soup.RestDocsSupport;
import com.palangwi.soup.news.dto.ArticleDto;
import com.palangwi.soup.news.dto.DailyNewsRequestDto;
import com.palangwi.soup.news.dto.DailyNewsResponseDto;
import com.palangwi.soup.news.dto.NewsDto;
import com.palangwi.soup.news.service.NewsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class NewsControllerDocsTest extends RestDocsSupport {

    private final NewsService newsService = mock(NewsService.class);

    @Override
    protected Object initController() {
        return new NewsController(newsService);
    }

    @DisplayName("뉴스 목록 조회 API")
    @Test
    void getFilteredNews() throws Exception {
        // given
        ArticleDto articleDto = new ArticleDto("테스트 뉴스 제목", "https://example.com/news", "테스트 뉴스 요약");
        NewsDto newsDto = new NewsDto(
                1L,
                "테스트 키워드",
                "테스트 뉴스의 긴 요약 내용입니다.",
                List.of(articleDto),
                List.of("AI", "LLM", "OpenAI"),
                LocalDateTime.now()
        );
        DailyNewsResponseDto response = new DailyNewsResponseDto(
                List.of(newsDto),
                1,
                1,
                0
        );
        given(newsService.getDailyNews(any(DailyNewsRequestDto.class), any(Pageable.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/news")
                        .param("keywordId", "1")
                        .param("startDate", "2024-01-01")
                        .param("endDate", "2024-01-31")
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("news-list",
                        queryParameters(
                                parameterWithName("keywordId").description("키워드 ID (선택)").optional(),
                                parameterWithName("startDate").description("시작 날짜 (yyyy-MM-dd 형식, 선택)").optional(),
                                parameterWithName("endDate").description("종료 날짜 (yyyy-MM-dd 형식, 선택)").optional(),
                                parameterWithName("page").description("페이지 번호 (0부터 시작)").optional(),
                                parameterWithName("size").description("페이지 크기").optional(),
                                parameterWithName("sort").description("정렬 기준 (createdDate)").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.newsDtos").type(JsonFieldType.ARRAY).description("뉴스 목록"),
                                fieldWithPath("data.newsDtos[].keywordId").type(JsonFieldType.NUMBER).description("키워드 ID"),
                                fieldWithPath("data.newsDtos[].keywordName").type(JsonFieldType.STRING).description("키워드 이름"),
                                fieldWithPath("data.newsDtos[].longSummary").type(JsonFieldType.STRING).description("뉴스 긴 요약"),
                                fieldWithPath("data.newsDtos[].articles").type(JsonFieldType.ARRAY).description("기사 목록"),
                                fieldWithPath("data.newsDtos[].articles[].title").type(JsonFieldType.STRING).description("기사 제목"),
                                fieldWithPath("data.newsDtos[].articles[].url").type(JsonFieldType.STRING).description("기사 URL"),
                                fieldWithPath("data.newsDtos[].articles[].summary").type(JsonFieldType.STRING).description("기사 요약"),
                                fieldWithPath("data.newsDtos[].relatedKeywords").type(JsonFieldType.ARRAY).description("관련 키워드 목록 (문자열 배열)"),
                                fieldWithPath("data.newsDtos[].createdDate").type(JsonFieldType.ARRAY).description("생성 일시 (배열 형식: [년, 월, 일, 시, 분, 초, 나노초])"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("뉴스 상세 조회 API")
    @Test
    void getNewsInfo() throws Exception {
        // given
        ArticleDto articleDto = new ArticleDto("테스트 뉴스 제목", "https://example.com/news", "테스트 뉴스 요약");
        NewsDto newsDto = new NewsDto(
                1L,
                "테스트 키워드",
                "테스트 뉴스의 긴 요약 내용입니다.",
                List.of(articleDto),
                List.of("AI", "LLM", "OpenAI"),
                LocalDateTime.now()
        );
        given(newsService.getNewsDetailInfo(anyString())).willReturn(newsDto);

        // when & then
        mockMvc.perform(get("/api/v1/news/{newsId}", "news123"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("news-detail",
                        pathParameters(
                                parameterWithName("newsId").description("뉴스 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.keywordId").type(JsonFieldType.NUMBER).description("키워드 ID"),
                                fieldWithPath("data.keywordName").type(JsonFieldType.STRING).description("키워드 이름"),
                                fieldWithPath("data.longSummary").type(JsonFieldType.STRING).description("뉴스 긴 요약"),
                                fieldWithPath("data.articles").type(JsonFieldType.ARRAY).description("기사 목록"),
                                fieldWithPath("data.articles[].title").type(JsonFieldType.STRING).description("기사 제목"),
                                fieldWithPath("data.articles[].url").type(JsonFieldType.STRING).description("기사 URL"),
                                fieldWithPath("data.articles[].summary").type(JsonFieldType.STRING).description("기사 요약"),
                                fieldWithPath("data.relatedKeywords").type(JsonFieldType.ARRAY).description("관련 키워드 목록 (문자열 배열)"),
                                fieldWithPath("data.createdDate").type(JsonFieldType.ARRAY).description("생성 일시 (배열 형식: [년, 월, 일, 시, 분, 초, 나노초])"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }
}

