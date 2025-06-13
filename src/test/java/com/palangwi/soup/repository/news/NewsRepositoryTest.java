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

    private LocalDateTime now = LocalDateTime.now();
    private Pageable pageable;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    @BeforeEach
    void setUp() {
        News news1 = createNews("인공지능", 500);
        News news2 = createNews("클라우드", 700);
        News news3 = createNews("빅데이터", 800);

        newsRepository.saveAll(List.of(news1, news2, news3));

        pageable = PageRequest.of(0, 20);
        startDate = now.minusMinutes(1);
        endDate = now.plusDays(1);
    }

    @AfterEach
    void tearDown() {
        newsRepository.deleteAll();
    }

    @Test
    @DisplayName("날짜와 키워드 리스트로 News 목록을 조회한다.")
    void findByCreatedDateBetweenAndKeywordIn() {
        List<News> result = newsRepository.findByCreatedDateBetweenAndKeywordIn(startDate, endDate, List.of("인공지능", "클라우드"));

        assertThat(result).hasSize(2);
        assertThat(result).extracting(News::getKeyword)
                .containsExactlyInAnyOrder("인공지능", "클라우드");
    }

    @Test
    @DisplayName("빈 리스트로 검색 시 빈 값을 반환한다.")
    void findByCreatedDateBetweenAndKeywordIn_withEmptyKeywordList() {
        List<News> result = newsRepository.findByCreatedDateBetweenAndKeywordIn(startDate, endDate, List.of());

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 키워드 검색 시 빈 값을 반환한다.")
    void findByCreatedDateBetweenAndKeywordIn_withNonExistingKeyword_returnsEmpty() {
        List<News> result = newsRepository.findByCreatedDateBetweenAndKeywordIn(startDate, endDate, List.of("없는키워드"));

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("날짜와 단일 키워드로 News 페이지를 조회한다.")
    void findByCreatedDateBetweenAndKeyword() {
        Page<News> result = newsRepository.findByCreatedDateBetweenAndKeyword(startDate, endDate, "인공지능", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result.getContent()).allMatch(n -> n.getKeyword().equals("인공지능"));
    }

    @Test
    @DisplayName("날짜로 News 페이지를 조회한다.")
    void findByCreatedDateBetween() {
        Page<News> result = newsRepository.findByCreatedDateBetween(startDate, endDate, pageable);

        assertThat(result).hasSize(3);
        assertThat(result.getTotalElements()).isEqualTo(3);
        assertThat(result).extracting(News::getKeyword)
                .containsExactlyInAnyOrder("인공지능", "클라우드", "빅데이터");
    }

    @Test
    @DisplayName("키워드로 News 페이지를 조회한다.")
    void findByKeyword() {
        Page<News> result = newsRepository.findByKeyword("인공지능", pageable);

        assertThat(result).hasSize(1);
        assertThat(result.getTotalElements()).isEqualTo(1);
        assertThat(result).extracting(News::getKeyword)
                .containsExactlyInAnyOrder("인공지능");
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

    private News createNews(String keyword, int tokens) {
        Summary summary = new Summary("짧은 요약", "긴 요약");
        List<Article> articles = List.of(
                new Article("기사 제목 1", "https://news.com/1", "기사 요약 1"),
                new Article("기사 제목 2", "https://news.com/2", "기사 요약 2")
        );

        return new News(keyword, summary, articles, tokens);
    }
}