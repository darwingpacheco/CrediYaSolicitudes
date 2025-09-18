package co.com.solicitudescrediya.model.gateways;

import co.com.solicitudescrediya.model.autoValidate.AutoValidateCapacity;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.notification.EmailNotification;
import reactor.core.publisher.Mono;

public interface LoanSolicitudeEventPublisher {
    Mono<Void> publish(EmailNotification emailNotification);

    Mono<AutoValidateCapacity> sendDataSqsToValidation(AutoValidateCapacity autoValidateCapacity);
}
