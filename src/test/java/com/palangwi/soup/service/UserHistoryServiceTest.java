package com.palangwi.soup.service;

import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userlog.ChangeType;
import com.palangwi.soup.domain.userlog.UserHistory;
import com.palangwi.soup.repository.UserHistoryRepository;
import com.palangwi.soup.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
class UserHistoryServiceTest {

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    private static final String TEST_EMAIL = "test@test.com";

    @Test
    @DisplayName("회원가입 이력이 정상적으로 저장된다.")
    void saveCreateHistory() {
        // given
        User user = createUser("가입 닉네임");
        UserHistory history = UserHistory.ofCreate(user.getEmail(), user.getGender(), user.getBirthDate());

        // when
        userHistoryRepository.save(history);
        Optional<UserHistory> result = userHistoryRepository.findTopByEmailAndChangeTypeOrderByCreatedDateDesc(TEST_EMAIL, ChangeType.CREATE);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(TEST_EMAIL);
        assertThat(result.get().getChangeType()).isEqualTo(ChangeType.CREATE);
    }

    @Test
    @DisplayName("회원탈퇴 이력이 정상적으로 저장된다.")
    void saveDeleteHistory() {
        // given
        User user = createUser("탈퇴 닉네임");
        UserHistory history = UserHistory.ofDelete(user.getEmail(), user.getGender(), user.getBirthDate(), "탈퇴 사유");

        // when
        userHistoryRepository.save(history);
        Optional<UserHistory> result = userHistoryRepository.findTopByEmailAndChangeTypeOrderByCreatedDateDesc(TEST_EMAIL, ChangeType.DELETE);

        // then
        assertThat(result).isPresent();
        assertThat(result.get().getChangeType()).isEqualTo(ChangeType.DELETE);
        assertThat(result.get().getLeaveReason()).isEqualTo("탈퇴 사유");
    }

    @Test
    @DisplayName("존재하지 않는 이메일/타입 조합 조회 시 빈 값을 반환한다.")
    void findNonExistingHistory() {
        // given
        User user = createUser("가입 닉네임");
        UserHistory history = UserHistory.ofCreate(user.getEmail(), user.getGender(), user.getBirthDate());

        // when
        Optional<UserHistory> result = userHistoryRepository.findTopByEmailAndChangeTypeOrderByCreatedDateDesc("nonexistent@email.com", ChangeType.CREATE);

        // then
        assertThat(result).isNotPresent();
    }

    private User createUser(String nickname) {
        return User.builder()
                .email(TEST_EMAIL)
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
