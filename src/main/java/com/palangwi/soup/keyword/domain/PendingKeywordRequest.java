package com.palangwi.soup.keyword.domain;

import com.palangwi.soup.common.domain.BaseEntity;
import com.palangwi.soup.user.domain.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "pending_keyword_request", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "keyword_id"})
})
public class PendingKeywordRequest extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    private Keyword keyword;

    @Builder
    public PendingKeywordRequest(User user, Keyword keyword) {
        this.user = user;
        this.keyword = keyword;
    }

    public static PendingKeywordRequest of(final User user, final Keyword keyword) {
        PendingKeywordRequest request = PendingKeywordRequest.builder()
                .user(user)
                .keyword(keyword)
                .build();
        keyword.getPendingKeywordRequests().add(request);
        return request;
    }

}
