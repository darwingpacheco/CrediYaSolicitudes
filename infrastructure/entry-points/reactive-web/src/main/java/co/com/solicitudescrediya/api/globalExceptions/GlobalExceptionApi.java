package co.com.solicitudescrediya.api.globalExceptions;
import co.com.solicitudescrediya.model.adapterExceptionApi.ApiError;
import co.com.solicitudescrediya.usecase.loan.conflictException.CustomException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import reactor.core.publisher.Mono;

@ControllerAdvice
public class GlobalExceptionApi {

    @ExceptionHandler(CustomException.class)
    public Mono<ResponseEntity<ApiError>> handleCustomException(CustomException ex) {
        ApiError apiError = ex.getApiError();
        return Mono.just(ResponseEntity
                .status(apiError.getStatus())
                .body(apiError));
    }
}
