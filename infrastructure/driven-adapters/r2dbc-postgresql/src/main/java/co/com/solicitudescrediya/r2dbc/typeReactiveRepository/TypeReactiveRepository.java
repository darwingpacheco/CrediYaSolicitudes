package co.com.solicitudescrediya.r2dbc.typeReactiveRepository;

import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.r2dbc.entities.LoanTypeEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface TypeReactiveRepository extends ReactiveCrudRepository<LoanTypeEntity, Integer>,
        ReactiveQueryByExampleExecutor<LoanTypeEntity> {

    @Query("SELECT * FROM tipo_prestamo WHERE id_tipo_prestamo = :typeLoanId")
    Mono<LoanTypeEntity> existLoanTypeById(int typeLoanId);
}
