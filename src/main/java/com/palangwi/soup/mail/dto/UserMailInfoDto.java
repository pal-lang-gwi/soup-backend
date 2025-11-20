package com.palangwi.soup.mail.dto;

import java.util.List;

public record UserMailInfoDto(Long userId, String email, String username, List<String> subscribedKeywords) {
}
