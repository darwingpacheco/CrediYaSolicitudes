package co.com.solicitudescrediya.model.stateloan.gateways;

import co.com.solicitudescrediya.model.stateloan.LoanState;
import reactor.core.publisher.Mono;

public interface StateLoanRepository {
    Mono<LoanState> findByStateToUpdate(int stateLoanId);

    Mono<LoanState> findByStateLoan(int stateLoanId);

    Mono<LoanState> getLoanState(int state);

}
