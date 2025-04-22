package com.palangwi.soup.domain.user;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.security.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    private Role role;

    private Gender gender;

    private LocalDate birthDate;

    private String providerId;

    private String profileImageUrl;

    private boolean isDeleted;

    private LocalDateTime deletedAt;

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
                 String providerId, String profileImageUrl, boolean isDeleted, LocalDateTime deletedAt) {
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.gender = gender;
        this.birthDate = birthDate;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
        this.isDeleted = isDeleted;
        this.deletedAt = deletedAt;
    }

    public void initializeAdditionalInfo(String email, Role role, Gender gender, LocalDate birthDate) {
        this.email = email;
        this.role = role;
        this.gender = gender;
        this.birthDate = birthDate;
        this.isDeleted = false;
    }

    public void updateUserInfo(String nickname, String profileImageUrl) {
        if (nickname != null) {
            this.nickname = nickname;
        }

        if (profileImageUrl != null) {
            this.profileImageUrl = profileImageUrl;
        }
    }

    public void softDelete() {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
    }
}