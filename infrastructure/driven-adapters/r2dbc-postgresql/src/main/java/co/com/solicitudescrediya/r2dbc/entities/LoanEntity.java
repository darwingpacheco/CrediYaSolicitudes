package co.com.solicitudescrediya.r2dbc.entities;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("solicitud")
@Data
public class LoanEntity {

    @Id
    private Long id;

    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Integer idTipoPrestamo;
    private Integer idEstado;

}
