package com.palangwi.soup.service.keyword;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.dto.keyword.RegisterKeywordRequestDto;
import com.palangwi.soup.dto.keyword.response.RegisterKeywordResponseDto;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import com.palangwi.soup.repository.user.UserRepository;
import com.palangwi.soup.repository.userkeyword.UserKeywordRepository;
import com.palangwi.soup.security.Role;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

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

    @BeforeEach
    void setUp() {
        keywordRepository.deleteAll();
        userRepository.deleteAll();
        userKeywordRepository.deleteAll();
    }

    private User createUser() {
        return userRepository.save(
                User.builder()
                        .email("test@test.com")
                        .username("테스트")
                        .nickname("테스트닉네임")
                        .role(Role.USER)
                        .gender(Gender.MALE)
                        .birthDate(LocalDate.of(1999, 9, 9))
                        .providerId("구글")
                        .profileImageUrl("https://sample-image.png")
                        .build());
    }

    @DisplayName("키워드가 DB에 없으면 새로 생성되고, 유저-키워드 관계도 생성된다.")
    @Test
    void registerKeyword_정상등록() {
        // given
        User user = createUser();
        List<String> keywords = Arrays.asList("키워드1", "키워드2");
        RegisterKeywordRequestDto requestDto = new RegisterKeywordRequestDto(keywords);

        // when
        RegisterKeywordResponseDto result = keywordService.registerKeyword(user.getId(), requestDto);

        // then
        assertThat(result.registeredKeywords()).hasSize(2);
        assertThat(keywordRepository.findAll()).hasSize(2);
        assertThat(userKeywordRepository.findAll()).hasSize(2);
    }
}