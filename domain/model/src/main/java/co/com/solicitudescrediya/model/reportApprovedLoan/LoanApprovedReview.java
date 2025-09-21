package co.com.solicitudescrediya.model.reportApprovedLoan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApprovedReview {
    private String dateApproved;
    private BigDecimal amountLoanAprroved;
}
