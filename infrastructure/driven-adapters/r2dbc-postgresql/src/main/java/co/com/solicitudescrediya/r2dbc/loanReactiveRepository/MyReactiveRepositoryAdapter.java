package co.com.solicitudescrediya.r2dbc.loanReactiveRepository;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.notification.ChangeStateLoan;
import co.com.solicitudescrediya.r2dbc.entities.LoanEntity;
import co.com.solicitudescrediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Repository
public class MyReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        Loan,
        LoanEntity,
    Integer,
    MyReactiveRepository
> implements LoanRepository {

    public MyReactiveRepositoryAdapter(MyReactiveRepository repository,
                                       ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, Loan.class));
    }

    @Transactional
    @Override
    public Mono<Loan> createLoan(Loan loan) {
        return repository.save(this.toData(loan))
                .map(this::toEntity);
    }

    @Override
    public Flux<Loan> findPendingForReview(List<String> stateUser) {

        List<String> allowedStatuses = List.of(
                "PENDIENTE",
                "RECHAZADO",
                "REVISION_MANUAL"
        );

        List<String> filteredStatuses;

        if (stateUser == null || stateUser.isEmpty())
            filteredStatuses = allowedStatuses;
        else {
            filteredStatuses = stateUser.stream()
                    .filter(allowedStatuses::contains)
                    .toList();
        }
        if (filteredStatuses.isEmpty()) {
            return Flux.empty();
        }
        return repository.findByEstados(filteredStatuses)
                .map(this::toEntity);
    }

    @Override
    public Flux<Loan> findApprovedByEmail(String emailUser) {
        return repository.findApprovedByEmail(emailUser)
                .map(this::toEntity);
    }

    @Override
    public Mono<Loan> findBySolicitudedId(int approvedId) {
        return repository.findBySolicitudeId(approvedId);
    }

    @Override
    public Mono<Loan> updateStatus(int idState, int idApproved) {
        return repository.updateStateByApprovedId(idState, idApproved)
                .map(this::toEntity);
    }
}
