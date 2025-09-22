    package com.palangwi.soup.domain.keyword;

    import com.fasterxml.jackson.annotation.JsonIgnore;
    import com.palangwi.soup.domain.BaseEntity;
    import com.palangwi.soup.domain.user.User;
    import com.palangwi.soup.domain.userkeyword.UserKeyword;
    import com.palangwi.soup.exception.keyword.KeywordAlreadyRequestedException;
    import com.palangwi.soup.exception.keyword.KeywordInvalidStatusException;
    import jakarta.annotation.Nullable;
    import jakarta.persistence.*;

    import java.time.LocalDateTime;
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
    @Table(
            name = "keyword",
            uniqueConstraints = @UniqueConstraint(name = "uk_keyword_name", columnNames = "name")
    )
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public class Keyword extends BaseEntity {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String name;

        private String normalizedName;

        private Integer subscribeUserCnt;

        @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<UserKeyword> userKeywords = new ArrayList<>();

        @Enumerated(EnumType.STRING)
        private Source source;

        @Enumerated(EnumType.STRING)
        private Status status;

        @Nullable
        @Column(length = 100)
        private String rejectReason;

        @Nullable
        private LocalDateTime rejectedAt;

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "requested_user_id", nullable = true)
        private User firstRequestUser;

        @OneToMany(mappedBy = "keyword", cascade = CascadeType.ALL, orphanRemoval = true)
        @JsonIgnore
        private List<PendingKeywordRequest> pendingKeywordRequests = new ArrayList<>();

        public static Keyword of(String name, String normalizedName, Source source, User firstRequestUser) {
            return Keyword.builder()
                    .name(name)
                    .normalizedName(normalizedName)
                    .subscribeUserCnt(0)
                    .source(source)
                    .firstRequestUser(firstRequestUser)
                    .status(Status.PENDING)
                    .build();
        }

        @Builder
        private Keyword(String name, String normalizedName, Integer subscribeUserCnt, Source source, Status status, User firstRequestUser) {
            this.name = name;
            this.normalizedName = normalizedName;
            this.subscribeUserCnt = subscribeUserCnt;
            this.source = source;
            this.status = status;
            this.firstRequestUser = firstRequestUser;
        }

        // 추후 Keyword 도메인을 분리하게 된다면, 구독중인 사용자의 수를 어떻게 관리할 지 논의가 필요할 것 같습니다.
        public int getSubscribedCount() {
            return (int) userKeywords.stream()
                    .filter(UserKeyword::isSubscribed)
                    .count();
        }

        public void increaseSubscribedCount() {
            this.subscribeUserCnt++;
        }

        public void decreaseSubscribedCount() {
            this.subscribeUserCnt--;
        }

        public void approve(User firstRequestUser) {
            if (this.status != Status.PENDING) {
                throw new KeywordInvalidStatusException();
            }
            this.status = Status.ACTIVE;
            this.firstRequestUser = firstRequestUser;
        }

        public void reject(String rejectReason, LocalDateTime rejectedAt) {
            this.status = Status.REJECTED;
            this.rejectReason = rejectReason;
            this.rejectedAt = rejectedAt;
        }

        public void remove(String removeReason, LocalDateTime removedAt) {
            this.status = Status.DELETED;
            this.rejectReason = removeReason;
            this.rejectedAt = removedAt;
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