package co.com.solicitudescrediya.model.userGateway;

import co.com.solicitudescrediya.model.loan.Loan;
import reactor.core.publisher.Mono;

public interface UserGateway {
    Mono<Boolean> existUserByEmail(String emailUser);
}
