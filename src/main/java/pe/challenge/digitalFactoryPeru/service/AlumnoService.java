package pe.challenge.digitalFactoryPeru.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import pe.challenge.digitalFactoryPeru.dto.AlumnoDTOs.AlumnoRequest;
import pe.challenge.digitalFactoryPeru.dto.AlumnoDTOs.AlumnoResponse;
import pe.challenge.digitalFactoryPeru.entity.Alumno;
import pe.challenge.digitalFactoryPeru.exception.AlumnosException;
import pe.challenge.digitalFactoryPeru.repository.AlumnoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AlumnoService {

    private final AlumnoRepository alumnoRepository;

    public Mono<Void> grabarAlumno(AlumnoRequest request) {
        return alumnoRepository.findById(request.id())
                .flatMap(existingAlumno ->
                        Mono.error(new AlumnosException("Id ya existe y no se pudo registrar al alumno."))
                )
                .switchIfEmpty(Mono.defer(() -> {
                    Alumno nuevoAlumno = Alumno.builder()
                            .id(request.id())
                            .nombre(request.nombre())
                            .apellido(request.apellido())
                            .estado(request.estado().toLowerCase())
                            .edad(request.edad())
                            .isNew(true)
                            .build();

                    return alumnoRepository.save(nuevoAlumno);
                }))
                .then();
    }


    public Flux<AlumnoResponse> obtenerAlumnosActivos() {
        return alumnoRepository.findByEstadoIgnoreCase("activo")
                .map(this::mapToResponse);
    }

    private AlumnoResponse mapToResponse(Alumno alumno) {
        return new AlumnoResponse(
                alumno.getId(),
                alumno.getNombre(),
                alumno.getApellido(),
                alumno.getEstado(),
                alumno.getEdad()
        );
    }
}

