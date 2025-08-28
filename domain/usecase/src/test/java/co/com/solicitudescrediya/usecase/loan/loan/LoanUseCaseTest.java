package co.com.solicitudescrediya.usecase.loan.loan;

import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.stateloan.LoanState;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.LoanType;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;

import static java.lang.Boolean.TRUE;

@ExtendWith(MockitoExtension.class)
public class LoanUseCaseTest {

    @Mock
    private LoanRepository loanRepository;

    @Mock
    private TypeLoanRepository typeLoanRepository;

    @Mock
    private StateLoanRepository stateLoanRepository;

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

}
