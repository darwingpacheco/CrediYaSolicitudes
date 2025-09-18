package co.com.solicitudescrediya.model.loan.reviewLoans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoanDetailDTO {
    private long id;
    private String loanType;
    private String loanState;
    private int termLoan;
    private BigDecimal amountLoan;
}
