package co.com.solicitudescrediya.usecase.autoValidate;

import co.com.solicitudescrediya.model.autoValidate.AutoValidateCapacity;
import co.com.solicitudescrediya.model.autoValidate.gateways.AutoValidateCapacityRepository;
import co.com.solicitudescrediya.model.gateways.LoanSolicitudeEventPublisher;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class AutoValidateUseCase {

    private final TypeLoanRepository typeLoanRepository;
    private final LoanRepository loanRepository;
    private final LoanSolicitudeEventPublisher solicitudeEventPublisher;
    private final UserGateway userGateway;

    public Mono<AutoValidateCapacity> validateCapacityLoan(Loan loan, String token) {
        return typeLoanRepository.findByLoanType(loan.getTypeLoanId())
                .flatMap(typeloan -> userGateway.getUserByEmail(token, loan.getEmailUser(), "autoValidate")
                        .flatMap(loanApproveds -> loanRepository.findApprovedByNumberDoc(loan.getNumberDocumentUser())
                                .map(approved -> AutoValidateCapacity.builder()
                                        .id(loan.getId())
                                        .numberDocumentUser(loan.getNumberDocumentUser())
                                        .emailUser(loan.getEmailUser())
                                        .baseSalary(loanApproveds.getBaseSalary())
                                        .termLoan(loan.getTermLoan())
                                        .approvedLoansAuto(approved)
                                        .build()
                                )
                                .flatMap(solicitudeEventPublisher::sendDataSqsToValidation)
                        )
                );
    }
}
