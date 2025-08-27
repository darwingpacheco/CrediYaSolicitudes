package co.com.solicitudescrediya.api.globalExceptions;

import lombok.Getter;
import org.springframework.validation.Errors;

@Getter
public class ValidateExceptionHandler extends RuntimeException {

    private final Errors error;

    public ValidateExceptionHandler(Errors errors) {
        super("Error de validación");
        this.error = errors;
    }
}
