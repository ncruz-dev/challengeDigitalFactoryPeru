package pe.challenge.digitalFactoryPeru.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("alumnos")
public class Alumno implements Persistable<String> {

    @Id
    private String id;

    private String nombre;
    private String apellido;
    private String estado;
    private Integer edad;

    @Transient
    @Builder.Default
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew || id == null;
    }
}

