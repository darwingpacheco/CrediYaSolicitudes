package co.com.solicitudescrediya.model.stateloan.gateways;

import reactor.core.publisher.Mono;

public interface StateLoanRepository {
    Mono<Boolean> findByLoanId(int stateLoanId);
}
