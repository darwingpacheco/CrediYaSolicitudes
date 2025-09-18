package co.com.solicitudescrediya.sqs.sender.loanSolicitude;

import co.com.solicitudescrediya.model.gateways.LoanSolicitudeEventPublisher;
import co.com.solicitudescrediya.model.notification.EmailNotification;
import co.com.solicitudescrediya.sqs.sender.SQSSender;
import co.com.solicitudescrediya.sqs.sender.config.SQSSenderProperties;
import co.com.solicitudescrediya.sqs.sender.loanSolicitude.constants.LoanSolicitudeSQSName;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.logging.Log;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Log4j2
@Component
@RequiredArgsConstructor
public class LoanSolicitudeEventPublisherSQS implements LoanSolicitudeEventPublisher {

    private final SQSSender publisher;
    private final SQSSenderProperties properties;

    @Override
    public Mono<Void> publish(EmailNotification emailNotification) {
        String queueUrl = properties.queues().get("loan-application-state-changed");

        // Agregar logs para depurar
        if (queueUrl == null) {
            log.error("¡ERROR! No se encontró la URL para la cola 'loan-application-state-changed' en las propiedades.");
            return Mono.error(new IllegalStateException("URL de la cola no configurada."));
        }

        log.info("Publicando evento a la cola: {}", queueUrl);

        return publisher.publish(
                emailNotification,
                properties.queues().get(LoanSolicitudeSQSName.LOAN_APPLICATION_STATE_CHANGED.getKey())
        ).then();
    }
}
