package co.com.solicitudescrediya.model.userGateway;

import co.com.solicitudescrediya.model.UserCheckResponse;
import co.com.solicitudescrediya.model.user.User;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<UserCheckResponse> existUserByEmail(String emailUser, String token);

    Mono<User> getUserByEmail(String token, String email);
}
