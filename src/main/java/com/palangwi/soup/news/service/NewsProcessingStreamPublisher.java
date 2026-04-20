package com.palangwi.soup.news.service;

import com.palangwi.soup.common.utils.RedisHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsProcessingStreamPublisher {

    private final RedisHelper redisHelper;

    @Value("${news.processing.stream-key:news:processing}")
    private String streamKey;

    public void publish(Long keywordId, String keywordName) {
        RecordId recordId = redisHelper.addToStream(
                streamKey,
                Map.of(
                        "keywordId", keywordId.toString(),
                        "keywordName", keywordName
                )
        );

        log.info("📨 Redis Stream 발행 완료 - streamKey: {}, recordId: {}, keywordId: {}",
                streamKey, recordId, keywordId);
    }
}
