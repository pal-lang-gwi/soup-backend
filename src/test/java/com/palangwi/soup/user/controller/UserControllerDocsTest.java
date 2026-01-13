package com.palangwi.soup.user.controller;

import com.palangwi.soup.RestDocsSupport;
import com.palangwi.soup.common.security.Role;
import com.palangwi.soup.keyword.dto.MyKeywordDto;
import com.palangwi.soup.keyword.dto.MyKeywordListResponseDto;
import com.palangwi.soup.keyword.service.KeywordService;
import com.palangwi.soup.user.domain.Gender;
import com.palangwi.soup.user.dto.*;
import com.palangwi.soup.user.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import jakarta.servlet.http.Cookie;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerDocsTest extends RestDocsSupport {

    private final UserService userService = mock(UserService.class);
    private final KeywordService keywordService = mock(KeywordService.class);

    @Override
    protected Object initController() {
        return new UserController(userService, keywordService);
    }

    @DisplayName("사용자 초기 설정 API")
    @Test
    void initAdditionalInfo() throws Exception {
        // given
        UserInitSettingResponseDto response = UserInitSettingResponseDto.builder()
                .userId(1L)
                .email("user@example.com")
                .nickname("테스트유저")
                .role(Role.USER)
                .gender(Gender.MALE)
                .birthDate(LocalDate.of(1990, 1, 1))
                .profileImageUrl("https://example.com/profile.jpg")
                .build();
        given(userService.initAdditionalUserInfo(anyLong(), any())).willReturn(response);

        UserAdditionalInfoRequestDto requestDto = UserAdditionalInfoRequestDto.builder()
                .nickname("테스트유저")
                .gender("MALE")
                .birthDate(LocalDate.of(1990, 1, 1))
                .build();

        // when & then
        mockMvc.perform(post("/api/v1/users/init")
                        .cookie(new Cookie("access_token", "sample-jwt-access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-init",
                        requestCookies(
                                cookieWithName("access_token").description("인증을 위한 JWT Access Token")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("gender").type(JsonFieldType.STRING).description("성별 (MALE, FEMALE)"),
                                fieldWithPath("birthDate").type(JsonFieldType.STRING).description("생년월일 (YYYY-MM-DD)")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("역할"),
                                fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                                fieldWithPath("data.birthDate").type(JsonFieldType.ARRAY).description("생년월일 (배열 형식: [년, 월, 일])"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("사용자 정보 수정 API")
    @Test
    void updateUser() throws Exception {
        // given
        UserResponseDto response = new UserResponseDto(
                "user@example.com",
                "홍길동",
                "수정된닉네임",
                Role.USER,
                Gender.MALE,
                LocalDate.of(1990, 1, 1),
                "kakao_123456",
                "https://example.com/profile2.jpg",
                List.of(new UserKeywordDto("키워드1"))
        );
        given(userService.updateUserInfo(anyLong(), any())).willReturn(response);

        UserUpdateRequestDto requestDto = UserUpdateRequestDto.builder()
                .nickname("수정된닉네임")
                .profileImageUrl("https://example.com/profile2.jpg")
                .build();

        // when & then
        mockMvc.perform(patch("/api/v1/users")
                        .cookie(new Cookie("access_token", "sample-jwt-access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-update",
                        requestCookies(
                                cookieWithName("access_token").description("인증을 위한 JWT Access Token")
                        ),
                        requestFields(
                                fieldWithPath("nickname").type(JsonFieldType.STRING).description("닉네임 (선택, 2-20자)").optional(),
                                fieldWithPath("profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL (선택)").optional()
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("역할"),
                                fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                                fieldWithPath("data.birthDate").type(JsonFieldType.ARRAY).description("생년월일 (배열 형식: [년, 월, 일])"),
                                fieldWithPath("data.providerId").type(JsonFieldType.STRING).description("OAuth 제공자 ID"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                                fieldWithPath("data.userKeywords").type(JsonFieldType.ARRAY).description("구독 키워드 목록"),
                                fieldWithPath("data.userKeywords[].keyword").type(JsonFieldType.STRING).description("키워드 이름"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("사용자 정보 조회 API")
    @Test
    void getUserInfo() throws Exception {
        // given
        UserResponseDto response = new UserResponseDto(
                "user@example.com",
                "홍길동",
                "테스트유저",
                Role.USER,
                Gender.MALE,
                LocalDate.of(1990, 1, 1),
                "kakao_123456",
                "https://example.com/profile.jpg",
                List.of(new UserKeywordDto("키워드1"), new UserKeywordDto("키워드2"))
        );
        given(userService.getUserInfo(anyLong())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/users")
                        .cookie(new Cookie("access_token", "sample-jwt-access-token")))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-info",
                        requestCookies(
                                cookieWithName("access_token").description("인증을 위한 JWT Access Token")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("이메일"),
                                fieldWithPath("data.username").type(JsonFieldType.STRING).description("사용자 이름"),
                                fieldWithPath("data.nickname").type(JsonFieldType.STRING).description("닉네임"),
                                fieldWithPath("data.role").type(JsonFieldType.STRING).description("역할"),
                                fieldWithPath("data.gender").type(JsonFieldType.STRING).description("성별"),
                                fieldWithPath("data.birthDate").type(JsonFieldType.ARRAY).description("생년월일 (배열 형식: [년, 월, 일])"),
                                fieldWithPath("data.providerId").type(JsonFieldType.STRING).description("OAuth 제공자 ID"),
                                fieldWithPath("data.profileImageUrl").type(JsonFieldType.STRING).description("프로필 이미지 URL"),
                                fieldWithPath("data.userKeywords").type(JsonFieldType.ARRAY).description("구독 키워드 목록"),
                                fieldWithPath("data.userKeywords[].keyword").type(JsonFieldType.STRING).description("키워드 이름"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("닉네임 중복 확인 API")
    @Test
    void validateNickname() throws Exception {
        // given
        given(userService.isAvailableNickname(anyString())).willReturn(true);

        // when & then
        mockMvc.perform(get("/api/v1/users/check-nickname")
                        .param("nickname", "사용가능닉네임"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-check-nickname",
                        queryParameters(
                                parameterWithName("nickname").description("확인할 닉네임")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.BOOLEAN).description("사용 가능 여부 (true: 사용 가능, false: 사용 불가)"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("회원 탈퇴 API")
    @Test
    void deleteAccount() throws Exception {
        // given
        UserDeleteRequestDto requestDto = new UserDeleteRequestDto("탈퇴 사유");

        // when & then
        mockMvc.perform(post("/api/v1/users/delete")
                        .cookie(new Cookie("access_token", "sample-jwt-access-token"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-delete",
                        requestCookies(
                                cookieWithName("access_token").description("인증을 위한 JWT Access Token")
                        ),
                        requestFields(
                                fieldWithPath("reason").type(JsonFieldType.STRING).description("탈퇴 사유")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (null)"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("내 키워드 목록 조회 API")
    @Test
    void getMyKeywords() throws Exception {
        // given
        MyKeywordListResponseDto response = new MyKeywordListResponseDto(
                List.of(),
                0,
                0,
                1
        );
        given(keywordService.getMyKeywords(anyLong(), any(Pageable.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/users/me/keywords")
                        .cookie(new Cookie("access_token", "sample-jwt-access-token"))
                        .param("page", "0")
                        .param("size", "20"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("user-my-keywords",
                        requestCookies(
                                cookieWithName("access_token").description("인증을 위한 JWT Access Token")
                        ),
                        queryParameters(
                                parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.myKeywordDtos").type(JsonFieldType.ARRAY).description("내 키워드 목록"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }
}
