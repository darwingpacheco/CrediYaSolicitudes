package co.com.solicitudescrediya.api;

import co.com.solicitudescrediya.api.dto.LoanRequestDTO;
import co.com.solicitudescrediya.api.globalExceptions.ValidateExceptionHandler;
import co.com.solicitudescrediya.api.mapper.LoanMapperDTO;
import co.com.solicitudescrediya.api.utils.ValidatorUtils;
import co.com.solicitudescrediya.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.FieldError;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;


@Slf4j
@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanUseCase loanUseCase;
    private final LoanMapperDTO loanMapperDTO;
    private final ValidatorUtils validatorUtils;

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
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String token = request.headers().firstHeader("Authorization");

        return loanUseCase.getAllLoanRequestsGroupedByUser(token, page, size)
                .flatMap(result -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(result));
    }
}
