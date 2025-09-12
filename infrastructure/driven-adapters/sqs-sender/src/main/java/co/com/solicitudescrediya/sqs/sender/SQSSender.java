package co.com.solicitudescrediya.sqs.sender;

import co.com.solicitudescrediya.sqs.sender.config.SQSSenderProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSSender {
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<String> publish(Object event, String queueUrl) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .flatMap(json -> Mono.fromFuture(
                        client.sendMessage(SendMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .messageBody(json)
                                .build())
                ))
                .map(SendMessageResponse::messageId)
                .doOnNext(id -> log.info("Event {} published to {} messageId={}",
                        event.getClass().getSimpleName(), queueUrl, id));
    }
}
