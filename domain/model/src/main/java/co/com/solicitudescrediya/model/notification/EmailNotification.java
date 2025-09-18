package co.com.solicitudescrediya.model.notification;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class EmailNotification {

    Long idLoan;
    String status;
    String email;
    String numberDocument;
    BigDecimal loanAmount;
    String loanType;
    String infoMessage;
}
