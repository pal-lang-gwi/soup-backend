package com.palangwi.soup.news.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SQSSendService {
    private final SqsTemplate template;

    @Value("${cloud.aws.sqs.queue-name}")
    private String queueName;

    public SendResult<String> sendMessage(Long keywordId, String keywordName) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> messageMap = Map.of(
                    "keywordId", keywordId.toString(),
                    "keywordName", keywordName
            );

            String message = objectMapper.writeValueAsString(messageMap);

            log.info("Send: {} 키워드 뉴스의 핵심 키워드 추출을 시작합니다. (키워드 ID: {})", keywordName, keywordId);

            return template.send(to -> to
                    .queue(queueName)
                    .payload(message));

        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }
    }
}