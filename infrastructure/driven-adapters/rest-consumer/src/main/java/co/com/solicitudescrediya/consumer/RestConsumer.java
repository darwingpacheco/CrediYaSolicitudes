package co.com.solicitudescrediya.consumer;

import co.com.solicitudescrediya.model.userGateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {

    private final WebClient client;

    public Mono<Boolean> existUserByEmail(String emailUser, String token) {
        return client.get()
                .uri("http://localhost:8081/api/v1/usuarios/email/{email}", emailUser)
                .header(HttpHeaders.AUTHORIZATION, token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(errorBody)))
                )
                .onStatus(HttpStatusCode::is5xxServerError, response ->
                        response.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("Error en autenticacion: " + errorBody)))
                )
                .bodyToMono(Void.class)
                .hasElement();
    }
}