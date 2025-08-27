package co.com.solicitudescrediya.model.loan.gateways;

import co.com.solicitudescrediya.model.loan.Loan;
import reactor.core.publisher.Mono;

public interface LoanRepository {
    Mono<Loan> createLoan(Loan loan);

    Mono<Boolean> getUserByDocument(String numberDocumetUser);
}
