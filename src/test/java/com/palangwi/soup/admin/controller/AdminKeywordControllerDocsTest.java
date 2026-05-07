package com.palangwi.soup.admin.controller;

import com.palangwi.soup.RestDocsSupport;
import com.palangwi.soup.admin.dto.keyword.AddKeywordResponseDto;
import com.palangwi.soup.admin.dto.keyword.RemoveKeywordRequestDto;
import com.palangwi.soup.admin.dto.keyword.RemoveKeywordResponseDto;
import com.palangwi.soup.admin.keyword.service.AdminKeywordService;
import com.palangwi.soup.keyword.dto.KeywordListResponseDto;
import com.palangwi.soup.keyword.dto.KeywordResponseDto;
import com.palangwi.soup.keyword.dto.RequestKeywordRequestDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.restdocs.payload.JsonFieldType;

import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminKeywordControllerDocsTest extends RestDocsSupport {

    private final AdminKeywordService adminKeywordService = mock(AdminKeywordService.class);

    @Override
    protected Object initController() {
        return new AdminKeywordController(adminKeywordService);
    }

    @DisplayName("전체 키워드 목록 조회 API")
    @Test
    void getAllKeywords() throws Exception {
        // given
        KeywordResponseDto keywordDto = new KeywordResponseDto(1L, "테스트 키워드", "테스트키워드");
        KeywordListResponseDto response = new KeywordListResponseDto(
                List.of(keywordDto),
                1,
                1,
                0
        );
        given(adminKeywordService.getAllKeywordList(nullable(String.class), any(Pageable.class))).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/admin/keyword")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-keyword-list",
                        queryParameters(
                                parameterWithName("status").optional().description("키워드 상태 (ACTIVE, INACTIVE, DELETED, PENDING, REJECTED). 생략 시 전체 조회"),
                                parameterWithName("page").description("페이지 번호 (0부터 시작)"),
                                parameterWithName("size").description("페이지 크기")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.keywordResponseDtos").type(JsonFieldType.ARRAY).description("키워드 목록"),
                                fieldWithPath("data.keywordResponseDtos[].id").type(JsonFieldType.NUMBER).description("키워드 ID"),
                                fieldWithPath("data.keywordResponseDtos[].name").type(JsonFieldType.STRING).description("키워드 이름"),
                                fieldWithPath("data.keywordResponseDtos[].normalizedName").type(JsonFieldType.STRING).description("정규화된 키워드 이름"),
                                fieldWithPath("data.totalElements").type(JsonFieldType.NUMBER).description("전체 요소 수"),
                                fieldWithPath("data.totalPages").type(JsonFieldType.NUMBER).description("전체 페이지 수"),
                                fieldWithPath("data.currentPage").type(JsonFieldType.NUMBER).description("현재 페이지"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("키워드 추가 API")
    @Test
    void addKeyword() throws Exception {
        // given
        AddKeywordResponseDto response = new AddKeywordResponseDto("새로운 키워드");
        given(adminKeywordService.addKeyword(anyString())).willReturn(response);

        RequestKeywordRequestDto requestDto = new RequestKeywordRequestDto("새로운 키워드");

        // when & then
        mockMvc.perform(post("/api/v1/admin/keyword/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-keyword-add",
                        requestFields(
                                fieldWithPath("keyword").type(JsonFieldType.STRING).description("추가할 키워드")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.keyword").type(JsonFieldType.STRING).description("추가된 키워드"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("키워드 삭제 API")
    @Test
    void removeKeyword() throws Exception {
        // given
        RemoveKeywordResponseDto response = new RemoveKeywordResponseDto("테스트 키워드", "사용되지 않는 키워드입니다");
        given(adminKeywordService.removeKeyword(anyLong(), anyString())).willReturn(response);

        RemoveKeywordRequestDto requestDto = new RemoveKeywordRequestDto(1L, "사용되지 않는 키워드입니다");

        // when & then
        mockMvc.perform(post("/api/v1/admin/keyword/remove")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-keyword-remove",
                        requestFields(
                                fieldWithPath("keywordId").type(JsonFieldType.NUMBER).description("삭제할 키워드 ID"),
                                fieldWithPath("removeReason").type(JsonFieldType.STRING).description("삭제 사유")
                        ),
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.keyword").type(JsonFieldType.STRING).description("삭제된 키워드"),
                                fieldWithPath("data.removeReason").type(JsonFieldType.STRING).description("삭제 사유"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }
}
