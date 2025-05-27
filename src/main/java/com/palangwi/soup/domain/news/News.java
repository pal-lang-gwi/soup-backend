package com.palangwi.soup.domain.news;

import com.palangwi.soup.domain.BaseEntity;
import com.palangwi.soup.domain.keyword.Keyword;
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
public class News extends BaseEntity {

    @Id
    private ObjectId id;

    private String keyword;

    private Summary summary;

    private List<Article> articles;

    private int tokens;

    public News(String keyword, Summary summary, List<Article> articles, int tokens) {
        this.keyword = keyword;
        this.summary = summary;
        this.articles = articles;
        this.tokens = tokens;
    }
}