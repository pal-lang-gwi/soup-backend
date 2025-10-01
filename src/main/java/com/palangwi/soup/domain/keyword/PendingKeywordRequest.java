package com.palangwi.soup.domain.keyword;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.user.User;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
