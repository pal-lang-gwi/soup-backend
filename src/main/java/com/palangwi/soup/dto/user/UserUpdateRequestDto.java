package com.palangwi.soup.dto.user;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserUpdateRequestDto(

        @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
        @Pattern(regexp = "^[a-zA-Z0-9가-힣]*$", message = "닉네임은 특수문자를 제외한 문자여야 합니다.")
        @Nullable
        String nickname,

        @Size(max = 255, message = "프로필 이미지 URL은 255자 이하로 입력해주세요.")
        @Nullable
        String profileImageUrl) {

}
