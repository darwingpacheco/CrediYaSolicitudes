package co.com.solicitudescrediya.usecase.loan;

import co.com.solicitudescrediya.model.loan.reviewLoans.ListLoanUserDTO;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.reviewLoans.LoanDetailDTO;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.model.user.User;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import co.com.solicitudescrediya.usecase.loan.paginator.Paginator;
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

                    if (userResponse.statusCode() != 200)
                        return Mono.error(new ConflictException(userResponse.body()));

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

    public Mono<Paginator<ListLoanUserDTO>> getAllLoanRequestsGroupedByUser(String token, int page, int size) {
        return loanRepository.findPendingForReview()
                .switchIfEmpty(Mono.error(new ConflictException(REGIST_NOT_EXIST)))
                .groupBy(Loan::getEmailUser)
                .flatMap(groupedEmail ->
                        groupedEmail.collectList().flatMap(loans -> {
                            String email = groupedEmail.key();

                            Mono<User> userMono = userGateway.getUserByEmail(token, email);

                            // DETAIL LOAN
                            Flux<LoanDetailDTO> loanDetailsFlux = Flux.fromIterable(loans)
                                    .flatMap(petition ->
                                            Mono.zip(
                                                    stateLoanRepository.getLoanState(petition.getStateLoanId()),
                                                    typeLoanRepository.getLoanType(petition.getTypeLoanId())
                                            ).map(tuple -> new LoanDetailDTO(
                                                    tuple.getT2().getNameTypeLoan(),
                                                    tuple.getT1().getStateName(),
                                                    petition.getTermLoan(),
                                                    petition.getAmountLoan()
                                            ))
                                    );

                            // AMOUNT APPROVED
                            Mono<BigDecimal> totalMonthlyDebtMono = loanRepository.findApprovedByEmail(email)
                                    .collectList()
                                    .map(approvedLoans -> approvedLoans.stream()
                                            .map(loan -> loan.getAmountLoan()
                                                    .divide(BigDecimal.valueOf(loan.getTermLoan()), RoundingMode.HALF_UP))
                                            .reduce(BigDecimal.ZERO, BigDecimal::add)
                                    );

                            // ALL LIST
                            return Mono.zip(userMono, loanDetailsFlux.collectList(), totalMonthlyDebtMono)
                                    .map(tuple -> {
                                        User user = tuple.getT1();
                                        List<LoanDetailDTO> loanDetails = tuple.getT2();
                                        BigDecimal totalMonthlyDebt = tuple.getT3();

                                        return new ListLoanUserDTO(
                                                user.getName() + " " + user.getLastName(),
                                                user.getEmail(),
                                                user.getNumberDocument(),
                                                user.getBaseSalary(),
                                                loanDetails,
                                                totalMonthlyDebt
                                        );
                                    });
                        })
                )
                .collectList()
                .map(allResults -> {
                    int totalElements = allResults.size();

                    int fromIndex = Math.min(page * size, totalElements);
                    int toIndex = Math.min(fromIndex + size, totalElements);

                    List<ListLoanUserDTO> pageContent = allResults.subList(fromIndex, toIndex);

                    return new Paginator<>(
                            pageContent,
                            page,
                            size,
                            totalElements
                    );
                });
    }
}