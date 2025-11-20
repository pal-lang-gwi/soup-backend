package com.palangwi.soup.news.domain;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    private String title;
    private String link;
    private String summary;

    public Article(String title, String link, String summary) {
        this.title = title;
        this.link = link;
        this.summary = summary;
    }
}