package com.palangwi.soup.dto.mail;

import java.util.List;

public record UserMailInfoDto(Long userId, String email, String username, List<String> subscribedKeywords) {
}
