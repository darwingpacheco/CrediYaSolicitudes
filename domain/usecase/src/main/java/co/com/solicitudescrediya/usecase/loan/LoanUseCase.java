package co.com.solicitudescrediya.usecase.loan;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanUseCase {

    private final LoanRepository loanRepository;
    private final StateLoanRepository stateLoanRepository;
    private final TypeLoanRepository typeLoanRepository;
    private final UserGateway userGateway;

    public Mono<Loan> createLoan(Loan loan) {
        String emailUser = loan.getEmailUser();
        return userGateway.existUserByEmail(emailUser)
                .flatMap(userExists -> {
                    if (!userExists)
                        return Mono.error(new ConflictException("El usuario: " + emailUser + " no existe"));

                    loan.setStateLoanId(1);
                    Mono<Boolean> stateCheck = stateLoanRepository.findByLoanId(loan.getStateLoanId());
                    Mono<Boolean> typeCheck = typeLoanRepository.findByLoanType(loan.getTypeLoanId());

                    return Mono.zip(stateCheck, typeCheck)
                            .flatMap(tuple -> {
                                Boolean stateExists = tuple.getT1();
                                Boolean typeExists = tuple.getT2();

                                if (!stateExists)
                                    return Mono.error(new ConflictException("En este momento no es posible asignarte un estado de préstamo"));

                                if (!typeExists)
                                    return Mono.error(new ConflictException("No existe el tipo de préstamo solicitado"));

                                return loanRepository.createLoan(loan);
                            });
                });
    }
}
