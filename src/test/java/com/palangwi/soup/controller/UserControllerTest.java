package com.palangwi.soup.controller;

import com.palangwi.soup.dto.user.UserAdditionalInfoRequestDto;
import com.palangwi.soup.dto.user.UserUpdateRequestDto;
import com.palangwi.soup.security.WithMockJwtAuthentication;
import com.palangwi.soup.service.user.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class UserControllerTest extends ControllerTestSupport{

    @MockitoBean
    private UserService userService;

    @Test
    @DisplayName("신규 가입 시 요구사항에 맞게 추가 정보를 입력한다.")
    @WithMockJwtAuthentication
    public void additionalInfo_success() throws Exception {
        // given
        UserAdditionalInfoRequestDto request = UserAdditionalInfoRequestDto.builder()
                .email("init@test.com")
                .gender("MALE")
                .birthDate(LocalDate.of(1996, 12, 2))
                .build();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("신규 가입 시 적절하지 않은 추가 정보를 입력하면 에러를 반환한다.")
    @WithMockJwtAuthentication
    public void additionalInfo_fail() throws Exception {
        // given
        UserAdditionalInfoRequestDto request = UserAdditionalInfoRequestDto.builder()
                .email("init@test.com")
                .gender("WRONG_GENDER")
                .birthDate(LocalDate.of(1996, 12, 2))
                .build();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/init")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("유효하지 않은 성별입니다."));
    }

    @Test
    @DisplayName("회원 정보를 수정한다.")
    @WithMockJwtAuthentication
    public void updateUserInfo_success() throws Exception {
        // given
        UserUpdateRequestDto request = UserUpdateRequestDto.builder()
                .nickname("UpdatedNickname")
                .profileImageUrl("Updated Profile Image URL")
                .build();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).updateUserInfo(1L, request);
    }

    @Test
    @DisplayName("회원 정보 수정 요청이 적절하지 않을 시 에러를 반환한다.")
    @WithMockJwtAuthentication
    public void updateUserInfo_fail() throws Exception {
        // given
        UserUpdateRequestDto request = UserUpdateRequestDto.builder()
                .nickname("띄어쓰기가 포함된 부적절한 닉네임")
                .profileImageUrl("Updated Profile Image URL")
                .build();

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                )
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error.message").value("닉네임은 특수문자를 제외한 문자여야 합니다."));

        verify(userService).updateUserInfo(1L, request);
    }

    @Test
    @DisplayName("내 정보를 조회한다.")
    @WithMockJwtAuthentication(id = 1L)
    public void getMyInfo() throws Exception {
        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).getUserInfo(1L);
    }

    @Test
    @DisplayName("닉네임 중복조회 시 사용 가능한 닉네임일 경우 true를 반환한다.")
    public void validateNickname_available() throws Exception {
        // given
        String nickname = "testNickname";
        given(userService.isAvailableNickname(nickname)).willReturn(true);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/check-nickname")
                .param("nickname", nickname))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(true))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("닉네임 중복조회 시 이미 존재하는 닉네임일 경우 false를 반환한다.")
    public void validateNickname_notAvailable() throws Exception {
        // given
        String nickname = "testNickname";
        given(userService.isAvailableNickname(nickname)).willReturn(false);

        // when // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/users/check-nickname")
                        .param("nickname", nickname))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").value(false))
                .andExpect(jsonPath("$.error").isEmpty());
    }

    @Test
    @DisplayName("회원을 탈퇴 처리한다.")
    @WithMockJwtAuthentication(id = 1L)
    public void userDelete() throws Exception {
        // when // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/users/delete"))
                .andDo(print())
                .andExpect(status().isOk());

        verify(userService).deleteAccount(1L);
    }
}
