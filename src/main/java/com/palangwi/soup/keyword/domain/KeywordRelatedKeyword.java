package com.palangwi.soup.keyword.domain;

import com.palangwi.soup.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Entity
@Table(
        name = "keyword_related_keyword",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_keyword_relation",
                columnNames = {"keyword_id", "related_keyword_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordRelatedKeyword extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "related_keyword_id", nullable = false)
    private Keyword relatedKeyword;

    public static KeywordRelatedKeyword of(Keyword keyword, Keyword relatedKeyword) {
        return KeywordRelatedKeyword.builder()
                .keyword(keyword)
                .relatedKeyword(relatedKeyword)
                .build();
    }

    @Builder
    public KeywordRelatedKeyword(Keyword keyword, Keyword relatedKeyword) {
        this.keyword = keyword;
        this.relatedKeyword = relatedKeyword;
    }
}
