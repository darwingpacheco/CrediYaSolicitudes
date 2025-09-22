package co.com.solicitudescrediya.usecase.autoValidateLoan;

import co.com.solicitudescrediya.model.autoValidate.AutoValidateCapacity;
import co.com.solicitudescrediya.model.autoValidate.NewStateAutoValidate;
import co.com.solicitudescrediya.model.gateways.LoanSolicitudeEventPublisher;
import co.com.solicitudescrediya.model.loan.ApprovedLoansAutoValidation;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.reportApprovedLoan.LoanApprovedReview;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.model.user.User;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import co.com.solicitudescrediya.usecase.autoValidate.AutoValidateUseCase;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static co.com.solicitudescrediya.model.util.Constants.ID_LOAN_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutoValidateUseCaseTest {
    @Mock
    private TypeLoanRepository typeLoanRepository;

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private LoanSolicitudeEventPublisher solicitudeEventPublisher;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private AutoValidateUseCase useCase;

    private Loan loan;
    private LoanType typeLoan;
    private User loanUser;
    private List<ApprovedLoansAutoValidation> approvedLoans;

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

        typeLoan = new LoanType(
                1,
                "Préstamo Personal",
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(20_000_000),
                BigDecimal.valueOf(2.5),
                true
        );

        loanUser = new User(
                1L,
                "Carlos",
                "Pérez",
                "cliente@ejemplo.com",
                "1234567890",
                "1234227890",
                "3224291874",
                LocalDate.of(2003, 10, 1),
                "abrego",
                1,
                BigDecimal.valueOf(5_000_000));

        approvedLoans = List.of(
                ApprovedLoansAutoValidation.builder()
                        .amountLoan(BigDecimal.valueOf(1_000_000))
                        .termLoan(24)
                        .interestRateLoan(BigDecimal.valueOf(2.5))
                        .build()
        );

    }

    @Test
    void validateCapacityLoan_ShouldReturnAutoValidateCapacity() {
        when(typeLoanRepository.findByLoanType(anyInt())).thenReturn(Mono.just(typeLoan));
        when(userGateway.getUserByEmail(
                eq("token"),
                eq(loan.getEmailUser()),
                eq("autoValidate")
        )).thenReturn(Mono.just(loanUser));

        when(loanRepository.findApprovedByNumberDoc(anyString()))
                .thenReturn(Mono.just(approvedLoans));
        when(solicitudeEventPublisher.sendDataSqsToValidation(any(AutoValidateCapacity.class)))
                .thenReturn(Mono.just(new AutoValidateCapacity()));

        Mono<AutoValidateCapacity> result = useCase.validateCapacityLoan(loan, "token");

        StepVerifier.create(result)
                .assertNext(capacity -> assertNotNull(capacity))
                .verifyComplete();

        verify(typeLoanRepository).findByLoanType(1);
        verify(userGateway).getUserByEmail(eq("token"), eq(loan.getEmailUser()), eq("autoValidate"));
        verify(loanRepository).findApprovedByNumberDoc(loan.getNumberDocumentUser());
        verify(solicitudeEventPublisher).sendDataSqsToValidation(any(AutoValidateCapacity.class));
    }

    @Test
    void validateCapacityLoan_TypeLoanNotFound_ShouldReturnEmpty() {
        when(typeLoanRepository.findByLoanType(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.validateCapacityLoan(loan, "token"))
                .verifyComplete();

        verify(typeLoanRepository).findByLoanType(1);
        verifyNoInteractions(userGateway, loanRepository, solicitudeEventPublisher);
    }

    @Test
    void updateStateLastAutoValidate_ShouldUpdateStatusAndReturnResponse() {
        NewStateAutoValidate response = new NewStateAutoValidate(1, 2, "APROBADO");
        when(loanRepository.findBySolicitudedId(anyInt())).thenReturn(Mono.just(loan));
        when(loanRepository.updateStatus(anyInt(), anyInt())).thenReturn(Mono.just(loan));

        StepVerifier.create(useCase.updateStateLastAutoValidate(response))
                .assertNext(resp -> assertEquals(2, resp.getStatus()))
                .verifyComplete();

        verify(loanRepository).findBySolicitudedId(1);
        verify(loanRepository).updateStatus(1, 2);
    }

    @Test
    void updateStateLastAutoValidate_LoanNotFound_ShouldThrowConflictException() {
        NewStateAutoValidate response = new NewStateAutoValidate(1, 2, "APROBADO");
        when(loanRepository.findBySolicitudedId(anyInt())).thenReturn(Mono.empty());

        StepVerifier.create(useCase.updateStateLastAutoValidate(response))
                .expectErrorMatches(throwable -> throwable instanceof ConflictException &&
                        throwable.getMessage().equals(ID_LOAN_NOT_FOUND))
                .verify();

        verify(loanRepository).findBySolicitudedId(1);
        verifyNoMoreInteractions(loanRepository);
    }

    @Test
    void validateApprovedState_ShouldNotifyIfStateLoanIs2() {
        loan = new Loan(
                1L,
                "1234227890",
                BigDecimal.valueOf(3_000_000),
                12,
                "cliente@ejemplo.com",
                2,
                1
        );

        NewStateAutoValidate response = new NewStateAutoValidate(1,2, "APROBADO");

        when(solicitudeEventPublisher.notificationSqsForReview(any(LoanApprovedReview.class))).thenReturn(Mono.empty());

        StepVerifier.create(useCase.validateApprovedState(loan, response))
                .expectNext(response)
                .verifyComplete();

        verify(solicitudeEventPublisher).notificationSqsForReview(any(LoanApprovedReview.class));
    }

    @Test
    void validateApprovedState_ShouldReturnWithoutNotificationIfStateLoanIsNot2() {
        loan = new Loan(
                1L,
                "1234227890",
                BigDecimal.valueOf(3_000_000),
                12,
                "cliente@ejemplo.com",
                1,
                1
        );

        NewStateAutoValidate response = new NewStateAutoValidate(1, 2, "APROBADO");

        StepVerifier.create(useCase.validateApprovedState(loan, response))
                .expectNext(response)
                .verifyComplete();

        verifyNoInteractions(solicitudeEventPublisher);
    }
}