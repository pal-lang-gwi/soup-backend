package com.palangwi.soup.common.config;

import com.palangwi.soup.IntegrationTestSupport;
import com.palangwi.soup.common.security.WithMockJwtAuthentication;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SecurityConfigTest extends IntegrationTestSupport {

    @Test
    @DisplayName("관리자 API는 일반 사용자 접근을 거부한다.")
    @WithMockJwtAuthentication(role = "ROLE_USER")
    void adminApiRequiresAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/admin/keyword")
                        .param("status", "ACTIVE"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("관리자 API는 관리자 접근을 허용한다.")
    @WithMockJwtAuthentication(role = "ROLE_ADMIN")
    void adminApiAllowsAdminRole() throws Exception {
        mockMvc.perform(get("/api/v1/admin/keyword")
                        .param("status", "ACTIVE"))
                .andExpect(status().isOk());
    }
}
