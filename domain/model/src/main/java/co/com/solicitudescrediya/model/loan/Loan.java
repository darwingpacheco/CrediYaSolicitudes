package co.com.solicitudescrediya.model.loan;
import lombok.*;

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
