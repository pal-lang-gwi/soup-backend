package com.palangwi.soup.dto.news;

import com.palangwi.soup.domain.news.Article;
import com.palangwi.soup.domain.news.Summary;

import java.util.List;

public record NewsResult (String keyword, Summary summary, List<Article> articles) {
}
