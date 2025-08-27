package co.com.solicitudescrediya.api.utils;

import co.com.solicitudescrediya.api.globalExceptions.ValidateExceptionHandler;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.SmartValidator;
import org.springframework.web.reactive.function.server.ServerRequest;
import reactor.core.publisher.Mono;

@AllArgsConstructor
@Component
public class ValidatorUtils {

    private final SmartValidator validator;

    public <T> Mono<T> validateRequestBody(ServerRequest request, Class<T> loanRequestDTOClass) {
        return request.bodyToMono(loanRequestDTOClass)
                .flatMap(body -> {
                            var errors = new BeanPropertyBindingResult(body, loanRequestDTOClass.getName());
                            validator.validate(body, errors);

                            if (errors.hasErrors()) {
                                return Mono.error(new ValidateExceptionHandler(errors));
                            }

                            return Mono.just(body);
                        }
                );
    }
}
