package com.palangwi.soup.service.news;

import com.palangwi.soup.domain.keyword.Keyword;
import com.palangwi.soup.repository.keyword.KeywordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NewsService {

    private final NewsRepository newsRepository;
    private final KeywordRepository keywordRepository;

    public void collectNews() {
        List<Keyword> keywords = keywordRepository.findAll();

        for (Keyword keyword : keywords) {

        }
    }
}