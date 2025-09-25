package com.palangwi.soup.repository.news;

import static org.assertj.core.api.Assertions.*;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.domain.news.Article;
import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.domain.news.Summary;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

class NewsRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private NewsRepository newsRepository;

    private Pageable pageable;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        News news1 = createNews(1L, "인공지능", 500);
        News news2 = createNews(2L, "클라우드", 700);
        News news3 = createNews(3L, "빅데이터", 800);

        newsRepository.saveAll(List.of(news1, news2, news3));

        List<News> savedNews = newsRepository.findAll();

        LocalDateTime minDate = savedNews.stream()
                .map(News::getCreatedDate)
                .min(LocalDateTime::compareTo)
                .orElseThrow();

        LocalDateTime maxDate = savedNews.stream()
                .map(News::getCreatedDate)
                .max(LocalDateTime::compareTo)
                .orElseThrow();

        startDate = minDate.minusMinutes(1);
        endDate = maxDate.plusDays(1);

        pageable = PageRequest.of(0, 20);
    }

    @AfterEach
    void tearDown() {
        newsRepository.deleteAll();
    }

    @Test
    @DisplayName("날짜와 키워드 리스트로 News 목록을 조회한다.")
    void findByCreatedDateBetweenAndKeywordIn() {
        List<News> result = newsRepository.findByCreatedDateBetweenAndKeywordIdIn(startDate, endDate, List.of(1L, 2L));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(News::getKeywordId)
                .containsExactlyInAnyOrder(1L, 2L);
    }

    @Test
    @DisplayName("빈 리스트로 검색 시 빈 값을 반환한다.")
    void findByCreatedDateBetweenAndKeywordIn_withEmptyKeywordList() {
        List<News> result = newsRepository.findByCreatedDateBetweenAndKeywordIdIn(startDate, endDate, List.of());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 키워드 검색 시 빈 값을 반환한다.")
    void findByCreatedDateBetweenAndKeywordIn_withNonExistingKeyword_returnsEmpty() {
        List<News> result = newsRepository.findByCreatedDateBetweenAndKeywordIdIn(startDate, endDate, List.of(-1L));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("날짜와 단일 키워드로 News 페이지를 조회한다.")
    void findByCreatedDateBetweenAndKeyword() {
        Page<News> result = newsRepository.findByCreatedDateBetweenAndKeywordId(startDate, endDate, 1L, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).allMatch(n -> n.getKeywordId().equals(1L));
    }

    @Test
    @DisplayName("날짜로 News 페이지를 조회한다.")
    void findByCreatedDateBetween() {
        Page<News> result = newsRepository.findByCreatedDateBetween(startDate, endDate, pageable);

        assertThat(result).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result).extracting(News::getKeywordId)
                .containsExactlyInAnyOrder(1L, 2L, 3L);
    }

    @Test
    @DisplayName("키워드로 News 페이지를 조회한다.")
    void findByKeyword() {
        Page<News> result = newsRepository.findByKeywordId(1L, pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result).extracting(News::getKeywordId)
                .containsExactlyInAnyOrder(1L);
    }

    @Test
    @DisplayName("페이지 경계값 테스트")
    void findByCreatedDateBetween_withPageSizeOne() {
        Pageable smallPageable = PageRequest.of(0, 1);
        Page<News> result = newsRepository.findByCreatedDateBetween(startDate, endDate, smallPageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result.getTotalPages()).isEqualTo(3);
    }

    private News createNews(Long keywordId, String keyword, int tokens) {
        Summary summary = new Summary("짧은 요약", "긴 요약");
        List<Article> articles = List.of(
                new Article("기사 제목 1", "https://news.com/1", "기사 요약 1"),
                new Article("기사 제목 2", "https://news.com/2", "기사 요약 2")
        );

        return new News(keywordId, keyword, summary, articles, tokens);
    }
}