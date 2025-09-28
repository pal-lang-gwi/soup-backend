package com.palangwi.soup.controller.news;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.dto.news.ArticleDto;
import com.palangwi.soup.dto.news.DailyNewsRequestDto;
import com.palangwi.soup.dto.news.DailyNewsResponseDto;
import com.palangwi.soup.dto.news.NewsDto;
import com.palangwi.soup.security.WithMockJwtAuthentication;
import com.palangwi.soup.service.news.NewsService;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

class NewsControllerTest extends IntegrationTestSupport {

    @MockitoBean
    private NewsService newsService;

    private DailyNewsRequestDto request;
    private DailyNewsResponseDto response;
    private NewsDto newsDto;
    private Pageable pageable;
    private List<ArticleDto> articles;
    private Long keywordId;
    private String keywordName;
    private String startDate;
    private String endDate;

    @BeforeEach
    void setUp() {
        keywordId = 1L;
        keywordName = "인공지능";
        startDate = "2025-06-01";
        endDate = "2025-06-02";

        articles = List.of(
                new ArticleDto("인공지능 뉴스 1", "https://news.com/1", "AI 요약 1"),
                new ArticleDto("인공지능 뉴스 2", "https://news.com/2", "AI 요약 2")
        );

        newsDto = new NewsDto(
                keywordId,
                keywordName,
                "긴 요약",
                articles,
                LocalDateTime.of(2025, 6, 1, 6, 0)
        );

        request = new DailyNewsRequestDto(keywordId, startDate, endDate);
        response = new DailyNewsResponseDto(List.of(newsDto), 1L, 1, 0);
        pageable = PageRequest.of(0, 20, Direction.DESC, "createdDate");
    }

    @Test
    @DisplayName("뉴스 필터링 검색에 성공한다.")
    @WithMockJwtAuthentication
    void getFilteredNews() throws Exception {
        given(newsService.getDailyNews(DailyNewsRequestDto.from(request), pageable))
                .willReturn(response);

        //when && then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/news")
                .param("keywordId", String.valueOf(keywordId))
                .param("startDate", startDate)
                .param("endDate", endDate)
                .param("page", "0")
                .param("size", "20")
                .param("sort", "createdDate,DESC")
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.newsDtos[0].keywordName").value("인공지능"))
                .andExpect(jsonPath("$.data.newsDtos[0].longSummary").value("긴 요약"))
                .andExpect(jsonPath("$.data.newsDtos[0].articles[0].title").value("인공지능 뉴스 1"))
                .andExpect(jsonPath("$.data.newsDtos[0].articles[0].url").value("https://news.com/1"))
                .andExpect(jsonPath("$.data.newsDtos[0].articles[0].summary").value("AI 요약 1"))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.totalPages").value(1))
                .andExpect(jsonPath("$.data.currentPage").value(0));

        verify(newsService).getDailyNews(DailyNewsRequestDto.from(request), pageable);
    }

    @Test
    @DisplayName("단일 뉴스 정보 조회에 성공한다.")
    @WithMockJwtAuthentication
    void getNewsInfo() throws Exception {
        //given
        String newsId = "1";
        given(newsService.getNewsDetailInfo(newsId))
                .willReturn(newsDto);

        //when && then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/news/{newsId}", newsId)
                .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.keywordName").value("인공지능"))
                .andExpect(jsonPath("$.data.longSummary").value("긴 요약"))
                .andExpect(jsonPath("$.data.articles[0].title").value("인공지능 뉴스 1"))
                .andExpect(jsonPath("$.data.articles[0].url").value("https://news.com/1"))
                .andExpect(jsonPath("$.data.articles[0].summary").value("AI 요약 1"));

        verify(newsService).getNewsDetailInfo(newsId);
    }
}