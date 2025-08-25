package co.com.solicitudescrediya.r2dbc;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.r2dbc.entities.LoanEntity;
import co.com.solicitudescrediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

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
}
