package com.palangwi.soup.domain.keyword;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Entity
@Table(name = "keyword")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Keyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String normalizedName;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<UserKeyword> userKeywords = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Source source;

    @Enumerated(EnumType.STRING)
    private Status status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_user_id", nullable = true)
    private User requestedUser;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PendingKeywordRequest> pendingKeywordRequests = new ArrayList<>();

    public static Keyword of(String name, String normalizedName, Source source, User user) {
        Keyword keyword = Keyword.builder()
                .name(name)
                .normalizedName(normalizedName)
                .source(source)
                .status(Status.PENDING)
                .build();
        keyword.setRequestedUser(user);
        return keyword;
    }

    @Builder
    private Keyword(String name, String normalizedName, Source source, Status status) {
        this.name = name;
        this.normalizedName = normalizedName;
        this.source = source;
        this.status = status;
    }

    // 추후 Keyword 도메인을 분리하게 된다면, 구독중인 사용자의 수를 어떻게 관리할 지 논의가 필요할 것 같습니다.
    public int getSubscribedCount() {
        return (int) userKeywords.stream()
                .filter(UserKeyword::isSubscribed)
                .count();
    }

    public void setRequestedUser(User user) {
        if (this.requestedUser == user) return;
        this.requestedUser = user;
        if (user != null && !user.getRequestedKeywords().contains(this)) {
            user.getRequestedKeywords().add(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Keyword keyword = (Keyword) o;
        return id != null && id.equals(keyword.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}