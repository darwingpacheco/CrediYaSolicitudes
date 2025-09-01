package co.com.solicitudescrediya.usecase.loan;

import co.com.solicitudescrediya.model.adapterExceptionApi.ApiError;
import co.com.solicitudescrediya.model.loan.Loan;
import co.com.solicitudescrediya.model.loan.gateways.LoanRepository;
import co.com.solicitudescrediya.model.stateloan.gateways.StateLoanRepository;
import co.com.solicitudescrediya.model.typeloan.gateways.TypeLoanRepository;
import co.com.solicitudescrediya.model.userGateway.UserGateway;
import co.com.solicitudescrediya.usecase.loan.conflictException.ConflictException;
import co.com.solicitudescrediya.usecase.loan.conflictException.CustomException;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class LoanUseCase {

    private final LoanRepository loanRepository;
    private final StateLoanRepository stateLoanRepository;
    private final TypeLoanRepository typeLoanRepository;
    private final UserGateway userGateway;

    public Mono<Loan> createLoan(Loan loan, String token) {
        String emailUser = loan.getEmailUser();
        return userGateway.existUserByEmail(emailUser, token)
                .flatMap(userResponse -> {

                    int status = userResponse.statusCode();
                    String message = userResponse.body();

                    if (status == 401)
                        return Mono.error(new CustomException(new ApiError("Unauthorized", message, 401)));
                    else if (status == 403)
                        return Mono.error(new CustomException(new ApiError("Forbiden", message, 403)));
                    else if (status == 404 || message.equalsIgnoreCase("USER_NOTFOUND"))
                        return Mono.error(new ConflictException("El usuario: " + emailUser + " no existe"));

                    loan.setStateLoanId(1);
                    int idType = loan.getTypeLoanId();
                    Mono<Boolean> stateCheck = stateLoanRepository.findByLoanId(loan.getStateLoanId());
                    Mono<Boolean> typeCheck = typeLoanRepository.findByLoanType(idType);
                    Mono<Boolean> valueInRange = typeLoanRepository.findValueRange(idType, loan.getAmountLoan());

                    return Mono.zip(stateCheck, typeCheck, valueInRange)
                            .flatMap(tuple -> {
                                Boolean stateExists = tuple.getT1();
                                Boolean typeExists = tuple.getT2();
                                Boolean valueExist = tuple.getT3();

                                if (!stateExists)
                                    return Mono.error(new ConflictException("En este momento no es posible asignarte un estado de préstamo"));

                                if (!typeExists)
                                    return Mono.error(new ConflictException("No existe el tipo de préstamo solicitado"));

                                if (!valueExist)
                                    return Mono.error(new ConflictException("El monto ingresado no esta permitido"));

                                return loanRepository.createLoan(loan);
                            });
                });
    }
}
