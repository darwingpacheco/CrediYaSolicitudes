package co.com.solicitudescrediya.model.autoValidate;

import co.com.solicitudescrediya.model.loan.ApprovedLoansAutoValidation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class AutoValidateCapacity {
    private Long id;
    private String numberDocumentUser;
    private String emailUser;
    private BigDecimal baseSalary;
    private Integer termLoan;
    private BigDecimal amountNewLoan;
    private BigDecimal interestNewLoan;
    private List<ApprovedLoansAutoValidation> approvedLoansAuto;
}
