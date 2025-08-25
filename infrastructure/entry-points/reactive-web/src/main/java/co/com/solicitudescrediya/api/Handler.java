package co.com.solicitudescrediya.api;

import co.com.solicitudescrediya.api.dto.LoanRequestDTO;
import co.com.solicitudescrediya.api.mapper.LoanMapperDTO;
import co.com.solicitudescrediya.usecase.loan.LoanUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class Handler {

    private final LoanUseCase loanUseCase;
    private final LoanMapperDTO loanMapperDTO;
    private final jakarta.validation.Validator validator;

    public Mono<ServerResponse> createLoan(ServerRequest request) {
        return request.bodyToMono(LoanRequestDTO.class)
                .flatMap(dto -> {
                    var validateFields = validator.validate(dto);
                    if (!validateFields.isEmpty()) {
                        String errorMsg = validateFields.stream()
                                .map(v -> v.getPropertyPath() + ": " + v.getMessage())
                                .collect(Collectors.joining(", "));
                        return Mono.error(new IllegalArgumentException(errorMsg));
                    }

                    return loanUseCase.createLoan(loanMapperDTO.toLoan(dto))
                            .flatMap(loan -> ServerResponse.ok().bodyValue(loanMapperDTO.toLoanResponseDTO(loan)));
                });
    }
}
