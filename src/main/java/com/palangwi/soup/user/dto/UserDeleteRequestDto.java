package com.palangwi.soup.user.dto;

import lombok.Builder;

@Builder
public record UserDeleteRequestDto(String reason) {
}
