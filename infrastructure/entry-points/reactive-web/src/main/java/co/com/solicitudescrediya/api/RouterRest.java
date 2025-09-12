package co.com.solicitudescrediya.api;

import co.com.solicitudescrediya.api.dto.LoanRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@RequiredArgsConstructor
public class RouterRest {

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/v1/solicitudes",
                    produces = {"application/json"},
                    method = RequestMethod.POST,
                    beanClass = Handler.class,
                    beanMethod = "createLoan",
                    operation = @Operation(
                            operationId = "createLoan",
                            summary = "Create new loan for client",
                            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                                    required = true,
                                    description = "User object to create",
                                    content = @Content(
                                            mediaType = "application/json",
                                            schema = @Schema(implementation = LoanRequestDTO.class)
                                    )
                            ),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Successful operation"),
                                    @ApiResponse(responseCode = "400", description = "Bad request"),
                                    @ApiResponse(responseCode = "500", description = "Internal server error")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/v1/solicitud",
                    produces = {"application/json"},
                    method = RequestMethod.GET,
                    beanClass = Handler.class,
                    beanMethod = "getAllLoanRequests",
                    operation = @Operation(
                            operationId = "getAllLoanRequests",
                            summary = "Retrieve all loan requests",
                            description = "Devuelve todas las solicitudes registradas en el sistema con paginación. " +
                                    "Ejemplo de uso: http://localhost:8080/api/v1/solicitud?page=0&size=10",
                            parameters = {
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "page",
                                            description = "Número de la página a recuperar (0 por defecto)",
                                            required = false,
                                            example = "0"
                                    ),
                                    @io.swagger.v3.oas.annotations.Parameter(
                                            name = "size",
                                            description = "Cantidad de elementos por página (10 por defecto)",
                                            required = false,
                                            example = "10"
                                    )
                            },
                            responses = {
                                    @ApiResponse(
                                            responseCode = "200",
                                            description = "Listado de solicitudes",
                                            content = @Content(
                                                    mediaType = "application/json",
                                                    schema = @Schema(example = "{\n" +
                                                            "    \"content\": [\n" +
                                                            "        {\n" +
                                                            "            \"name\": \"Yury Pacheco\",\n" +
                                                            "            \"email\": \"yurypacheco@gmail.com\",\n" +
                                                            "            \"numberDocument\": \"60417625\",\n" +
                                                            "            \"baseSalary\": 1470000.00,\n" +
                                                            "            \"loanRequests\": [\n" +
                                                            "                {\n" +
                                                            "                    \"loanType\": \"Préstamo Vehicular\",\n" +
                                                            "                    \"loanState\": \"PENDIENTE\",\n" +
                                                            "                    \"termLoan\": 12,\n" +
                                                            "                    \"amountLoan\": 25000000.00\n" +
                                                            "                }\n" +
                                                            "            ],\n" +
                                                            "            \"totalMonthlyDebt\": 0\n" +
                                                            "        }\n" +
                                                            "    ],\n" +
                                                            "    \"page\": 0,\n" +
                                                            "    \"size\": 10,\n" +
                                                            "    \"totalElements\": 3,\n" +
                                                            "    \"totalPages\": 1\n" +
                                                            "}"
                                                    )
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "403",
                                            description = "No tiene permisos para acceder a este recurso",
                                            content = @Content(
                                                    schema = @Schema(example = "{\n" +
                                                            "    \"error\": \"Forbidden\",\n" +
                                                            "    \"message\": \"No tiene permisos para acceder a este recurso\",\n" +
                                                            "    \"status\": 403\n" +
                                                            "}")
                                            )
                                    ),
                                    @ApiResponse(
                                            responseCode = "409",
                                            description = "Token no válido",
                                            content = @Content(
                                                    schema = @Schema(example = "{\n" +
                                                            "    \"error\": \"Conflict\",\n" +
                                                            "    \"message\": \"Token no valido\",\n" +
                                                            "    \"status\": 409\n" +
                                                            "}")
                                            )
                                    )
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routerFunction(Handler handler) {
        return route(POST("/api/v1/solicitudes"), handler::createLoan)
                .andRoute(GET("/api/v1/solicitud"), handler::getAllLoanRequests);
    }
}
