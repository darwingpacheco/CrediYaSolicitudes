package co.com.solicitudescrediya.r2dbc;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.stateloan.LoanState;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.r2dbc.entities.LoanEntity;
import co.com.solicitudescrediya.r2dbc.entities.LoanStateEntity;
import co.com.solicitudescrediya.r2dbc.entities.LoanTypeEntity;
import co.com.solicitudescrediya.r2dbc.loanReactiveRepository.MyReactiveRepository;
import co.com.solicitudescrediya.r2dbc.loanReactiveRepository.MyReactiveRepositoryAdapter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.reactivecommons.utils.ObjectMapper;
import org.springframework.data.domain.Example;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static java.lang.Boolean.TRUE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MyReactiveRepositoryAdapterTest {
    // TODO: change four you own tests

    @InjectMocks
    MyReactiveRepositoryAdapter repositoryAdapter;

    @Mock
    MyReactiveRepository repository;

    @Mock
    ObjectMapper mapper;

    private LoanState loanState;
    private LoanType loanType;
    private Loan loan;
    private LoanEntity loanEntity;
    private LoanStateEntity loanStateEntity;
    private LoanTypeEntity loanTypeEntity;

    @BeforeEach
    void setUp() {
        loan = new Loan(
                1L,
                "1234227890",
                BigDecimal.valueOf(3_000_000),
                12,
                "cliente@ejemplo.com",
                1,
                1
        );

        loanEntity = new LoanEntity();
        loanEntity.setId(1L);
        loanEntity.setNumberDocumentUser("1234227890");
        loanEntity.setAmountLoan(BigDecimal.valueOf(3_000_000));
        loanEntity.setTermLoan(12);
        loanEntity.setEmailUser("cliente@ejemplo.com");
        loanEntity.setStateLoanId(1);
        loanEntity.setTypeLoanId(1);

        loanState = new LoanState(
                1L,
                "PENDIENTE",
                "Pendiente de revisión"
        );

        loanStateEntity = new LoanStateEntity();
        loanStateEntity.setId(1L);
        loanStateEntity.setStateName("PENDIENTE");
        loanStateEntity.setStateDescription("Pendiente de revisión");

        loanType = new LoanType(
                1L,
                "Préstamo Personal",
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(20_000_000),
                BigDecimal.valueOf(2.5),
                true
        );

        loanTypeEntity = new LoanTypeEntity();
        loanTypeEntity.setId(1L);
        loanTypeEntity.setNameTypeLoan("Préstamo Personal");
        loanTypeEntity.setMinAmountLoan(BigDecimal.valueOf(1_000_000));
        loanTypeEntity.setMaxAmountLoan(BigDecimal.valueOf(20_000_000));
        loanTypeEntity.setInterestRateLoan(BigDecimal.valueOf(2.5));
        loanTypeEntity.setAutomaticValidation(true);
    }

}
