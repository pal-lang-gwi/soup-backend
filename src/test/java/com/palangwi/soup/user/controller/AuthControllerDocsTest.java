package com.palangwi.soup.user.controller;

import com.palangwi.soup.RestDocsSupport;
import com.palangwi.soup.user.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AuthControllerDocsTest extends RestDocsSupport {

    private final AuthService authService = mock(AuthService.class);

    @Override
    protected Object initController() {
        return new AuthController(authService);
    }

    @DisplayName("토큰 재발급 API")
    @Test
    void refresh() throws Exception {
        // given
        doNothing().when(authService).reissueAccessToken(any(HttpServletRequest.class), any(HttpServletResponse.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/refresh"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth-refresh",
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (null)"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("로그아웃 API")
    @Test
    void logout() throws Exception {
        // given
        doNothing().when(authService).logout(any(HttpServletRequest.class), any(HttpServletResponse.class));

        // when & then
        mockMvc.perform(post("/api/v1/auth/logout"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("auth-logout",
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.NULL).description("응답 데이터 (null)"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }
}
