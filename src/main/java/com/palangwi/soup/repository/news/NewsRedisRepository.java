package com.palangwi.soup.repository.news;

import com.palangwi.soup.dto.news.NewsResult;
import com.palangwi.soup.utils.RedisHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class NewsRedisRepository {

    private static final String PREFIX = "news:latest:";
    private final RedisHelper redisHelper;

    public void save(Long keywordId, NewsResult newsResult, long ttlSeconds) {
        redisHelper.set(PREFIX + keywordId, newsResult, ttlSeconds);
    }

    public Object find(Long keywordId) {
        return redisHelper.get(PREFIX + keywordId);
    }

    public void delete(Long keywordId) {
        redisHelper.delete(PREFIX + keywordId);
    }
}