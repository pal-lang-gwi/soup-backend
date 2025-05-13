package com.palangwi.soup.domain.news;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Article {
    private String title;
    private String link;
    private String summary;
}