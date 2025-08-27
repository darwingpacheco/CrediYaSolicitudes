package co.com.solicitudescrediya.model.typeloan.gateways;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import reactor.core.publisher.Mono;

public interface TypeLoanRepository {

    Mono<Boolean> findByLoanType(int typeLoanId);
}
