package com.palangwi.soup.admin.controller;

import com.palangwi.soup.RestDocsSupport;
import com.palangwi.soup.admin.dto.email.EmailScheduleResponseDto;
import com.palangwi.soup.admin.dto.email.EmailTestResponseDto;
import com.palangwi.soup.mail.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.restdocs.payload.JsonFieldType;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class AdminEmailControllerDocsTest extends RestDocsSupport {

    private final MailService mailService = mock(MailService.class);

    @Override
    protected Object initController() {
        return new AdminEmailController(mailService);
    }

    @DisplayName("이메일 테스트 전송 API")
    @Test
    void testEmail() throws Exception {
        // given
        EmailTestResponseDto response = new EmailTestResponseDto(
                "test@example.com",
                LocalDateTime.of(2025, 12, 31, 10, 30, 0)
        );
        given(mailService.testEmail(anyLong())).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/admin/email/test"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-email-test",
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.email").type(JsonFieldType.STRING).description("수신 이메일 주소"),
                                fieldWithPath("data.sentAt").type(JsonFieldType.ARRAY).description("전송 시각 (배열 형식: [년, 월, 일, 시, 분])"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }

    @DisplayName("이메일 스케줄 조회 API")
    @Test
    void getEmailSchedule() throws Exception {
        // given
        EmailScheduleResponseDto response = new EmailScheduleResponseDto(
                "SUCCESS",
                "2025-12-31T09:00:00",
                "2025-12-31T18:00:00",
                3
        );
        given(mailService.getMailSchedule()).willReturn(response);

        // when & then
        mockMvc.perform(get("/api/v1/admin/email/schedule"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("admin-email-schedule",
                        responseFields(
                                fieldWithPath("success").type(JsonFieldType.BOOLEAN).description("성공 여부"),
                                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터"),
                                fieldWithPath("data.lastStatus").type(JsonFieldType.STRING).description("마지막 실행 상태"),
                                fieldWithPath("data.lastExecutionTime").type(JsonFieldType.STRING).description("마지막 실행 시각"),
                                fieldWithPath("data.nextExecutionTime").type(JsonFieldType.STRING).description("다음 실행 예정 시각"),
                                fieldWithPath("data.activeTasks").type(JsonFieldType.NUMBER).description("활성 작업 수"),
                                fieldWithPath("error").type(JsonFieldType.NULL).description("에러 정보 (성공시 null)")
                        )
                ));
    }
}
