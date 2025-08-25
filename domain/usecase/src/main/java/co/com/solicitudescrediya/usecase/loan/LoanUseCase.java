package co.com.solicitudescrediya.usecase.loan;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanUseCase {

    private final LoanRepository loanRepository;

    public Mono<Loan> createLoan(Loan loan) {
        loan.setIdEstado(1);
        return loanRepository.createLoan(loan);
    }
}
