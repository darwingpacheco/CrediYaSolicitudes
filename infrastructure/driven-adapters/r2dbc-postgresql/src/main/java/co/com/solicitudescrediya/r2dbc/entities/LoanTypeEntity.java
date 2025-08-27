package co.com.solicitudescrediya.r2dbc.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("tipo_prestamo")
@Data
public class LoanTypeEntity {

    @Id
    @Column("id_tipo_prestamo")
    private Long id;

    @Column("nombre")
    private String nameTypeLoan;

    @Column("monto_minimo")
    private BigDecimal minAmountLoan;

    @Column("monto_maximo")
    private BigDecimal maxAmountLoan;

    @Column("tasa_interes")
    private BigDecimal interestRateLoan;

    @Column("validacion_automatica")
    private Boolean automaticValidation;
}
