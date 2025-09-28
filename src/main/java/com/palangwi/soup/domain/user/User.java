package com.palangwi.soup.domain.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.domain.keyword.PendingKeywordRequest;
import com.palangwi.soup.domain.userkeyword.UserKeywords;
import com.palangwi.soup.security.Role;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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

    private Boolean deleted;

    private LocalDateTime deletedAt;

    @Embedded
    private UserKeywords userKeywords = new UserKeywords();

    @OneToMany(mappedBy = "firstRequestUser")
    @JsonIgnore
    private Set<Keyword> requestedKeywords = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PendingKeywordRequest> pendingKeywordRequests = new ArrayList<>();


    public static User createFirstLoginUser(String email, String username, String providerId) {
        return User.builder()
                .email(email)
                .username(username)
                .role(Role.USER)
                .providerId(providerId)
                .profileImageUrl("https://sample.png")
                .deleted(false)
                .build();
    }

    @Builder
    private User(String email, String username, String nickname, Role role, Gender gender, LocalDate birthDate,
                 String providerId, String profileImageUrl, Boolean deleted, LocalDateTime deletedAt) {
        this.email = email;
        this.username = username;
        this.nickname = nickname;
        this.role = role;
        this.gender = gender;
        this.birthDate = birthDate;
        this.providerId = providerId;
        this.profileImageUrl = profileImageUrl;
        this.deleted = deleted;
        this.deletedAt = deletedAt;
    }

    public void initializeAdditionalInfo(String nickname, Gender gender, LocalDate birthDate) {
        this.nickname = nickname;
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

    public void deleteUser(LocalDateTime now) {
        this.deleted = true;
        this.deletedAt = now;
    }
}