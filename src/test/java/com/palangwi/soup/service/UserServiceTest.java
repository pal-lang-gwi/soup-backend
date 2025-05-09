package com.palangwi.soup.service;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;

import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userlog.ChangeType;
import com.palangwi.soup.domain.userlog.UserHistory;
import com.palangwi.soup.dto.user.UserDeleteRequestDto;
import com.palangwi.soup.dto.user.UserResponseDto;
import com.palangwi.soup.dto.user.UserUpdateRequestDto;
import com.palangwi.soup.exception.user.DuplicateNicknameException;
import com.palangwi.soup.repository.UserHistoryRepository;
import com.palangwi.soup.repository.UserRepository;
import com.palangwi.soup.security.Role;
import com.palangwi.soup.service.user.UserService;
import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserHistoryRepository userHistoryRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
    }

    @DisplayName("유저의 정보를 반환한다.")
    @Test
    void getUserInfo() {
        //given
        User user = userRepository.save(createUser("테스트닉네임"));

        //when
        UserResponseDto result = userService.getUserInfo(user.getId());

        //then
        assertThat(result).isNotNull()
                .extracting("email", "username", "nickname", "role", "gender", "birthDate", "profileImageUrl")
                .containsExactly(
                        user.getEmail(),
                        user.getUsername(),
                        user.getNickname(),
                        user.getRole(),
                        user.getGender(),
                        user.getBirthDate(),
                        user.getProfileImageUrl()
                );
    }

    @DisplayName("유저의 정보를 수정할 수 있다.")
    @Test
    void updateUserInfo() {
        // given
        User user1 = userRepository.save(createUser("수정된닉네임"));

        String newNickname = "수정된닉네임";
        String newImageUrl = "https://updated-image.png";

        UserUpdateRequestDto request = new UserUpdateRequestDto(newNickname, newImageUrl);

        // when
        UserResponseDto result = userService.updateUserInfo(user1.getId(), request);

        // then
        assertThat(result.nickname()).isEqualTo(newNickname);
        assertThat(result.profileImageUrl()).isEqualTo(newImageUrl);
    }

    @DisplayName("닉네임이 중복되면 예외가 발생한다.")
    @Test
    void updateUserInfoWithDuplicateNickname() {
        // given
        User existingUser = userRepository.save(createUser("기존닉네임"));
        User targetUser = userRepository.save(createUser("내닉네임"));

        // 중복된 닉네임으로 수정 시도
        String duplicateNickname = "기존닉네임";
        String newImageUrl = "https://updated-image.png";

        UserUpdateRequestDto request = new UserUpdateRequestDto(duplicateNickname, newImageUrl);

        // when & then
        assertThatThrownBy(() -> userService.updateUserInfo(targetUser.getId(), request))
                .isInstanceOf(DuplicateNicknameException.class)
                .hasMessage("이미 사용 중인 닉네임입니다.");
    }

    @DisplayName("유저가 탈퇴하면 DB에서 삭제되고, 회원로그 테이블에 기록된다.")
    @Test
    void withdrawUser() {
        // given
        User user = userRepository.save(createUser("테스트닉네임"));
        UserDeleteRequestDto request = UserDeleteRequestDto.builder()
                .reason("탈퇴 사유")
                .build();

        // when
        userService.deleteAccount(user.getId(), request);
        Optional<UserHistory> userHistory = userHistoryRepository.findTopByEmailAndChangeTypeOrderByCreatedDateDesc(
                user.getEmail(),
                ChangeType.DELETE);

        // then
        Optional<User> withdrawnUser = userRepository.findByEmail(user.getEmail());

        assertThat(withdrawnUser).isEmpty();
        assertThat(userHistory.isPresent()).isTrue();
        assertThat(userHistory.get().getEmail()).isEqualTo(user.getEmail());
        assertThat(userHistory.get().getLeaveReason()).isEqualTo(request.reason());
        assertThat(userHistory.get().getChangeType()).isEqualTo(ChangeType.DELETE);
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