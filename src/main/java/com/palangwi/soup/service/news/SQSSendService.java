package com.palangwi.soup.service.news;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.awspring.cloud.sqs.operations.SendResult;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class SQSSendService {
    private final SqsTemplate template;

    @Value("${cloud.aws.sqs.queue-name}")
    private String queueName;

    @Value("${cloud.aws.sqs.queue-type}")
    private String queueType;

    public SendResult<String> sendMessage(Long keywordId, String keywordName) {
        try {
            String keywordIdToString = keywordId.toString();

            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> messageMap = new HashMap<>();
            messageMap.put("keywordId", keywordIdToString);
            messageMap.put("keywordName", keywordName);

            String message = objectMapper.writeValueAsString(messageMap);

            log.info(
                    String.format("Send : %s 키워드 뉴스의 핵심 키워드 추출을 시작합니다. AI서버로 정보를 전달합니다: (키워드 ID : %s)",
                            keywordName, keywordId));

            if(queueType.equals("fifo")){
                //FIFO인 경우 메시지에 groupID와 DeduplicationID 지정
                return template.send(to -> to
                        .queue(queueName)
                        .messageGroupId(keywordIdToString)
                        .messageDeduplicationId(keywordIdToString)
                        .payload(message));
            }
            else{
                return template.send(to -> to
                        .queue(queueName)
                        .payload(message));
            }
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON 직렬화 실패", e);
        }

    }
}
