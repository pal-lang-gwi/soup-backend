package com.palangwi.soup.admin.controller;

import com.palangwi.soup.RestDocsSupport;
import com.palangwi.soup.admin.dto.keyword.*;
import com.palangwi.soup.admin.keyword.service.AdminKeywordRequestService;
import com.palangwi.soup.keyword.domain.Status;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminRequestedKeywordControllerDocsTest extends RestDocsSupport {

    private final AdminKeywordRequestService adminKeywordService = mock(AdminKeywordRequestService.class);

    @Override
    protected Object initController() {
        return new AdminRequestedKeywordController(adminKeywordService);
    }

    @DisplayName("키워드 요청 목록 조회 API")
    @Test
    void getRequestedKeyword() throws Exception {
        // given
        KeywordDto keywordDto = new KeywordDto(
                1L,
                "테스트 키워드",
                Status.PENDING,
                LocalDateTime.of(2025, 12, 31, 10, 0, 0),
                null
        );
        UserDto userDto = new UserDto(1L, "user@example.com");
        AdminKeywordResponseDto responseDto = new AdminKeywordResponseDto(1L, keywordDto, userDto);

        AdminKeywordResponseListDto response = new AdminKeywordResponseListDto(
                List.of(responseDto),
                1,
                1,
                0
        );
        given(adminKeywordService.getRequestedKeywords(anyString(), any(Pageable.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/admin/keyword-requests")
                        .param("status", "PENDING")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-keyword-requests-list",
                        queryParameters(
                                parameterWithName("status").description("키워드 상태 (PENDING, APPROVED, REJECTED)"),
                                parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.adminKeywordResponseDtos").type(JsonFieldType.ARRAY).description("키워드 요청 목록"),
                                fieldWithPath("data.adminKeywordResponseDtos[].requestId").type(JsonFieldType.NUMBER).description("요청 ID"),
                                fieldWithPath("data.adminKeywordResponseDtos[].keyword").type(JsonFieldType.OBJECT).description("키워드 정보"),
                                fieldWithPath("data.adminKeywordResponseDtos[].keyword.keywordId").type(JsonFieldType.NUMBER).description("키워드 ID"),
                                fieldWithPath("data.adminKeywordResponseDtos[].keyword.name").type(JsonFieldType.STRING).description("키워드 이름"),
                                fieldWithPath("data.adminKeywordResponseDtos[].keyword.status").type(JsonFieldType.STRING).description("키워드 상태"),
                                fieldWithPath("data.adminKeywordResponseDtos[].keyword.requestedDate").type(JsonFieldType.ARRAY).description("요청 일시 (배열 형식: [년, 월, 일, 시, 분, 초, 나노초])"),
                                fieldWithPath("data.adminKeywordResponseDtos[].keyword.rejectionReason").type(JsonFieldType.NULL).description("거절 사유").optional(),
                                fieldWithPath("data.adminKeywordResponseDtos[].requestedBy").type(JsonFieldType.OBJECT).description("요청 사용자"),
                                fieldWithPath("data.adminKeywordResponseDtos[].requestedBy.userId").type(JsonFieldType.NUMBER).description("사용자 ID"),
                                fieldWithPath("data.adminKeywordResponseDtos[].requestedBy.email").type(JsonFieldType.STRING).description("사용자 이메일"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("키워드 승인 API")
    @Test
    void approveRequestedKeyword() throws Exception {
        // given
        ApproveKeywordResponseDto response = new ApproveKeywordResponseDto("테스트 키워드", 5);
        given(adminKeywordService.approveKeyword(anyLong())).willReturn(response);

        // when & then
        mockMvc.perform(post("/api/v1/admin/keyword-requests/{requestId}/approve", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-keyword-requests-approve",
                        pathParameters(
                                parameterWithName("requestId").description("요청 ID")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.keyword").type(JsonFieldType.STRING).description("승인된 키워드"),
                                fieldWithPath("data.requestedUserCnt").type(JsonFieldType.NUMBER).description("요청한 사용자 수"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("키워드 거절 API")
    @Test
    void rejectRequestedKeyword() throws Exception {
        // given
        RejectKeywordResponseDto response = new RejectKeywordResponseDto("테스트 키워드", "부적절한 키워드입니다");
        given(adminKeywordService.rejectKeyword(anyLong(), anyString())).willReturn(response);

        RejectKeywordRequestDto requestDto = new RejectKeywordRequestDto("부적절한 키워드입니다");

        // when & then
        mockMvc.perform(post("/api/v1/admin/keyword-requests/{requestId}/reject", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-keyword-requests-reject",
                        pathParameters(
                                parameterWithName("requestId").description("요청 ID")
                        ),
                        requestFields(
                                fieldWithPath("rejectReason").type(JsonFieldType.STRING).description("거절 사유")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.keyword").type(JsonFieldType.STRING).description("거절된 키워드"),
                                fieldWithPath("data.rejectReason").type(JsonFieldType.STRING).description("거절 사유"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }
}
