package co.com.solicitudescrediya.r2dbc.loanReactiveRepository;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.r2dbc.entities.ApprovedLoanDTO;
import co.com.solicitudescrediya.r2dbc.entities.LoanEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.query.ReactiveQueryByExampleExecutor;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

// TODO: This file is just an example, you should delete or modify it
public interface MyReactiveRepository extends ReactiveCrudRepository<LoanEntity, Integer>, ReactiveQueryByExampleExecutor<LoanEntity> {

    @Query("SELECT * FROM solicitud WHERE documento_identidad = :numberDocument")
    Mono<LoanEntity> findByNumberDoc(String numberDocument);

    @Query("""
            SELECT s.*
            FROM solicitud s
            JOIN estados e ON s.id_estado = e.id_estado
            JOIN tipo_prestamo t ON s.id_tipo_prestamo = t.id_tipo_prestamo
            WHERE e.nombre IN (:stateUser)
            """)
    Flux<LoanEntity> findByEstados(List<String> stateUser);

    @Query("""
                SELECT s.*
                FROM solicitud s
                JOIN estados e ON s.id_estado = e.id_estado
                WHERE s.email = :email AND e.nombre = 'APROBADO'
            """)
    Flux<LoanEntity> findApprovedByEmail(String email);

    @Query("""
            UPDATE solicitud
            SET id_estado = :idState
            WHERE id_solicitud = :idApproved
            RETURNING *
            """)
    Mono<LoanEntity> updateStateByApprovedId(@Param("idState") int idState,
                                             @Param("idApproved") int idApproved);

    @Query("""
                SELECT *
                FROM solicitud WHERE id_solicitud = :idApproved
            """)
    Mono<Loan> findBySolicitudeId(int idApproved);

    @Query("""
        SELECT s.monto, s.plazo, lt.tasa_interes
        FROM solicitud s
        INNER JOIN tipo_prestamo lt ON lt.id_tipo_prestamo = s.id_tipo_prestamo
        WHERE s.documento_identidad =:numDoc AND s.id_estado = 2
        ORDER BY s.id_solicitud ASC
    """)
    Flux<ApprovedLoanDTO> findApprovedLoansByNumDoc(@Param("documento_identidad") String numDoc);
}
