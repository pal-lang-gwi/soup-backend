package com.palangwi.soup.repository;

import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.userlog.ChangeType;
import com.palangwi.soup.domain.userlog.UserHistory;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.palangwi.soup.domain.userlog.ChangeType.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@Transactional
public class UserHistoryRepositoryTest {

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    @BeforeEach
    public void setUp() {
        UserHistory userHistory1 = createUserHistory("email1@email.com", DELETE);
        UserHistory userHistory2 = createUserHistory("email2@email.com", CREATE);
        UserHistory userHistory3 = createUserHistory("email2@email.com", DELETE);
        UserHistory userHistory4 = createUserHistory("email3@email.com", CREATE);

        userHistoryRepository.saveAll(List.of(userHistory1, userHistory2, userHistory3, userHistory4));
    }

    @Test
    @DisplayName("최근 탈퇴 이력을 조회할 수 있다.")
    public void getLatestUserDeleteHistory() {
        // given은 beforeEach에서 처리


        // when
        Optional<UserHistory> userHistory = userHistoryRepository.findTopByEmailAndChangeTypeOrderByCreatedDateDesc("email2@email.com", DELETE);

        // then
        assertThat(userHistory.get().getChangeType()).isEqualTo(DELETE);
        assertThat(userHistory.get().getEmail()).isEqualTo("email2@email.com");
    }

    @Test
    @DisplayName("최근 가입 이력을 조회할 수 있다.")
    public void getLatestUserCreateHistory() {
        // given은 beforeEach에서 처리


        // when
        Optional<UserHistory> userHistory = userHistoryRepository.findTopByEmailAndChangeTypeOrderByCreatedDateDesc("email2@email.com", CREATE);

        // then
        assertThat(userHistory.get().getChangeType()).isEqualTo(CREATE);
        assertThat(userHistory.get().getEmail()).isEqualTo("email2@email.com");
    }

    private static UserHistory createUserHistory(String email, ChangeType changeType) {
        return UserHistory.builder()
                .email(email)
                .changeType(changeType)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1999, 11, 1))
                .build();
    }
}
