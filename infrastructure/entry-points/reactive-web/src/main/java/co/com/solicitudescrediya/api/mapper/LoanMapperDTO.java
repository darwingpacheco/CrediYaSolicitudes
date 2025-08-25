package co.com.solicitudescrediya.api.mapper;

import co.com.solicitudescrediya.api.dto.LoanRequestDTO;
import co.com.solicitudescrediya.api.dto.LoanResponseDTO;
import co.com.solicitudescrediya.model.loan.Loan;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoanMapperDTO {

    Loan toLoan(LoanRequestDTO loanRequestDTO);

    LoanResponseDTO toLoanResponseDTO(Loan loan);
}
