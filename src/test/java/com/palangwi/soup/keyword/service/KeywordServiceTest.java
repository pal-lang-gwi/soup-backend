package com.palangwi.soup.keyword.service;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.admin.keyword.repository.AdminKeywordRepository;
import com.palangwi.soup.admin.keyword.service.AdminKeywordRequestService;
import com.palangwi.soup.common.security.Role;
import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.keyword.domain.PendingKeywordRequest;
import com.palangwi.soup.keyword.domain.Source;
import com.palangwi.soup.keyword.dto.SubscribeKeywordRequestDto;
import com.palangwi.soup.keyword.dto.response.SearchKeywordDto;
import com.palangwi.soup.keyword.dto.response.SearchKeywordsResponseDto;
import com.palangwi.soup.keyword.dto.response.SubscribeKeywordResponseDto;
import com.palangwi.soup.keyword.repository.KeywordRepository;
import com.palangwi.soup.user.domain.Gender;
import com.palangwi.soup.user.domain.User;
import com.palangwi.soup.subscription.repository.UserKeywordRepository;
import com.palangwi.soup.user.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class KeywordServiceTest extends IntegrationTestSupport {

    @Autowired
    private KeywordService keywordService;

    @Autowired
    private KeywordRepository keywordRepository;

    @Autowired
    private UserKeywordRepository userKeywordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AdminKeywordRequestService adminKeywordRequestService;
    @Autowired
    private AdminKeywordRepository adminKeywordRepository;

    @BeforeEach
    void setUp() {
        keywordRepository.deleteAll();
        userRepository.deleteAll();
        userKeywordRepository.deleteAll();
    }

    @AfterEach
    void tearDown() {
        userKeywordRepository.deleteAll();
        keywordRepository.deleteAll();
        userRepository.deleteAll();
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

    @DisplayName("키워드가 DB에 없으면 새로 생성되고, 유저-키워드 관계도 생성된다.")
    @Test
    void registerKeyword_정상등록() {
        // given
        User user1 = createUser("키워드를 등록한 사용자");
        Keyword keyword1 = Keyword.of("키워드1", "키워드1", Source.USER_REQUEST, user1);
        Keyword keyword2 = Keyword.of("키워드2", "키워드2", Source.USER_REQUEST, user1);

        User user2 = createUser("키워드를 등록할 사용자");

        keywordRepository.saveAll(Arrays.asList(keyword1, keyword2));

        Long keywordId = keyword1.getId();

        SubscribeKeywordRequestDto requestDto = new SubscribeKeywordRequestDto(keywordId);

        // when
        SubscribeKeywordResponseDto result = keywordService.subscribeKeyword(user2.getId(), requestDto);

        // then
        assertThat(result.keywordId()).isEqualTo(keywordId);
        assertThat(keywordRepository.existsById(keywordId)).isTrue();
        assertThat(userKeywordRepository.findAll()).hasSize(1);
    }

    @Test
    @DisplayName("검색 키워드가 정상적으로 조회된다.")
    void searchKeywords_정상() {
        // given
        User user = createUser("테스트 닉네임");
        Keyword keyword1 = Keyword.of("자바", "java", Source.USER_REQUEST, user);
        Keyword keyword2 = Keyword.of("자바스크립트", "javascript", Source.USER_REQUEST, user);
        Keyword keyword3 = Keyword.of("파이썬", "python", Source.USER_REQUEST, user);
        keywordRepository.saveAll(Arrays.asList(keyword1, keyword2, keyword3));

        PendingKeywordRequest request = PendingKeywordRequest.of(user, keyword1);
        adminKeywordRepository.save(request);
        Long requestId = request.getId();

        adminKeywordRequestService.approveKeyword(requestId);

        Pageable pageable = PageRequest.of(0, 20);

        // when
        SearchKeywordsResponseDto response = keywordService.searchKeywords(user.getId(), "자바", pageable);

        // then
        assertThat(response.keywords()).hasSize(1);
        assertThat(response.keywords().stream().map(SearchKeywordDto::name).toList())
                .containsExactly("자바");

        assertThat(response.keywords().get(0).name()).isEqualTo("자바");
        assertThat(response.keywords().get(0).normalizedName()).isEqualTo("java");
        assertThat(response.keywords().get(0).isSubscribed()).isTrue();

        assertThat(response.totalElements()).isEqualTo(1);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.currentPage()).isEqualTo(1);
    }

    @Test
    @DisplayName("부분 일치 검색이 정상적으로 동작한다.")
    void searchKeywords_부분일치() {
        // given
        User user = createUser("테스트 닉네임");
        Keyword keyword1 = Keyword.of("자바", "java", Source.USER_REQUEST, user);
        Keyword keyword2 = Keyword.of("자바스크립트", "javascript", Source.USER_REQUEST, user);
        Keyword keyword3 = Keyword.of("파이썬", "python", Source.USER_REQUEST, user);

        String searchKeyword = "자";
        keyword1.approve(user);
        keyword2.approve(user);

        keywordRepository.saveAll(Arrays.asList(keyword1, keyword2, keyword3));
        Pageable pageable = PageRequest.of(0, 20);
        // when
        SearchKeywordsResponseDto response = keywordService.searchKeywords(user.getId(), searchKeyword, pageable);

        // then
        assertThat(response.keywords()).hasSize(2);
        assertThat(response.keywords().stream().map(SearchKeywordDto::name).toList())
                .containsExactly("자바", "자바스크립트");
        assertThat(response.totalElements()).isEqualTo(2);
        assertThat(response.totalPages()).isEqualTo(1);
        assertThat(response.currentPage()).isEqualTo(1);
    }
}