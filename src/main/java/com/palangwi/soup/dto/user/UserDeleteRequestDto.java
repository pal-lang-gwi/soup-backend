package com.palangwi.soup.dto.user;

import lombok.Builder;

@Builder
public record UserDeleteRequestDto(String reason) {
}
