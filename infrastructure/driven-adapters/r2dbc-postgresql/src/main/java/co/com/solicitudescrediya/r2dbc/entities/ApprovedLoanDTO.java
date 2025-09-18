package co.com.solicitudescrediya.r2dbc.entities;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Column;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ApprovedLoanDTO {
    @Column("monto")
    private BigDecimal amountLoan;

    @Column("plazo")
    private Integer termLoan;

    @Column("tasa_interes")
    private java.math.BigDecimal interestRateLoan;
}
