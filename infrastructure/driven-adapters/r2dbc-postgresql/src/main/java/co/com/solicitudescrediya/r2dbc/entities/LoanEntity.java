package co.com.solicitudescrediya.r2dbc.entities;


import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;

@Table("solicitud")
@Data
public class LoanEntity {

    @Id
    @Column("id_solicitud")
    private Long id;

    @Column("documento_identidad")
    private String numberDocumentUser;

    @Column("monto")
    private BigDecimal amountLoan;

    @Column("plazo")
    private Integer termLoan;

    @Column("email")
    private String emailUser;

    @Column("id_estado")
    private int stateLoanId;

    @Column("id_tipo_prestamo")
    private int typeLoanId;
}
