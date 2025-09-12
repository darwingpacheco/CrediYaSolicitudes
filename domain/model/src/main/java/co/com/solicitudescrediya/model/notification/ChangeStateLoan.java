package co.com.solicitudescrediya.model.notification;
import lombok.*;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ChangeStateLoan {
    int idApplication;
    int idState;
}
