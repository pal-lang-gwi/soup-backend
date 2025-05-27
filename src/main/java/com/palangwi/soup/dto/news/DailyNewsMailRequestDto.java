package com.palangwi.soup.dto.news;

import com.palangwi.soup.domain.user.User;

import java.time.LocalDateTime;
import java.util.List;

public record DailyNewsMailRequestDto(User user, List<String> keywords, LocalDateTime now) {
}
