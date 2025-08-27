package co.com.solicitudescrediya.api.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class LoanRequestDTO {

    @NotNull(message = "El numero de documento no puede ser nulo")
    private String numberDocumentUser;

    @NotNull(message = "El monto no puede ser nulo")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor a 0")
    @Digits(integer = 12, fraction = 2, message = "El monto no puede tener más de 2 decimales")
    private BigDecimal amountLoan;

    @NotNull(message = "El plazo no puede ser nulo")
    @Min(value = 1, message = "El plazo debe ser al menos de 1 mes")
    private Integer termLoan;

    @NotBlank(message = "El correo no puede estar vacío")
    @Email(message = "Debe ser un correo válido")
    private String emailUser;

    @NotNull(message = "El tipo de préstamo no puede ser nulo")
    private int typeLoanId;
}
