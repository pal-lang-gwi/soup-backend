package com.palangwi.soup.news.dto;

import com.palangwi.soup.user.domain.User;

import java.time.LocalDateTime;
import java.util.List;

public record DailyNewsMailRequestDto(User user, List<Long> keywordIds, LocalDateTime now) {
}
