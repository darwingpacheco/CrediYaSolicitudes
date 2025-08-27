package co.com.solicitudescrediya.usecase.loan;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanUseCase {

    private final LoanRepository loanRepository;
    private final StateLoanRepository stateLoanRepository;
    private final TypeLoanRepository typeLoanRepository;
    private Mono<Boolean> stateCheck, typeCheck, validateUser;

    public Mono<Loan> createLoan(Loan loan) {
        loan.setStateLoanId(1);

        stateCheck = stateLoanRepository.findByLoanId(loan.getStateLoanId());
        typeCheck = typeLoanRepository.findByLoanType(loan.getTypeLoanId());
        validateUser = loanRepository.getUserByDocument(loan.getNumberDocumentUser());

        return Mono.zip(stateCheck, typeCheck, validateUser)
                .flatMap(tuple -> {
                    Boolean stateExists = tuple.getT1();
                    Boolean typeExists = tuple.getT2();
                    Boolean existUser = tuple.getT3();

                    if (existUser)
                        return Mono.error(new ConflictException("En este momento ya tienes una solicitud asignada"));

                    if (!stateExists)
                        return Mono.error(new ConflictException("En este momento no es posible asignarte un estado de prestamo"));

                    if (!typeExists)
                        return Mono.error(new ConflictException("No existe el tipo de préstamo solicitado"));

                    return loanRepository.createLoan(loan);
                });
    }
}
