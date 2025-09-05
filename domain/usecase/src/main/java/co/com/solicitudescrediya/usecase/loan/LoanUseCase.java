package co.com.solicitudescrediya.usecase.loan;

import co.com.solicitudescrediya.model.adapterExceptionApi.ApiError;
import co.com.solicitudescrediya.model.loan.ListLoanUserDTO;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.stateloan.LoanState;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.model.user.User;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import static co.com.solicitudescrediya.model.util.Constants.*;

@RequiredArgsConstructor
public class LoanUseCase {

    private final LoanRepository loanRepository;
    private final StateLoanRepository stateLoanRepository;
    private final TypeLoanRepository typeLoanRepository;
    private final UserGateway userGateway;

    public Mono<Loan> createLoan(Loan loan, String token) {
        String emailUser = loan.getEmailUser();
        return userGateway.existUserByEmail(emailUser, token)
                .flatMap(userResponse -> {

                    if (userResponse.statusCode() != 200) {
                        return Mono.error(new ConflictException(userResponse.body()));
                    }

                    return stateLoanRepository.findByStateLoan(loan.getStateLoanId())
                            .switchIfEmpty(Mono.error(new ConflictException(NOT_STATE_LOAN)))
                            .flatMap(loanType -> typeLoanRepository.findByLoanType(loan.getTypeLoanId()))
                            .switchIfEmpty(Mono.error(new ConflictException(NOT_TYPE_LOAN)))
                            .flatMap(amountType -> typeLoanRepository.findValueRange(amountType.getId(), loan.getAmountLoan()))
                            .switchIfEmpty(Mono.error(new ConflictException(AMOUNT_NOT_RANGE)))
                            .flatMap(exist -> {
                                loan.setStateLoanId(1);
                                return loanRepository.createLoan(loan);
                            });
                });
    }

    public Flux<ListLoanUserDTO> getAllLoanRequestsForReview(String token, int page, int size) {
        return loanRepository.findPendingForReview()
                .switchIfEmpty(Mono.error(new ConflictException("NO EXISTEN REGISTROS AUN")))
                .flatMap(petition -> Mono.zip(
                        stateLoanRepository.getAllLoanState(petition.getStateLoanId()),
                        typeLoanRepository.getAllLoanType(petition.getTypeLoanId()),
                        userGateway.getAllUsers(token, petition.getEmailUser()),
                        loanRepository.findApprovedByEmail(petition.getEmailUser()).collectList()
                ).map(tuple -> {
                    LoanState status = tuple.getT1();
                    LoanType loanType = tuple.getT2();
                    User user = tuple.getT3();
                    List<Loan> approved = tuple.getT4();

                    BigDecimal totalMonthlyDebt = approved.stream()
                            .map(loanList -> loanList.getAmountLoan()
                                    .divide(BigDecimal.valueOf(loanList.getTermLoan()), RoundingMode.HALF_UP))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

                    return new ListLoanUserDTO(
                            user.getName() + " " + user.getLastName(),
                            user.getEmail(),
                            user.getNumberDocument(),
                            user.getBaseSalary(),
                            loanType.getNameTypeLoan(),
                            status.getStateName(),
                            petition.getTermLoan(),
                            petition.getAmountLoan(),
                            approved.size(),
                            totalMonthlyDebt
                    );
                }));
    }
}