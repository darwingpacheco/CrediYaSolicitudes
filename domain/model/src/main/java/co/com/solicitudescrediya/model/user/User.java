package co.com.solicitudescrediya.model.user;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
//import lombok.NoArgsConstructor;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class User {
    private Long userID;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String numberDocument;
    private String phone;
    private LocalDate dateBirth;
    private String address;
    private int idRol;
    private BigDecimal baseSalary;
}
