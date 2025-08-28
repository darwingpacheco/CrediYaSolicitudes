package co.com.solicitudescrediya.usecase.loan.loan;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.stateloan.LoanState;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import co.com.solicitudescrediya.usecase.loan.LoanUseCase;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LoanUseCaseTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanUseCase loanUseCase;

    @Mock
    private TypeLoanRepository typeLoanRepository;

    @Mock
    private StateLoanRepository stateLoanRepository;

    @Mock
    private UserGateway userGateway;

    private Loan loan;
    private LoanState loanState;
    private LoanType loanType;

    @BeforeEach
    void setUp() {
        loan = new Loan(
                1L,
                "1234227890",
                BigDecimal.valueOf(3000000),
                12,
                "cliente@ejemplo.com",
                1,
                1
        );

        loanState = new LoanState(
                1L,
                "'PENDIENTE'",
                "Pendiente de revisión"
        );

        loanType = new LoanType(1L,
                    "Préstamo Personal",
                BigDecimal.valueOf(1000000),
                BigDecimal.valueOf(20000000),
                BigDecimal.valueOf(2.5),
                TRUE
        );
    }

    @Test
    void emailNotExistInUser(){
        String email = loan.getEmailUser();
        when(userGateway.existUserByEmail(email)).thenReturn(Mono.just(false));

        StepVerifier.create(loanUseCase.createLoan(loan))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals("El usuario: " + email + " no existe"))
                .verify();
    }

    @Test
    void stateInLoanNotExist(){
        when(stateLoanRepository.findByLoanId(loan.getStateLoanId())).thenReturn(Mono.just(false));
        when(typeLoanRepository.findByLoanType(loan.getTypeLoanId())).thenReturn(Mono.just(true));
        when(userGateway.existUserByEmail(loan.getEmailUser())).thenReturn(Mono.just(true));

        StepVerifier.create(loanUseCase.createLoan(loan))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals("En este momento no es posible asignarte un estado de préstamo"))
                .verify();
    }

    @Test
    void typeLoanNotExist() {
        when(stateLoanRepository.findByLoanId(loan.getStateLoanId())).thenReturn(Mono.just(true));
        when(typeLoanRepository.findByLoanType(loan.getTypeLoanId())).thenReturn(Mono.just(false));
        when(userGateway.existUserByEmail(loan.getEmailUser())).thenReturn(Mono.just(true));

        StepVerifier.create(loanUseCase.createLoan(loan))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals("No existe el tipo de préstamo solicitado"))
                .verify();
    }

    @Test
    void successCreateLoan() {
        when(stateLoanRepository.findByLoanId(loan.getStateLoanId())).thenReturn(Mono.just(true));
        when(typeLoanRepository.findByLoanType(loan.getTypeLoanId())).thenReturn(Mono.just(true));
        when(userGateway.existUserByEmail(loan.getEmailUser())).thenReturn(Mono.just(true));
        when(loanRepository.createLoan(loan)).thenReturn(Mono.just(loan));

        StepVerifier.create(loanUseCase.createLoan(loan))
                .expectNext(loan)
                .verifyComplete();

        verify(loanRepository, times(1)).createLoan(loan);
    }

    @Test
    void bothStateAndTypeNotExist_takeStateErrorFirst() {
        when(stateLoanRepository.findByLoanId(loan.getStateLoanId())).thenReturn(Mono.just(false));
        when(typeLoanRepository.findByLoanType(loan.getTypeLoanId())).thenReturn(Mono.just(false));
        when(userGateway.existUserByEmail(loan.getEmailUser())).thenReturn(Mono.just(true));

        StepVerifier.create(loanUseCase.createLoan(loan))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals("En este momento no es posible asignarte un estado de préstamo"))
                .verify();
    }

}
