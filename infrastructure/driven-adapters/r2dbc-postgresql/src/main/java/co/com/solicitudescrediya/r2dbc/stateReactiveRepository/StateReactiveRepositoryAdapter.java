package co.com.solicitudescrediya.r2dbc.stateReactiveRepository;

import co.com.solicitudescrediya.model.stateloan.LoanState;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.r2dbc.entities.LoanStateEntity;
import co.com.solicitudescrediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

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
    public Mono<LoanState> findByStateLoan(int stateLoanId) {
        return this.repository.findByStateLoan(stateLoanId)
                    .map(this::toEntity);
    }

    @Override
    @Transactional
    public Mono<LoanState> getLoanState(int stateId) {
        return this.repository.findById(stateId)
                .map(this::toEntity);
    }
}
