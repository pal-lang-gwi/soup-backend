package com.palangwi.soup.dto.user;

import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.validation.EnumValue;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public record UserAdditionalInfoRequestDto(
        @NotNull(message = "닉네임은 필수입니다.")
        String nickname,

        @NotNull(message = "성별은 필수입니다.")
        @EnumValue(enumClass = Gender.class, message = "유효하지 않은 성별입니다.")
        String gender,

        @Past(message = "생년월일은 과거 날짜여야 합니다.")
        @NotNull(message = "생년월일은 필수입니다.")
        LocalDate birthDate
) {
}