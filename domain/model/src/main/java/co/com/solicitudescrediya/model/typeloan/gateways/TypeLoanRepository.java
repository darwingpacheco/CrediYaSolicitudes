package co.com.solicitudescrediya.model.typeloan.gateways;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface TypeLoanRepository {

    Mono<Boolean> findByLoanType(int typeLoanId);

    Mono<Boolean> findValueRange(int idType, BigDecimal amountLoan);
}
