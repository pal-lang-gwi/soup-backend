package com.palangwi.soup.service.user;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.repository.user.UserRepository;
import com.palangwi.soup.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.*;

class UserHistoryServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    private static final String TEST_EMAIL = "test@test.com";

    @Test
    @DisplayName("회원가입 이력이 정상적으로 저장된다.")
    void saveCreateHistory() {
        // given
        User user = createUser("가입 닉네임");

        // when
        User result = userRepository.save(user);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getEmail()).isEqualTo(TEST_EMAIL);
    }

    @Test
    @DisplayName("회원탈퇴 이력이 정상적으로 저장된다.")
    void saveDeleteHistory() {
        // given
        User user = createUser("탈퇴 닉네임");

        // when
        User result = userRepository.save(user);

        LocalDateTime now = LocalDateTime.now();
        user.deleteUser(now);

        // then
        assertThat(result).isNotNull();
        assertThat(result.getDeleted()).isEqualTo(true);
        assertThat(result.getDeletedAt()).isEqualTo(now);
    }

    private User createUser(String nickname) {
        User user = User.builder()
                .email(TEST_EMAIL)
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
