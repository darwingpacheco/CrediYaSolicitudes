package co.com.solicitudescrediya.sqs.sender.loanSolicitude.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum LoanSolicitudeSQSName {
    LOAN_APPLICATION_STATE_CHANGED("loan-application-state-changed"),
    LOAN_APPLICATION_AUTO_VALIDATION_REQUESTED("auto-validate-loan"),
    LOAN_APPROVED_TO_REVIEW_LIST("review-approved-loan");
    private final String key;
}
