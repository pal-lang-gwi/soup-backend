package com.palangwi.soup.dto.user;

import com.palangwi.soup.domain.user.Gender;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import com.palangwi.soup.security.Role;
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
