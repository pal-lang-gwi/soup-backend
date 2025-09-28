package com.palangwi.soup.domain.news;

import com.palangwi.soup.domain.BaseEntity;

import lombok.ToString;
import org.springframework.data.annotation.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "news")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@ToString
public class News extends BaseEntity {

    @Id
    private ObjectId id;

    private Long keywordId;

    private String keywordName;

    private Summary summary;

    private List<Article> articles;

    private int tokens;

    public News(Long keywordId, String keywordName, Summary summary, List<Article> articles, int tokens) {
        this.keywordId = keywordId;
        this.keywordName = keywordName;
        this.summary = summary;
        this.articles = articles;
        this.tokens = tokens;
    }
}