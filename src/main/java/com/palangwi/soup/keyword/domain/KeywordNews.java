package com.palangwi.soup.keyword.domain;

import com.palangwi.soup.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Entity
@Table(
        name = "keyword_news",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_keyword_news",
                columnNames = {"news_id", "keyword_id"}
        )
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class KeywordNews extends BaseEntity {

    // MongoDB document_id(ObjectId)를 그대로 PK로 사용
    @Id
    @Column(name = "news_id", nullable = false, length = 24)
    private String newsId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "keyword_id", nullable = false)
    private Keyword keyword;

    public static KeywordNews of(String newsId, Keyword keyword) {
        KeywordNews entity = new KeywordNews();
        entity.newsId = newsId;
        entity.keyword = keyword;
        return entity;
    }
}