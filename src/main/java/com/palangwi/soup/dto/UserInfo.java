package com.palangwi.soup.dto;

import com.palangwi.soup.domain.Gender;
import com.palangwi.soup.security.Role;

import java.time.LocalDate;

public record UserInfo(String email, String name, String nickname, Role role, Gender gender, LocalDate birthDate, String providerId, String profileImageUrl) {
}