package com.palangwi.soup.domain.keyword;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.user.User;
import com.palangwi.soup.domain.userkeyword.UserKeyword;
import com.palangwi.soup.exception.keyword.KeywordAlreadyRequestedException;
import com.palangwi.soup.exception.keyword.KeywordInvalidStatusException;
import jakarta.annotation.Nullable;
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

    @Nullable
    @Column(length = 100)
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_user_id", nullable = true)
    private User requestedUser;

    @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<PendingKeywordRequest> pendingKeywordRequests = new ArrayList<>();

    public static Keyword of(String name, String normalizedName, Source source, User requestedUser) {
        return Keyword.builder()
                .name(name)
                .normalizedName(normalizedName)
                .source(source)
                .requestedUser(requestedUser)
                .status(Status.PENDING)
                .build();
    }

    @Builder
    private Keyword(String name, String normalizedName, Source source, Status status, User requestedUser) {
        this.name = name;
        this.normalizedName = normalizedName;
        this.source = source;
        this.status = status;
        this.requestedUser = requestedUser;
    }

    // 추후 Keyword 도메인을 분리하게 된다면, 구독중인 사용자의 수를 어떻게 관리할 지 논의가 필요할 것 같습니다.
    public int getSubscribedCount() {
        return (int) userKeywords.stream()
                .filter(UserKeyword::isSubscribed)
                .count();
    }

    public void approve(User firstRequestUser) {
        if (this.status != Status.PENDING) {
            throw new KeywordInvalidStatusException();
        }
        this.status = Status.ACTIVE;
        this.rejectionReason = null;
        this.requestedUser = firstRequestUser;
    }

    public void reject(String rejectionReason) {
        this.status = Status.REJECTED;
        this.rejectionReason = rejectionReason;
    }

    public boolean alreadyRequested(User user) {
        return pendingKeywordRequests.stream()
                .anyMatch(req -> req.getUser().equals(user));
    }

    public void addPendingRequestIfNotExists(User user) {
        if (!alreadyRequested(user)) {
            pendingKeywordRequests.add(
                    PendingKeywordRequest.builder().keyword(this).user(user).build()
            );
        } else {
            throw new KeywordAlreadyRequestedException();
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