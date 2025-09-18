package co.com.solicitudescrediya.r2dbc.loanReactiveRepository;

import co.com.solicitudescrediya.model.loan.ApprovedLoansAutoValidation;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.r2dbc.entities.LoanEntity;
import co.com.solicitudescrediya.r2dbc.helper.ReactiveAdapterOperations;
import lombok.extern.slf4j.Slf4j;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
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
            filteredStatuses = stateUser;
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

    @Override
    public Mono<List<ApprovedLoansAutoValidation>> findApprovedByNumberDoc(String numberDocumentUser) {
        return repository.findApprovedLoansByNumDoc(numberDocumentUser)
                .doOnSubscribe(s -> log.info("Consultando préstamos aprobados número documento={} con id_estado=2", numberDocumentUser))
                .doOnNext(dto -> log.debug("Fila aprobada -> monto={}, plazo={}, tasaInteres={}", dto.getAmountLoan(), dto.getTermLoan(), dto.getInterestRateLoan()))
                .map(dto -> ApprovedLoansAutoValidation.builder()
                        .amountLoan(dto.getAmountLoan())
                        .termLoan(dto.getTermLoan())
                        .interestRateLoan(dto.getInterestRateLoan())
                        .build())
                .collectList()
                .doOnNext(list -> log.info("Total préstamos aprobados encontrados para {}: {}", numberDocumentUser, list.size()))
                .doOnError(e -> log.error("Error consultando aprobados para {}: {}", numberDocumentUser, e.getMessage()));
    }
}
