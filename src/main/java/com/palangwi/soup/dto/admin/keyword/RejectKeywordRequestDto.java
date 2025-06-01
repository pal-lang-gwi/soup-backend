package com.palangwi.soup.dto.admin.keyword;

import jakarta.validation.constraints.NotEmpty;

public record RejectKeywordRequestDto(
        @NotEmpty(message = "반려 사유는 필수 입력 항목입니다.")
        String rejectReason) {
}
