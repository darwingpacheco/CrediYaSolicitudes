package co.com.solicitudescrediya.usecase.autoValidate;

import co.com.solicitudescrediya.model.autoValidate.AutoValidateCapacity;
import co.com.solicitudescrediya.model.autoValidate.NewStateAutoValidate;
import co.com.solicitudescrediya.model.gateways.LoanSolicitudeEventPublisher;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.reportApprovedLoan.LoanApprovedReview;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static co.com.solicitudescrediya.model.util.Constants.ID_LOAN_NOT_FOUND;

@RequiredArgsConstructor
public class AutoValidateUseCase {

    private final TypeLoanRepository typeLoanRepository;
    private final LoanRepository loanRepository;
    private final LoanSolicitudeEventPublisher solicitudeEventPublisher;
    private final UserGateway userGateway;

    public Mono<AutoValidateCapacity> validateCapacityLoan(Loan loan, String token) {
        return typeLoanRepository.findByLoanType(loan.getTypeLoanId())
                .flatMap(typeLoan -> userGateway.getUserByEmail(token, loan.getEmailUser(), "autoValidate")
                        .flatMap(loanApproveds -> loanRepository.findApprovedByNumberDoc(loan.getNumberDocumentUser())
                                .map(approved -> AutoValidateCapacity.builder()
                                        .id(loan.getId())
                                        .numberDocumentUser(loan.getNumberDocumentUser())
                                        .emailUser(loan.getEmailUser())
                                        .baseSalary(loanApproveds.getBaseSalary())
                                        .termLoan(loan.getTermLoan())
                                        .amountNewLoan(loan.getAmountLoan())
                                        .interestNewLoan(typeLoan.getInterestRateLoan())
                                        .approvedLoansAuto(approved)
                                        .build()
                                )
                                .flatMap(solicitudeEventPublisher::sendDataSqsToValidation)
                        )
                );
    }

    public Mono<NewStateAutoValidate> updateStateLastAutoValidate(NewStateAutoValidate responseAutoValide) {
        return loanRepository.findBySolicitudedId(responseAutoValide.getIdLoan())
                .switchIfEmpty(Mono.error(new ConflictException(ID_LOAN_NOT_FOUND)))
                .flatMap(loan -> loanRepository.updateStatus(responseAutoValide.getIdLoan(), responseAutoValide.getStatus()))
                .flatMap(loan -> validateApprovedState(loan, responseAutoValide));
    }

    public Mono<NewStateAutoValidate> validateApprovedState(Loan loan, NewStateAutoValidate responseAutoValidate) {
        Mono<Void> notifyApproved = Mono.empty();
        if (loan.getStateLoanId() == 2) {
            LoanApprovedReview loanApprovedReview = new LoanApprovedReview(
                    LocalDateTime.now().toString(),
                    loan.getAmountLoan()
            );
            notifyApproved = solicitudeEventPublisher.notificationSqsForReview(loanApprovedReview);
        }

        return notifyApproved.thenReturn(responseAutoValidate);
    }
}
