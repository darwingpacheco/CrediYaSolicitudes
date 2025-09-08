package co.com.solicitudescrediya.r2dbc.typeReactiveRepository;

import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.r2dbc.entities.LoanTypeEntity;
import co.com.solicitudescrediya.r2dbc.helper.ReactiveAdapterOperations;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

@Repository
public class TypeReactiveRepositoryAdapter extends ReactiveAdapterOperations<
        LoanType,
        LoanTypeEntity,
        Integer,
        TypeReactiveRepository> implements TypeLoanRepository {

    protected TypeReactiveRepositoryAdapter(TypeReactiveRepository repository,
                                             ObjectMapper mapper) {
        super(repository, mapper, d -> mapper.map(d, LoanType.class));
    }

    @Override
    @Transactional
    public Mono<LoanType> findByLoanType(int typeLoanId) {
        return this.repository.existLoanTypeById(typeLoanId)
                .map(this::toEntity);
    }

    @Override
    public Mono<LoanType> findValueRange(int idType, BigDecimal amountLoan) {
        return this.repository.validateRangeById(idType, amountLoan)
                .map(this::toEntity);
    }

    @Override
    public Mono<LoanType> getLoanType(int idType) {
        return this.repository.findById(idType)
                .map(this::toEntity);
    }
}

