package com.palangwi.soup.common.controller;

import com.palangwi.soup.RestDocsSupport;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class HealthCheckControllerDocsTest extends RestDocsSupport {

    @Override
    protected Object initController() {
        return new HealthCheckController();
    }

    @DisplayName("Health Check API")
    @Test
    void healthCheck() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("health-check"));
    }
}
