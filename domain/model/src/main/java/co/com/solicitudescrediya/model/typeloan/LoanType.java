package co.com.solicitudescrediya.model.typeloan;
import lombok.*;
//import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanType {
    private Long id;
    private String nameTypeLoan;
    private BigDecimal minAmountLoan;
    private BigDecimal maxAmountLoan;
    private BigDecimal interestRateLoan;
    private Boolean automaticValidation;
}
