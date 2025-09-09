package co.com.solicitudescrediya.model.loan.reviewLoans;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ListLoanUserDTO {
    private String name;
    private String email;
    private String numberDocument;
    private BigDecimal baseSalary;

    private List<LoanDetailDTO> loanRequests;
    private BigDecimal totalMonthlyDebt;
}
