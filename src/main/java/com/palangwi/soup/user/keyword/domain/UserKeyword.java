package com.palangwi.soup.user.keyword.domain;

import com.palangwi.soup.common.domain.BaseEntity;
import com.palangwi.soup.keyword.domain.Keyword;
import com.palangwi.soup.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_keyword",
        uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "keyword_id"}))
public class UserKeyword extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id")
    private Keyword keyword;

    private boolean subscribed;

    @Builder
    private UserKeyword(User user, Keyword keyword, boolean subscribed) {
        this.user = user;
        this.keyword = keyword;
        this.subscribed = subscribed;
    }

    public void subscribe() {
        this.subscribed = true;
    }

    public void unsubscribe() {
        this.subscribed = false;
    }

    public static UserKeyword create(User user, Keyword keyword) {
        return UserKeyword.builder()
                .user(user)
                .keyword(keyword)
                .subscribed(false)
                .build();
    }
}
