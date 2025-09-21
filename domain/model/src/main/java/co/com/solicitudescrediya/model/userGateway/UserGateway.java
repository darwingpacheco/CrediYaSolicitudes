package co.com.solicitudescrediya.model.userGateway;

import co.com.solicitudescrediya.model.UserCheckResponse;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.user.User;
import com.sun.net.httpserver.HttpServer;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<UserCheckResponse> existUserByEmail(String identifyUrl, String emailUser, String token);

    Mono<User> getUserByEmail(String token, String email, String identifyUrl);
}
