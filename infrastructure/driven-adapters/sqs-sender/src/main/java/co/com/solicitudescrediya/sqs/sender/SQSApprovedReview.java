package co.com.solicitudescrediya.sqs.sender;

import co.com.solicitudescrediya.model.autoValidate.AutoValidateCapacity;
import co.com.solicitudescrediya.model.reportApprovedLoan.LoanApprovedReview;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
@Log4j2
@RequiredArgsConstructor
public class SQSApprovedReview {
    private final SqsAsyncClient client;
    private final ObjectMapper objectMapper;

    public Mono<LoanApprovedReview> publish(LoanApprovedReview event, String queueUrl) {
        return Mono.fromCallable(() -> objectMapper.writeValueAsString(event))
                .doOnNext(json -> log.info("Preparando para enviar a SQS. URL de la cola: {}, Cuerpo del mensaje: {}", queueUrl, json))
                .flatMap(json -> Mono.fromFuture(
                        client.sendMessage(SendMessageRequest.builder()
                                .queueUrl(queueUrl)
                                .messageBody(json)
                                .build())
                ))
                .doOnSuccess(response -> log.info("Evento publicado a SQS con éxito. URL de la cola: {}, ID del mensaje: {}", queueUrl, response.messageId()))
                .thenReturn(event);
    }
}
