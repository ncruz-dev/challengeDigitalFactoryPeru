package pe.challenge.digitalFactoryPeru.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public interface AlumnoDTOs {

    record AlumnoRequest(
            @NotBlank(message = "El ID es obligatorio")
            String id,

            @NotBlank(message = "El nombre es obligatorio")
            String nombre,

            @NotBlank(message = "El apellido es obligatorio")
            String apellido,

            @NotBlank(message = "El estado es obligatorio")
            @Pattern(regexp = "^(activo|inactivo)$", flags = Pattern.Flag.CASE_INSENSITIVE, message = "El estado debe ser 'activo' o 'inactivo'")
            String estado,

            @NotNull(message = "La edad no puede ser nula")
            @Min(value = 5, message = "La edad mínima es 5 años")
            @Max(value = 100, message = "La edad máxima es 100 años")
            Integer edad
    ) {}

    record AlumnoResponse(
            String id,
            String nombre,
            String apellido,
            String estado,
            Integer edad
    ) {}
}