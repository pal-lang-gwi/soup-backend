package com.palangwi.soup.domain;

import com.palangwi.soup.security.Role;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;

    private String name;

    private String nickname;

    private Role role;

    private Gender gender;

    private LocalDate birthDate;

    private String providerId;

    private String profileImageUrl;

    public static User createFirstLoginUser(String email, String name, String nickname, Role role, Gender gender, LocalDate birthDate, String providerId, String profileImageUrl) {
        return User.builder()
                .email(email)
                .name(name)
                .nickname(nickname)
                .role(role)
                .gender(gender)
                .birthDate(birthDate)
                .providerId(providerId)
                .profileImageUrl("https://sample.png")
                .build();
    }

    @Builder
    private User(String email, String name, String nickname, Role role, Gender gender, LocalDate birthDate, String providerId, String profileImageUrl) {
        this.email = email;
        this.name = name;
        this.nickname = nickname;
        this.role = role;
        this.gender = gender;
        this.birthDate = birthDate;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
    }
}