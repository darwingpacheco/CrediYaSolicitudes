package co.com.solicitudescrediya.model.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApprovedLoansAutoValidation {
    private BigDecimal amountLoan;
    private Integer termLoan;
    private BigDecimal interestRateLoan;
}
