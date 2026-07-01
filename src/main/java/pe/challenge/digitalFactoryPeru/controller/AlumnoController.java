package pe.challenge.digitalFactoryPeru.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pe.challenge.digitalFactoryPeru.dto.AlumnoDTOs.AlumnoRequest;
import pe.challenge.digitalFactoryPeru.dto.AlumnoDTOs.AlumnoResponse;
import pe.challenge.digitalFactoryPeru.service.AlumnoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/alumnos")
@CrossOrigin("*")
@RequiredArgsConstructor
public class AlumnoController {

    private final AlumnoService alumnoService;

    @PostMapping
    public Mono<ResponseEntity<Void>> grabarAlumno(@Valid @RequestBody AlumnoRequest request) {
        return alumnoService.grabarAlumno(request)
                .then(Mono.just(ResponseEntity.status(HttpStatus.CREATED).build()));
    }

    @GetMapping("/activos")
    public Flux<AlumnoResponse> obtenerAlumnosActivos() {
        return alumnoService.obtenerAlumnosActivos();
    }
}
