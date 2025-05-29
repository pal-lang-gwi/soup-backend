package com.palangwi.soup.repository.news;

import com.palangwi.soup.domain.news.News;
import org.bson.types.ObjectId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NewsRepository extends MongoRepository<News, ObjectId> {
    List<News> findByCreatedDateBetweenAndKeywordIn(LocalDateTime from, LocalDateTime to, List<String> keywords);

    Page<News> findByCreatedDateBetweenAndKeyword(LocalDateTime from, LocalDateTime to, String keyword, Pageable pageable);

    Page<News> findByCreatedDateBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}
