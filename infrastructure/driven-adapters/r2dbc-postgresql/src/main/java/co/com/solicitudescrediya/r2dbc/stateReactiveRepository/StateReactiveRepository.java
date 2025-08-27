package co.com.solicitudescrediya.r2dbc.stateReactiveRepository;

import co.com.solicitudescrediya.r2dbc.entities.LoanStateEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface StateReactiveRepository extends ReactiveCrudRepository<LoanStateEntity, Integer>,
    ReactiveQueryByExampleExecutor<LoanStateEntity> {

    @Query("SELECT * FROM estados WHERE id_estado = :stateLoanId AND nombre = 'PENDIENTE'")
    Mono<LoanStateEntity> existsByIdAndName(int stateLoanId);
}
