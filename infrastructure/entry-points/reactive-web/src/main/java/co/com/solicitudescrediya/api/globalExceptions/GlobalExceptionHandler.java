package co.com.solicitudescrediya.api.globalExceptions;

import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebExceptionHandler;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
@Order(-2)
public class GlobalExceptionHandler implements WebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    @NonNull
    public Mono<Void> handle(@NonNull ServerWebExchange exchange, @NonNull Throwable ex) {
        ServerHttpResponse response = exchange.getResponse();

        HttpStatus status;
        Map<String, Object> errorResponse = new LinkedHashMap<>();

        if (ex instanceof ValidateExceptionHandler vex) {
            status = HttpStatus.BAD_REQUEST; // 400
            errorResponse.put("error", "Bad Request");
            errorResponse.put("messages", vex.getError().getAllErrors()
                    .stream()
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList()
            );
            log.warn("Error de validación: {}", errorResponse.get("messages"));
        } else if (ex instanceof ConflictException) {
            status = HttpStatus.CONFLICT; // 409
            errorResponse.put("error", "Conflict");
            errorResponse.put("message", ex.getMessage());
            log.warn("Error de conflicto: {}", ex.getMessage());
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR; // 500
            errorResponse.put("error", "Internal Server Error");
            errorResponse.put("message", ex.getMessage() != null ? ex.getMessage() : "Ocurrió un error inesperado");
            log.error("Error inesperado", ex);
        }

        return writeJson(response, status, errorResponse);
    }

    private Mono<Void> writeJson(ServerHttpResponse response, HttpStatus status, Object body) {
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        try {
            String jsonBody = objectMapper.writeValueAsString(body);
            var buffer = response.bufferFactory().wrap(jsonBody.getBytes(StandardCharsets.UTF_8));
            return response.writeWith(Mono.just(buffer));
        } catch (Exception e) {
            log.error("Error serializando la respuesta de error", e);
            return Mono.error(e);
        }
    }
}
