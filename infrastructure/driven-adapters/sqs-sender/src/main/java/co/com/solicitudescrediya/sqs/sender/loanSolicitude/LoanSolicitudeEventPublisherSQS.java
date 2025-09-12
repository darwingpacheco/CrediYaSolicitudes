package co.com.solicitudescrediya.sqs.sender.loanSolicitude;

import co.com.solicitudescrediya.model.gateways.LoanSolicitudeEventPublisher;
import co.com.solicitudescrediya.model.notification.EmailNotification;
import co.com.solicitudescrediya.sqs.sender.SQSSender;
import co.com.solicitudescrediya.sqs.sender.config.SQSSenderProperties;
import co.com.solicitudescrediya.sqs.sender.loanSolicitude.constants.LoanSolicitudeSQSName;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class LoanSolicitudeEventPublisherSQS implements LoanSolicitudeEventPublisher {

    private final SQSSender publisher;
    private final SQSSenderProperties properties;

    @Override
    public Mono<Void> publish(EmailNotification emailNotification) {
        return publisher.publish(
                emailNotification,
                properties.queues().get(LoanSolicitudeSQSName.LOAN_APPLICATION_STATE_CHANGED.getKey())
        ).then();
    }
}
