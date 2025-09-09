package co.com.solicitudescrediya.r2dbc.loanReactiveRepository;

import co.com.solicitudescrediya.r2dbc.entities.LoanEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<LoanEntity, Integer>, ReactiveQueryByExampleExecutor<LoanEntity> {

    @Query("SELECT * FROM solicitud WHERE documento_identidad = :numberDocument")
    Mono<LoanEntity> findByNumberDoc(String numberDocument);

    @Query("""
    SELECT s.*
    FROM solicitud s
    JOIN estados e ON s.id_estado = e.id_estado
    JOIN tipo_prestamo t ON s.id_tipo_prestamo = t.id_tipo_prestamo
    WHERE e.nombre <> 'APROBADO'
    """)
    Flux<LoanEntity> findByEstados();

    @Query("""
        SELECT s.*
        FROM solicitud s
        JOIN estados e ON s.id_estado = e.id_estado
        WHERE s.email = :email AND e.nombre = 'APROBADO'
    """)
    Flux<LoanEntity> findApprovedByEmail(String email);
}
