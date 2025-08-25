package co.com.solicitudescrediya.model.loan;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Loan {
    private Long id;
    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private int idEstado;
    private int idTipoPrestamo;
}
