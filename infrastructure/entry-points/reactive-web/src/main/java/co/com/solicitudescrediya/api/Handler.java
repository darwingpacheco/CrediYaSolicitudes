package co.com.solicitudescrediya.api;

import co.com.solicitudescrediya.api.dto.ChangeStateLoanDTO;
import co.com.solicitudescrediya.api.dto.LoanRequestDTO;
import co.com.solicitudescrediya.api.globalExceptions.ValidateExceptionHandler;
import co.com.solicitudescrediya.api.mapper.LoanMapperDTO;
import co.com.solicitudescrediya.api.utils.ValidatorUtils;
import co.com.solicitudescrediya.usecase.loan.LoanUseCase;
import co.com.solicitudescrediya.usecase.notifystateloan.NotifyStateLoanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanUseCase loanUseCase;
    private final LoanMapperDTO loanMapperDTO;
    private final ValidatorUtils validatorUtils;
    private final NotifyStateLoanUseCase notifyStateLoanUseCase;

    public Mono<ServerResponse> createLoan(ServerRequest request) {
        String token = request.headers().firstHeader("Authorization");

        return validatorUtils.validateRequestBody(request, LoanRequestDTO.class)
                .doOnNext(loanRequest -> log.info("Request createLoan OK: {}", loanRequest))
                .flatMap(loanRequest -> loanUseCase.createLoan(loanMapperDTO.toLoan(loanRequest), token)
                        .doOnNext(loan -> log.info("Loan application created: {}", loan))
                        .flatMap(loan -> ServerResponse.ok().bodyValue(loanMapperDTO.toLoanResponseDTO(loan)))
                )
                .doOnError(error -> {
                    if (error instanceof ValidateExceptionHandler ex) {
                        ex.getError().getAllErrors().forEach(err -> {
                            log.error("Validación fallida: campo={}, mensaje={}",
                                    ((FieldError) err).getField(),
                                    err.getDefaultMessage());
                        });
                    } else {
                        log.error("Error to create loan: {}", error.getMessage(), error);
                    }
                });
    }

    public Mono<ServerResponse> getAllLoanRequests(ServerRequest request) {
        log.info("se ingresa en getAllLoanRequests a obtener lista filtrada de prestamos por usuario");
        String statusParam = request.queryParam("status").orElse("");
        List<String> requestedStatuses = Arrays.stream(statusParam.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();

        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String token = request.headers().firstHeader("Authorization");

        return loanUseCase.getAllLoanRequestsGroupedByUser(requestedStatuses, token, page, size)
                .flatMap(result -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result))
                .doOnError(error -> log.error("Error al obtener lista usuarios con prestamos: {}", error.getMessage()));
    }

    public Mono<ServerResponse> updateStateLoanAndNotify(ServerRequest request) {
        log.info("se ingresa en updateStateLoanAndNotify para actualizar estado del prestamo");

        String token = request.headers().firstHeader("Authorization");

        return validatorUtils.validateRequestBody(request, ChangeStateLoanDTO.class)
                .flatMap(changeStateLoan -> notifyStateLoanUseCase.updateStateLoan(loanMapperDTO.toChangeStateLoan(changeStateLoan), token)
                        .flatMap(changeStatusRsp -> ServerResponse.ok().bodyValue(loanMapperDTO.toChangeStateResponse(changeStatusRsp)))
                ).doOnError(error -> log.error("Error en la actualización del estado del prestamo: {}", error.getMessage()));
    }
}
