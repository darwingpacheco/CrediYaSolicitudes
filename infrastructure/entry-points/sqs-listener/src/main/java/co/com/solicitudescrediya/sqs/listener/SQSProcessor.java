package co.com.solicitudescrediya.sqs.listener;

import co.com.solicitudescrediya.model.autoValidate.NewStateAutoValidate;
import co.com.solicitudescrediya.usecase.autoValidate.AutoValidateUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
@Log4j2
public class SQSProcessor implements Function<Message, Mono<Void>> {
    private final AutoValidateUseCase autoValidateUseCase;
    private final ObjectMapper objectMapper;

    @Override
    public Mono<Void> apply(Message message) {
        log.info("Request obtenida del SQS responseAutoValidate-sqs: {}", message.body());

        return Mono.fromCallable(() -> {
                    try {
                        return objectMapper.readValue(message.body(), NewStateAutoValidate.class);
                    } catch (Exception e) {
                        log.error("Error al parsear JSON response autovalidate: {}", e.getMessage());
                        throw new RuntimeException("Error al parsear JSON autoValidation response");
                    }
                })
                .flatMap(responseAutoValide -> {
                    log.info("Response data autoValidation: idLoan={}, state={}, decision={}",
                            responseAutoValide.getIdLoan(),
                            responseAutoValide.getStatus(),
                            responseAutoValide.getDecision());
                    return autoValidateUseCase.updateStateLastAutoValidate(responseAutoValide);
                })
                .then();
    }
}
