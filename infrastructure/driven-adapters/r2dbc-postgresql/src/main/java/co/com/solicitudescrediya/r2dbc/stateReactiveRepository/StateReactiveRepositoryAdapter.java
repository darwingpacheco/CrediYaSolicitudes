package co.com.solicitudescrediya.r2dbc.stateReactiveRepository;

import co.com.solicitudescrediya.model.stateloan.LoanState;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.r2dbc.entities.LoanStateEntity;
import co.com.solicitudescrediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.function.Function;

@Repository
public class StateReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanState,
        LoanStateEntity,
        Integer,
        StateReactiveRepository> implements StateLoanRepository {

    protected StateReactiveRepositoryAdapter(StateReactiveRepository repository,
                                             ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanState.class));
    }

    @Override
    @Transactional
    public Mono<Boolean> findByLoanId(int stateLoanId) {
        return this.repository.existsByIdAndName(stateLoanId)
                    .map(this::toEntity)
                    .map(loanId -> true)
                    .defaultIfEmpty(false);
    }
}
