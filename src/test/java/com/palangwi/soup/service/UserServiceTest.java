package com.palangwi.soup.service;

import com.palangwi.soup.domain.Gender;
import com.palangwi.soup.domain.User;
import com.palangwi.soup.dto.user.UserResponseDto;
import com.palangwi.soup.repository.UserRepository;
import com.palangwi.soup.security.Role;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.time.LocalDate;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
class UserServiceTest {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @DisplayName("유저의 정보를 반환한다.")
    @Test
     void getUserInfo() {
        //given
        User user = userRepository.save(creatUser());

        //when
        UserResponseDto result = userService.getUserInfo(user.getId());

        //then
        assertThat(result).isNotNull()
                .extracting("nickname", "profileImageUrl")
                .containsExactly(
                        user.getNickname(),
                        user.getProfileImageUrl()
                );
    }

    private User creatUser() {
        return User.builder()
                .email("asdf1234@naver.com")
                .username("테스트")
                .nickname("테스트닉네임")
                .role(Role.USER)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1999, 9, 9))
                .providerId("구글")
                .profileImageUrl("https://sample-image.png")
                .build();
    }

}