package co.com.solicitudescrediya.api.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequestDTO {

    private BigDecimal monto;
    private Integer plazo;
    private String email;
    private Integer idTipoPrestamo;

}
