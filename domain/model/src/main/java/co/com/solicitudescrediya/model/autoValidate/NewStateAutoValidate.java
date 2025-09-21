package co.com.solicitudescrediya.model.autoValidate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder(toBuilder = true)
public class NewStateAutoValidate {
    private int idLoan;
    private int status;
    private String decision;
}
