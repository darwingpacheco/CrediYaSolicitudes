package co.com.solicitudescrediya.model.stateloan;
import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanState {
    private Integer id;
    private String stateName;
    private String stateDescription;
}
