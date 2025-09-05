package co.com.solicitudescrediya.model.typeloan.gateways;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface TypeLoanRepository {

    Mono<LoanType> findByLoanType(int typeLoanId);

    Mono<LoanType> findValueRange(int idType, BigDecimal amountLoan);

    Mono<LoanType> getAllLoanType(int idType);
}
