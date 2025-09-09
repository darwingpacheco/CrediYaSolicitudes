package co.com.solicitudescrediya.usecase.loan.loan;

import co.com.solicitudescrediya.model.UserCheckResponse;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.loan.reviewLoans.ListLoanUserDTO;
import co.com.solicitudescrediya.model.loan.reviewLoans.LoanDetailDTO;
import co.com.solicitudescrediya.model.stateloan.LoanState;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.model.user.User;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import co.com.solicitudescrediya.usecase.loan.LoanUseCase;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import co.com.solicitudescrediya.usecase.loan.paginator.Paginator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static co.com.solicitudescrediya.model.util.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoanUseCaseTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private TypeLoanRepository typeLoanRepository;

    @Mock
    private StateLoanRepository stateLoanRepository;

    @Mock
    private UserGateway userGateway;

    @InjectMocks
    private LoanUseCase loanUseCase;

    private Loan loan;
    private LoanState loanState;
    private LoanType loanType;
    private User user;
    private final String token = "mock-token";

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

        loanState = new LoanState(
                1,
                "PENDIENTE",
                "Pendiente de revisión");

        loanType = new LoanType(
                1,
                "Préstamo Personal",
                BigDecimal.valueOf(1_000_000),
                BigDecimal.valueOf(20_000_000),
                BigDecimal.valueOf(2.5),
                true
        );

        user = new User(
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
    }

    @Test
    void emailNotExistInUser() {
        var mockResponse = new UserCheckResponse(404, "Usuario no encontrado");
        when(userGateway.existUserByEmail(loan.getEmailUser(), token))
                .thenReturn(Mono.just(mockResponse));

        StepVerifier.create(loanUseCase.createLoan(loan, token))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals("Usuario no encontrado"))
                .verify();

        verify(userGateway).existUserByEmail(loan.getEmailUser(), token);
    }

    @Test
    void stateInLoanNotExist() {
        var mockResponse = new UserCheckResponse(200, "OK");
        when(userGateway.existUserByEmail(loan.getEmailUser(), token))
                .thenReturn(Mono.just(mockResponse));
        when(stateLoanRepository.findByStateLoan(loan.getStateLoanId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(loanUseCase.createLoan(loan, token))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals(NOT_STATE_LOAN))
                .verify();
    }

    @Test
    void typeLoanNotExist() {
        var mockResponse = new UserCheckResponse(200, "OK");
        when(userGateway.existUserByEmail(loan.getEmailUser(), token))
                .thenReturn(Mono.just(mockResponse));
        when(stateLoanRepository.findByStateLoan(loan.getStateLoanId()))
                .thenReturn(Mono.just(loanState));
        when(typeLoanRepository.findByLoanType(loan.getTypeLoanId()))
                .thenReturn(Mono.empty());

        StepVerifier.create(loanUseCase.createLoan(loan, token))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals(NOT_TYPE_LOAN))
                .verify();
    }

    @Test
    void amountNotInRange() {
        var mockResponse = new UserCheckResponse(200, "OK");
        when(userGateway.existUserByEmail(loan.getEmailUser(), token))
                .thenReturn(Mono.just(mockResponse));
        when(stateLoanRepository.findByStateLoan(loan.getStateLoanId()))
                .thenReturn(Mono.just(loanState));
        when(typeLoanRepository.findByLoanType(loan.getTypeLoanId()))
                .thenReturn(Mono.just(loanType));
        when(typeLoanRepository.findValueRange(loanType.getId(), loan.getAmountLoan()))
                .thenReturn(Mono.empty());

        StepVerifier.create(loanUseCase.createLoan(loan, token))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals(AMOUNT_NOT_RANGE))
                .verify();
    }

    @Test
    void successCreateLoan() {
        var mockResponse = new UserCheckResponse(200, "OK");
        when(userGateway.existUserByEmail(loan.getEmailUser(), token))
                .thenReturn(Mono.just(mockResponse));
        when(stateLoanRepository.findByStateLoan(loan.getStateLoanId()))
                .thenReturn(Mono.just(loanState));
        when(typeLoanRepository.findByLoanType(loan.getTypeLoanId()))
                .thenReturn(Mono.just(loanType));
        when(typeLoanRepository.findValueRange(loanType.getId(), loan.getAmountLoan()))
                .thenReturn(Mono.just(loanType));
        when(loanRepository.createLoan(loan)).thenReturn(Mono.just(loan));

        StepVerifier.create(loanUseCase.createLoan(loan, token))
                .expectNext(loan)
                .verifyComplete();

        verify(loanRepository).createLoan(loan);
    }

    @Test
    void getAllLoanRequestsGroupedByUser_success() {
        Loan approvedLoan = new Loan(2L, "1234227890",
                BigDecimal.valueOf(2_400_000), 12,
                "cliente@ejemplo.com", 2, 1);

        when(loanRepository.findPendingForReview()).thenReturn(Flux.just(loan));
        when(userGateway.getUserByEmail(token, loan.getEmailUser())).thenReturn(Mono.just(user));
        when(stateLoanRepository.getLoanState(loan.getStateLoanId())).thenReturn(Mono.just(loanState));
        when(typeLoanRepository.getLoanType(loan.getTypeLoanId())).thenReturn(Mono.just(loanType));
        when(loanRepository.findApprovedByEmail(loan.getEmailUser())).thenReturn(Flux.just(approvedLoan));

        StepVerifier.create(loanUseCase.getAllLoanRequestsGroupedByUser(token, 0, 10))
                .assertNext(paginator -> {
                    assertEquals(1, paginator.getContent().size());
                    ListLoanUserDTO dto = paginator.getContent().get(0);
                    assertEquals("Carlos Pérez", dto.getName());
                    assertEquals("cliente@ejemplo.com", dto.getEmail());
                    assertEquals(user.getNumberDocument(), dto.getNumberDocument());
                    assertEquals(BigDecimal.valueOf(5_000_000), dto.getBaseSalary());
                    assertEquals(1, dto.getLoanRequests().size());
                    LoanDetailDTO detail = dto.getLoanRequests().get(0);
                    assertEquals("Préstamo Personal", detail.getLoanType());
                    assertEquals("PENDIENTE", detail.getLoanState());
                })
                .verifyComplete();
    }

    @Test
    void getAllLoanRequestsGroupedByUser_empty() {
        when(loanRepository.findPendingForReview()).thenReturn(Flux.empty());

        StepVerifier.create(loanUseCase.getAllLoanRequestsGroupedByUser(token, 0, 10))
                .expectErrorMatches(e -> e instanceof ConflictException &&
                        e.getMessage().equals(REGIST_NOT_EXIST))
                .verify();
    }

    @Test
    void conflictException_message() {
        ConflictException ex = new ConflictException("Error esperado");
        assertEquals("Error esperado", ex.getMessage());
    }

    @Test
    void paginator_shouldPaginateCorrectly() {
        List<String> data = List.of("a", "b", "c", "d", "e");

        Paginator<String> paginator = new Paginator<>(data.subList(0, 2), 0, 2, data.size());

        assertEquals(2, paginator.getContent().size());
        assertEquals(0, paginator.getPage());
        assertEquals(2, paginator.getSize());
        assertEquals(5, paginator.getTotalElements());
    }
}
