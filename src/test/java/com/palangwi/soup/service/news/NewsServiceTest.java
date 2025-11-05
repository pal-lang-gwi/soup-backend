package com.palangwi.soup.service.news;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.Source;
import com.palangwi.soup.domain.news.News;
import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.dto.news.NewsResult;
import com.palangwi.soup.dto.news.NewsSummary;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import com.palangwi.soup.repository.news.NewsRedisRepository;
import com.palangwi.soup.repository.news.NewsRepository;
import com.palangwi.soup.repository.user.UserRepository;
import com.palangwi.soup.security.Role;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

class NewsServiceTest extends IntegrationTestSupport {

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        keywordRepository.deleteAll();
        userRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        keywordRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Autowired
    private NewsService newsService;

    @MockitoBean
    private NewsRepository newsRepository;

    @MockitoBean
    private NewsRedisRepository newsRedisRepository;

    @MockitoBean
    private NewsAIService newsAIService;

    @Test
    @DisplayName("AI 뉴스 요약 결과를 Redis에 저장한다.")
    void collectAndSaveNews() {
        // given
        User user = createUser("TestNickname");
        Keyword savedKeyword = keywordRepository.save(
                Keyword.of("AI", "AI", Source.USER_REQUEST, user)
        );

        Long keywordId = savedKeyword.getId();
        String keywordName = savedKeyword.getName();

        NewsResult mockResult = new NewsResult(
                savedKeyword.getId(),
                savedKeyword.getName(),
                new NewsSummary.Summary("짧은 요약", "긴 요약"),
                List.of(new NewsSummary.Article("제목", "https://link", "요약")),
                123
        );

        given(newsAIService.searchAndSummarizeAsync(keywordId, keywordName))
                .willReturn(CompletableFuture.completedFuture(mockResult));

        // when
        newsService.collectAndSendNews(keywordId);

        // then
        ArgumentCaptor<NewsResult> captor = ArgumentCaptor.forClass(NewsResult.class);

        await().atMost(Duration.ofSeconds(10))
                .untilAsserted(() ->
                        verify(newsRedisRepository)
                                .save(anyLong(), captor.capture(), anyLong())
                );

        NewsResult captured = captor.getValue();
        assertThat(captured.keywordId()).isEqualTo(keywordId);
        assertThat(captured.keywordName()).isEqualTo(keywordName);
        assertThat(captured.tokens()).isEqualTo(123);
        assertThat(captured.summary().short_summary()).isEqualTo("짧은 요약");
        assertThat(captured.summary().long_summary()).isEqualTo("긴 요약");
        assertThat(captured.articles()).hasSize(1);
        assertThat(captured.articles().getFirst().title()).isEqualTo("제목");
    }


    private User createUser(String nickname) {
        User user = User.builder()
                .email("test_" + UUID.randomUUID() + "@test.com")
                .username("테스트")
                .nickname(nickname)
                .role(Role.USER)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1999, 9, 9))
                .providerId("구글")
                .profileImageUrl("https://sample-image.png")
                .build();

        return userRepository.save(user);
    }
}