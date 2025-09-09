package co.com.solicitudescrediya.consumer;

import co.com.solicitudescrediya.model.UserCheckResponse;
import co.com.solicitudescrediya.model.user.User;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {

    private final WebClient client;

    public Mono<UserCheckResponse> existUserByEmail(String emailUser, String token) {
        return client.get()
                .uri("http://localhost:8081/api/v1/usuarios/email/{email}", emailUser)
                .header(HttpHeaders.AUTHORIZATION,token)
                .exchangeToMono(response ->
                        response.bodyToMono(String.class)
                                .defaultIfEmpty("")
                                .map(body -> new UserCheckResponse(response.statusCode().value(), body))
                );
    }

    @Override
    public Mono<User> getUserByEmail(String token, String email) {
        return client.get()
        .uri("http://localhost:8081/api/v1/usuarios/all/{email}", email)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .bodyToMono(User.class);
    }
}