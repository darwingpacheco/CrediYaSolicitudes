package co.com.solicitudescrediya.consumer;

import co.com.solicitudescrediya.model.UserCheckResponse;
import co.com.solicitudescrediya.model.adapterExceptionApi.ApiError;
import co.com.solicitudescrediya.model.user.User;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import co.com.solicitudescrediya.usecase.loan.conflictException.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {

    private final WebClient client;

    public Mono<UserCheckResponse> existUserByEmail(String identifyUrl, String emailUser, String token) {
        return client.get()
                .uri("http://localhost:8081/api/v1/usuarios/{identifyUrl}/email/{email}", identifyUrl,  emailUser)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchangeToMono(response ->
                        response.bodyToMono(Map.class)
                                .defaultIfEmpty(Map.of())
                                .map(body -> new UserCheckResponse(response.statusCode().value(), (String) body.getOrDefault("message", "")))
                );
    }

    @Override
    public Mono<User> getUserByEmail(String token, String email) {
        return client.get()
                .uri("http://localhost:8081/api/v1/usuarios/all/{email}", email)
                .header(HttpHeaders.AUTHORIZATION, token)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(User.class);
                    } else {
                        return response.bodyToMono(Map.class)
                                .defaultIfEmpty(Map.of())
                                .flatMap(body -> {
                                    ApiError apiError = new ApiError();
                                    apiError.setStatus(response.statusCode().value());
                                    String authMessage = (String) body.getOrDefault("message", "");
                                    apiError.setMessage(authMessage);
                                    apiError.setError((String) body.getOrDefault("error", "Undefined"));

                                    return Mono.error(new CustomException(apiError));
                                });
                    }
                });
    }
}