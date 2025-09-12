package co.com.solicitudescrediya.sqs.sender.loanSolicitude.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoanSolicitudeSQSName {
    LOAN_APPLICATION_STATE_CHANGED("loanApplicationStateChanged"),
    LOAN_APPLICATION_AUTO_VALIDATION_REQUESTED("loanApplicationAutoValidationRequested");
    private final String key;
}
