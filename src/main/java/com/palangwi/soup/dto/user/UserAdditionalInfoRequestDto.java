package com.palangwi.soup.dto.user;

import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.security.Role;
import com.palangwi.soup.validation.EnumValue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;

import java.time.LocalDate;

public record UserAdditionalInfoRequestDto(
        @Email(message = "이메일 형식이 올바르지 않습니다.")
        @NotNull(message = "이메일은 필수입니다.")
        String email,

        @NotNull(message = "성별은 필수입니다.")
        @EnumValue(enumClass = Gender.class, message = "유효하지 않은 성별입니다.")
        String gender,

        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        @NotNull(message = "생년월일은 필수입니다.")
        LocalDate birthDate
) {
}