package com.palangwi.soup.mail.controller;

import com.palangwi.soup.RestDocsSupport;
import com.palangwi.soup.mail.service.MailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class MailControllerDocsTest extends RestDocsSupport {

    private final MailService mailService = mock(MailService.class);

    @Override
    protected Object initController() {
        return new MailController(mailService);
    }

    @DisplayName("메일 트래킹 API")
    @Test
    void trakingMail() throws Exception {
        // given
        byte[] pixel = new byte[]{(byte) 0x89, (byte) 0x50, (byte) 0x4E, (byte) 0x47}; // PNG 픽셀 이미지
        given(mailService.trackingMail(anyLong())).willReturn(pixel);

        // when & then
        mockMvc.perform(get("/api/v1/mails/traking/open/{mailId}", 1L))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Type", "image/png"))
                .andExpect(content().bytes(pixel))
                .andDo(document("mail-tracking",
                        pathParameters(
                                parameterWithName("mailId").description("메일 ID")
                        )
                ));
    }
}

