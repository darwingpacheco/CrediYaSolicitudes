package co.com.solicitudescrediya.usecase.notifystateloan;

import co.com.solicitudescrediya.model.gateways.LoanSolicitudeEventPublisher;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.notification.ChangeStateLoan;
import co.com.solicitudescrediya.model.notification.EmailNotification;
import co.com.solicitudescrediya.model.reportApprovedLoan.LoanApprovedReview;
import co.com.solicitudescrediya.model.stateloan.LoanState;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.usecase.loan.LoanUseCase;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static co.com.solicitudescrediya.model.util.Constants.*;

@RequiredArgsConstructor
public class NotifyStateLoanUseCase {
    private final LoanRepository loanRepository;
    private final TypeLoanRepository typeLoanRepository;
    private final StateLoanRepository stateLoanRepository;
    private final LoanUseCase loanUseCase;
    private final LoanSolicitudeEventPublisher loanSolicitudeRepository;
    private final LoanSolicitudeEventPublisher solicitudeEventPublisher;

    public Mono<Loan> updateStateLoan(ChangeStateLoan changeStateLoan, String token) {
        return loanRepository.findBySolicitudedId(changeStateLoan.getIdApplication())
                .switchIfEmpty(Mono.error(new ConflictException(USER_NOT_FOUND)))
                .flatMap(loanValid ->
                        loanUseCase.validateUser("updateLoan", loanValid.getEmailUser(), token)
                                .then(Mono.defer(() -> validateStateUpdate(changeStateLoan.getIdState())))
                                .then(Mono.defer(() -> loanRepository.updateStatus(changeStateLoan.getIdApplication(), changeStateLoan.getIdState())))
                )
                .flatMap(loanApproved -> sendEmailNotification(loanApproved)
                        .flatMap(emailNotification -> publishToSQS(emailNotification, loanApproved))
                        .thenReturn(loanApproved)
                )
                .flatMap(sendApprovedDecision -> {
                    Mono<Void> notifyApproved = Mono.empty();
                    if (sendApprovedDecision.getStateLoanId() == 2) {
                        LoanApprovedReview loanApprovedReview = new LoanApprovedReview(
                                LocalDateTime.now().toString(),
                                sendApprovedDecision.getAmountLoan()
                        );
                        notifyApproved = solicitudeEventPublisher.notificationSqsForReview(loanApprovedReview);
                    }

                    return notifyApproved.thenReturn(sendApprovedDecision);
                });
    }

    private Mono<Loan> publishToSQS(EmailNotification emailNotification, Loan loan) {
        return loanSolicitudeRepository.publish(emailNotification)
                .thenReturn(loan);
    }

    private Mono<EmailNotification> sendEmailNotification(Loan loan) {
        return Mono.zip(
                typeLoanRepository.findByLoanType(loan.getTypeLoanId())
                        .switchIfEmpty(Mono.error(new ConflictException(NOT_TYPE_LOAN))),
                stateLoanRepository.findByStateToUpdate(loan.getStateLoanId())
                        .switchIfEmpty(Mono.error(new ConflictException(STATE_NOT_EXIST)))
        ).map(tuple -> {
            LoanType loanType = tuple.getT1();
            LoanState loanState = tuple.getT2();

            return EmailNotification.builder()
                    .idLoan(loan.getId())
                    .status(loanState.getStateName())
                    .email(loan.getEmailUser())
                    .numberDocument(loan.getNumberDocumentUser())
                    .loanAmount(loan.getAmountLoan())
                    .loanType(loanType.getNameTypeLoan())
                    .infoMessage(infoAnalysisToLoan(loanState.getId(), loanType.getNameTypeLoan()))
                    .build();
        });
    }

    private String infoAnalysisToLoan(int stateId, String typeLoanName) {
        return switch (stateId) {
            case 1 ->
                    "Su solicitud de tipo " + typeLoanName + " está siendo revisada. Le notificaremos el resultado pronto.";
            case 2 ->
                    "¡Felicitaciones! Su solicitud de prestamo de tipo " + typeLoanName + " ha sido aprobada. Te notificaremos pronto la información sobre el desembolso.";
            case 3 ->
                    "Lamentamos informarle que su solicitud de tipo " + typeLoanName + " ha sido rechazada. Comunicate a traves de nuestros canales para más información.";
            default ->
                    "Su solicitud de " + typeLoanName + " ha cambiado de estado, por favor acercate pronto a la oficina mas cercana.";
        };
    }

    public Mono<Void> validateStateUpdate(int stateId) {
        return stateLoanRepository.findByStateToUpdate(stateId)
                .switchIfEmpty(Mono.error(new ConflictException(NOT_STATE_LOAN)))
                .then();
    }

}
