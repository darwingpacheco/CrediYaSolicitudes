package co.com.solicitudescrediya.api.dto;

import java.math.BigDecimal;

public record LoanResponseDTO(
        String numberDocumentUser,
        BigDecimal amountLoan,
        Integer termLoan,
        String emailUser,
        Integer stateLoanId,
        Integer typeLoanId
) {

}