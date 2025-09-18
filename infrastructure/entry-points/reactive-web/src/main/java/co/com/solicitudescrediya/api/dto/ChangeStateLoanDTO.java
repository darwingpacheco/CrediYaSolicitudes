package co.com.solicitudescrediya.api.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChangeStateLoanDTO (
        @NotNull(message = "El ID de la solicitud es obligatorio")
        @Positive(message = "El ID de la solicitud debe ser mayor a 0")
        Long idApplication,

        @NotNull(message = "El ID del estado es obligatorio")
        @Positive(message = "El ID del estado debe ser mayor a 0")
        Long idState
){}