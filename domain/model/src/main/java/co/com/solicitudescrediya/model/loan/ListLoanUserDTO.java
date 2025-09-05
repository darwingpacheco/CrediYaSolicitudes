package co.com.solicitudescrediya.model.loan;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class ListLoanUserDTO {
    private String name;
    private String email;
    private String numberDocument;
    private BigDecimal baseSalary;
    private String typeLoan;
    private String stateLoan;
    private Integer term;
    private BigDecimal monto;

    private int totalElements;
    private BigDecimal approvedOnes;
}
