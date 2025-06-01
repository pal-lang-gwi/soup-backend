package com.palangwi.soup.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.security.Role;
import java.time.LocalDate;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.Source;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import jakarta.transaction.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.testcontainers.junit.jupiter.Testcontainers;

@Transactional
@Testcontainers
class KeywordRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private KeywordRepository keywordRepository;

    @BeforeEach
    void setUp() {
        User user = createUser("테스트 닉네임");
        Keyword keyword1 = Keyword.of("키워드1", "키워드1", Source.USER_REQUEST, user);
        Keyword keyword2 = Keyword.of("키워드2", "키워드2", Source.MANUAL, user);
        keywordRepository.saveAll(Arrays.asList(keyword1, keyword2));
    }

    @DisplayName("키워드 이름으로 존재 여부를 확인한다.")
    @Test
    void existsByName() {
        boolean exists = keywordRepository.existsByName("키워드1");
        assertThat(exists).isTrue();
    }

    @DisplayName("존재하지 않는 키워드 이름으로 존재 여부를 확인하면 false를 반환한다.")
    @Test
    void existsByName_false() {
        boolean exists = keywordRepository.existsByName("없는키워드");
        assertThat(exists).isFalse();
    }

    @DisplayName("키워드 이름으로 키워드를 조회한다.")
    @Test
    void findByName() {
        Optional<Keyword> result = keywordRepository.findByName("키워드1");
        assertThat(result).isPresent();
        assertThat(result.get().getName()).isEqualTo("키워드1");
    }

    @DisplayName("존재하지 않는 키워드 이름으로 조회하면 빈 값을 반환한다.")
    @Test
    void findByName_empty() {
        Optional<Keyword> result = keywordRepository.findByName("없는키워드");
        assertThat(result).isEmpty();
    }

    @DisplayName("여러 키워드 이름으로 키워드 리스트를 조회한다.")
    @Test
    void findAllByNameIn() {
        List<Keyword> result = keywordRepository.findAllByNameIn(Arrays.asList("키워드1", "키워드2", "없는키워드"));
        assertThat(result).hasSize(2);
        assertThat(result).extracting("name").containsExactlyInAnyOrder("키워드1", "키워드2");
    }

    private User createUser(String nickname) {
        return User.builder()
                .email("asdf1234@naver.com")
                .username("테스트")
                .nickname(nickname)
                .role(Role.USER)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1999, 9, 9))
                .providerId("구글")
                .profileImageUrl("https://sample-image.png")
                .build();
    }
}