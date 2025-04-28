package com.palangwi.soup.repository;

import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.security.Role;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
@Transactional
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void beforeEach() {
        User user1 = createUser("123@gmail.com", "김여준", "테스트닉네임1", false);
        User user2 = createUser("321@gmail.com", "유재광", "테스트닉네임2", true);

        userRepository.saveAll(List.of(user1, user2));
    }

    @DisplayName("사용자의 실명으로 조회하여 사용자가 존재하면 true를 반환한다.")
    @Test
    void existsByUsername() {
        // given은 beforeEach에서 처리

        // when
        boolean exists = userRepository.existsByUsername("김여준");

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("존재하지 않는 실명으로 조회하면 false를 반환한다.")
    @Test
    void existsByUsername_false() {
        // given은 beforeEach에서 처리

        // when
        boolean exists = userRepository.existsByUsername("NOT_EXIST_USERNAME");

        // then
        assertThat(exists).isFalse();
    }

    @DisplayName("사용자의 닉네임으로 조회하여 사용자가 존재하면 true를 반환한다.")
    @Test
    void existsByNickname() {
        // given은 beforeEach에서 처리

        // when
        boolean exists = userRepository.existsByNickname("테스트닉네임1");

        // then
        assertThat(exists).isTrue();
    }

    @DisplayName("사용자의 닉네임으로 조회하여 사용자가 존재하지 않으면 false를 반환한다.")
    @Test
    void existsByNickname_false() {
        // given은 beforeEach에서 처리

        // when
        boolean exists = userRepository.existsByNickname("NOT_EXIST_NICKNAME");

        // then
        assertThat(exists).isFalse();
    }

    @DisplayName("사용자의 실명으로 deleted되지 않은 사용자를 조회한다.")
    @Test
    void findByUsernameAndDeletedFalse1() {
        // given은 beforeEach에서 처리

        // when
        Optional<User> result = userRepository.findByUsernameAndDeletedFalse("김여준");

        // then
        assertThat(result).isPresent()
                .get()
                .satisfies(user -> {
                    assertThat(user.getEmail()).isEqualTo("123@gmail.com");
                    assertThat(user.getUsername()).isEqualTo("김여준");
                    assertThat(user.getNickname()).isEqualTo("테스트닉네임1");
                });
    }

    @DisplayName("사용자의 실명으로 deleted된 사용자를 조회하면 빈 값을 반환한다.")
    @Test
    void findByUsernameAndDeletedFalse2() {
        // given은 beforeEach에서 처리

        // when
        Optional<User> result = userRepository.findByUsernameAndDeletedFalse("유재광");

        // then
        assertThat(result).isEmpty();
    }

    @DisplayName("사용자의 정보가 없을 시 빈 값을 반환한다.")
    @Test
    void findByUsernameException() {
        // given은 beforeEach에서 처리

        //when
        Optional<User> result = userRepository.findByUsernameAndDeletedFalse("NOT_EXIST_USERNAME");

        //then
        assertThat(result).isEmpty();
    }

    private static User createUser(String email, String username, String nickname, boolean deleted) {
        return User.builder()
                .email(email)
                .username(username)
                .nickname(nickname)
                .role(Role.USER)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .profileImageUrl("https://sample.png")
                .deleted(deleted)
                .build();
    }
}