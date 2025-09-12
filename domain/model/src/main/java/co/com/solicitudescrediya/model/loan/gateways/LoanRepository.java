package co.com.solicitudescrediya.model.loan.gateways;

import co.com.solicitudescrediya.model.loan.Loan;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface LoanRepository {
    Mono<Loan> createLoan(Loan loan);

    Flux<Loan> findPendingForReview(List<String> stateUser);

    Flux<Loan>  findApprovedByEmail(String emailUser);

    Mono<Loan> findBySolicitudedId(int approvedId);

    Mono<Loan> updateStatus(int idState, int idApproved);
}
