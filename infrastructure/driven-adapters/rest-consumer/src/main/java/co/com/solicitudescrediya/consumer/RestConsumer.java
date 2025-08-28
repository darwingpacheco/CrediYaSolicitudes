package co.com.solicitudescrediya.consumer;

import co.com.solicitudescrediya.model.userGateway.UserGateway;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class RestConsumer implements UserGateway {

    private final WebClient client;

    @Override
    public Mono<Boolean> existUserByEmail(String emailUser) {
        return client.get()
                .uri("http://localhost:8081/api/v1/usuarios/email/{email}", emailUser)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode().is2xxSuccessful())
                .onErrorResume(error -> Mono.just(false));
    }
}