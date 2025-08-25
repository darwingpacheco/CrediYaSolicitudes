package co.com.solicitudescrediya.api.dto;

import java.math.BigDecimal;

public record LoanResponseDTO(Long i,
                              BigDecimal monto,
                              Integer plazo,
                              String email,
                              Integer idTipoPrestamo,
                              Integer idEstado) {
}
