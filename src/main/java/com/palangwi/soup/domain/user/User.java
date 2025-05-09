package com.palangwi.soup.domain.user;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.userkeyword.UserKeywords;
import com.palangwi.soup.security.Role;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String username;

    private String nickname;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Enumerated(EnumType.STRING)
    private Gender gender;

    private LocalDate birthDate;

    private String providerId;

    private String profileImageUrl;

    @Embedded
    private UserKeywords userKeywords = new UserKeywords();

    public static User createFirstLoginUser(String username, String nickname, String providerId) {
        return User.builder()
                .username(username)
                .nickname(nickname)
                .providerId(providerId)
                .profileImageUrl("https://sample.png")
                .build();
    }

    @Builder
    private User(String email, String username, String nickname, Role role, Gender gender, LocalDate birthDate,
                 String providerId, String profileImageUrl) {
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.gender = gender;
        this.birthDate = birthDate;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
    }

    public void initializeAdditionalInfo(String email, Gender gender, LocalDate birthDate) {
        this.email = email;
        this.role = Role.USER;
        this.gender = gender;
        this.birthDate = birthDate;
    }

    public void updateUserInfo(String nickname, String profileImageUrl) {
        if (nickname != null) {
            this.nickname = nickname;
        }

        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }
}