package co.com.solicitudescrediya.usecase.notifystateloan;

import co.com.solicitudescrediya.model.gateways.LoanSolicitudeEventPublisher;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.notification.ChangeStateLoan;
import co.com.solicitudescrediya.model.notification.EmailNotification;
import co.com.solicitudescrediya.model.stateloan.LoanState;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.usecase.loan.LoanUseCase;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static co.com.solicitudescrediya.model.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class NotifyStateLoanUseCaseTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private TypeLoanRepository typeLoanRepository;

    @Mock
    private StateLoanRepository stateLoanRepository;

    @Mock
    private LoanUseCase loanUseCase;

    @Mock
    private LoanSolicitudeEventPublisher loanSolicitudePublisher;

    @InjectMocks
    private NotifyStateLoanUseCase notifyStateLoanUseCase;

    private Loan loan;
    private LoanType loanType;
    private LoanState loanState;
    private ChangeStateLoan changeState;

    @BeforeEach
    void setUp() {

        loan = new Loan(
                1L,
                "1234227890",
                BigDecimal.valueOf(3_000_000),
                12,
                "test@test.com",
                1,
                1
        );

        loanState = new LoanState(
                1,
                "APROBADO",
                "Prestamo aprobado");

        loanType = new LoanType(
                1,
                "Préstamo Personal",
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(20_000_000),
                BigDecimal.valueOf(2.5),
                true
        );

        changeState = new ChangeStateLoan(
                1,
                2
        );
    }

    @Test
    void updateStateLoan_successfulFlow() {

        when(loanRepository.findBySolicitudedId(changeState.getIdApplication())).thenReturn(Mono.just(loan));
        when(loanUseCase.validateUser(anyString(), anyString(), anyString())).thenReturn(Mono.empty());
        when(stateLoanRepository.findByStateToUpdate(anyInt())).thenReturn(Mono.just(loanState));
        when(loanRepository.updateStatus(changeState.getIdState(), changeState.getIdApplication())).thenReturn(Mono.just(loan));
        when(typeLoanRepository.findByLoanType(loan.getTypeLoanId())).thenReturn(Mono.just(loanType));
        when(loanSolicitudePublisher.publish(any(EmailNotification.class))).thenReturn(Mono.empty());

        StepVerifier.create(notifyStateLoanUseCase.updateStateLoan(changeState, "token"))
                .expectNextMatches(result -> result.getId().equals(loan.getId()) && result.getEmailUser().equals("test@test.com"))
                .verifyComplete();

        ArgumentCaptor<EmailNotification> captor = ArgumentCaptor.forClass(EmailNotification.class);
        verify(loanSolicitudePublisher).publish(captor.capture());

        EmailNotification notification = captor.getValue();
        assertEquals("test@test.com", notification.getEmail());
        assertEquals("APROBADO", notification.getStatus());
    }

    @Test
    void updateStateLoan_userNotFound_throwsConflict() {
        when(loanRepository.findBySolicitudedId(changeState.getIdApplication())).thenReturn(Mono.empty());

        StepVerifier.create(notifyStateLoanUseCase.updateStateLoan(changeState, "token"))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals("El usuario no existe"))
                .verify();
    }

    @Test
    void updateStateLoan_invalidState_throwsConflict() {
        when(loanRepository.findBySolicitudedId(changeState.getIdApplication())).thenReturn(Mono.just(loan));
        when(loanUseCase.validateUser(anyString(), anyString(), anyString())).thenReturn(Mono.empty());
        when(stateLoanRepository.findByStateToUpdate(changeState.getIdState())).thenReturn(Mono.empty());

        StepVerifier.create(notifyStateLoanUseCase.updateStateLoan(changeState, "token"))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals("En este momento no es posible asignarte un estado de préstamo"))
                .verify();
    }

    @Test
    void updateStateLoan_shouldFailWhenLoanTypeDoesNotExist() {
        when(loanRepository.findBySolicitudedId(changeState.getIdApplication()))
                .thenReturn(Mono.just(loan));
        when(loanUseCase.validateUser(anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(stateLoanRepository.findByStateToUpdate(anyInt()))
                .thenReturn(Mono.just(loanState));
        when(loanRepository.updateStatus(changeState.getIdState(), changeState.getIdApplication()))
                .thenReturn(Mono.just(loan));

        when(typeLoanRepository.findByLoanType(anyInt()))
                .thenReturn(Mono.empty());

        StepVerifier.create(notifyStateLoanUseCase.updateStateLoan(changeState, "token"))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ConflictException);
                    assertEquals(NOT_TYPE_LOAN, error.getMessage());
                })
                .verify();
    }

    @Test
    void updateStateLoan_shouldFailWhenStateLoanDoesNotExist() {
        ChangeStateLoan request = new ChangeStateLoan(1, 2);

        when(loanRepository.findBySolicitudedId(request.getIdApplication()))
                .thenReturn(Mono.just(loan));
        when(loanUseCase.validateUser(anyString(), anyString(), anyString()))
                .thenReturn(Mono.empty());
        when(stateLoanRepository.findByStateToUpdate(request.getIdState()))
                .thenReturn(Mono.empty());

        StepVerifier.create(notifyStateLoanUseCase.updateStateLoan(request, "token"))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ConflictException);
                    assertEquals(NOT_STATE_LOAN, error.getMessage());
                })
                .verify();
    }

    @Test
    void updateStateLoan_shouldFailWhenUserIsInvalid() {
        ChangeStateLoan request = new ChangeStateLoan(1, 2);

        when(loanRepository.findBySolicitudedId(request.getIdApplication()))
                .thenReturn(Mono.just(loan));
        when(loanUseCase.validateUser(anyString(), anyString(), anyString()))
                .thenReturn(Mono.error(new ConflictException("INVALID_USER")));

        StepVerifier.create(notifyStateLoanUseCase.updateStateLoan(request, "token"))
                .expectErrorSatisfies(error -> {
                    assertTrue(error instanceof ConflictException);
                    assertEquals("INVALID_USER", error.getMessage());
                })
                .verify();
    }
}
