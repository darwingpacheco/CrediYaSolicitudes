package co.com.solicitudescrediya.r2dbc.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("estados")
@Data
public class LoanStateEntity {

    @Id
    @Column("id_estado")
    private Long id;

    @Column("nombre")
    private String stateName;

    @Column("descripcion")
    private String stateDescription;
}
