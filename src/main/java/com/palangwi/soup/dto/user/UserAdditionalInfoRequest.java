package com.palangwi.soup.dto.user;

import com.palangwi.soup.domain.Gender;
import com.palangwi.soup.security.Role;

import java.time.LocalDate;

public record UserAdditionalInfoRequest(
        String email,
        Role role,
        Gender gender,
        LocalDate birthDate
) {
}