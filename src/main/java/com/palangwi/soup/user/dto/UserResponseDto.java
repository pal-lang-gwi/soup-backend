package com.palangwi.soup.user.dto;

import com.palangwi.soup.common.security.Role;
import com.palangwi.soup.user.domain.Gender;
import com.palangwi.soup.user.domain.User;
import com.palangwi.soup.subscription.domain.UserKeyword;

import java.time.LocalDate;
import java.util.List;

public record UserResponseDto(String email, String username, String nickname, Role role, Gender gender,
                              LocalDate birthDate,
                              String providerId, String profileImageUrl, List<UserKeywordDto> userKeywords) {

    public static UserResponseDto of(User user) {
        return new UserResponseDto(
                user.getEmail(),
                user.getUsername(),
                user.getNickname(),
                user.getRole(),
                user.getGender(),
                user.getBirthDate(),
                user.getProviderId(),
                user.getProfileImageUrl(),
                user.getUserKeywords().getUserKeywordList().stream()
                        .filter(UserKeyword::isSubscribed)
                        .map(uk -> new UserKeywordDto(uk.getKeyword().getName()))
                        .toList()
        );
    }
}
