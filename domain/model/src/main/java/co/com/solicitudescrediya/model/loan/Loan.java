package co.com.solicitudescrediya.model.loan;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private Long id;
    private String numberDocumentUser;
    private BigDecimal amountLoan;
    private Integer termLoan;
    private String emailUser;
    private int stateLoanId;
    private int typeLoanId;
}
