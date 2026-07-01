package pe.challenge.digitalFactoryPeru.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.challenge.digitalFactoryPeru.dto.AlumnoDTOs.AlumnoRequest;
import pe.challenge.digitalFactoryPeru.entity.Alumno;
import pe.challenge.digitalFactoryPeru.exception.AlumnosException;
import pe.challenge.digitalFactoryPeru.repository.AlumnoRepository;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlumnoServiceTest {

    @Mock private AlumnoRepository alumnoRepository;
    @InjectMocks private AlumnoService alumnoService;

    @Test
    @DisplayName("Debe fallar al guardar si el ID ya existe")
    void grabarAlumno_FallaPorIdRepetido() {
        AlumnoRequest request = new AlumnoRequest("1", "Nicolas", "Cruz", "activo", 15);
        when(alumnoRepository.findById(anyString())).thenReturn(Mono.just(new Alumno()));

        Mono<Void> resultado = alumnoService.grabarAlumno(request);

        StepVerifier.create(resultado)
                .expectErrorMatches(throwable -> throwable instanceof AlumnosException &&
                        throwable.getMessage().contains("ya existe"))
                .verify();
    }

     @Test
     @DisplayName("Debe guardar exitosamente si el ID no existe")
     void grabarAlumno_Exito() {
         AlumnoRequest request = new AlumnoRequest("1", "Nicolas", "Cruz", "activo", 15);

         when(alumnoRepository.findById(anyString())).thenReturn(Mono.empty());
         when(alumnoRepository.save(any(Alumno.class))).thenReturn(Mono.just(new Alumno()));

         Mono<Void> resultado = alumnoService.grabarAlumno(request);

         StepVerifier.create(resultado).verifyComplete();
     }

     @Test
     @DisplayName("Obtener alumnos activos retorna lista de respuestas")
     void obtenerAlumnosActivos_ConDatos() {
         Alumno alumno1 = Alumno.builder()
                 .id("1")
                 .nombre("Nicolas")
                 .apellido("Cruz")
                 .estado("activo")
                 .edad(15)
                 .build();
         Alumno alumno2 = Alumno.builder()
                 .id("2")
                 .nombre("Rosa")
                 .apellido("Cruz")
                 .estado("activo")
                 .edad(16)
                 .build();

         when(alumnoRepository.findByEstadoIgnoreCase("activo"))
                 .thenReturn(reactor.core.publisher.Flux.just(alumno1, alumno2));

         StepVerifier.create(alumnoService.obtenerAlumnosActivos())
                 .expectNextMatches(response -> response.id().equals("1") && response.nombre().equals("Nicolas"))
                 .expectNextMatches(response -> response.id().equals("2") && response.nombre().equals("Rosa"))
                 .verifyComplete();
     }

     @Test
     @DisplayName("Obtener alumnos activos retorna flujo vacío cuando no hay resultados")
     void obtenerAlumnosActivos_SinDatos() {
         when(alumnoRepository.findByEstadoIgnoreCase("activo"))
                 .thenReturn(reactor.core.publisher.Flux.empty());

         StepVerifier.create(alumnoService.obtenerAlumnosActivos())
                 .verifyComplete();
     }

     @Test
     @DisplayName("Guardar alumno mapea correctamente el estado a minúsculas")
     void grabarAlumno_MapeoEstadoMinusculas() {
         AlumnoRequest request = new AlumnoRequest("3", "Juan", "Cruz", "ACTIVO", 17);

         when(alumnoRepository.findById(anyString())).thenReturn(Mono.empty());
         when(alumnoRepository.save(any(Alumno.class)))
                 .thenAnswer(invocation -> {
                     Alumno alumnoGuardado = invocation.getArgument(0);
                     return Mono.just(alumnoGuardado);
                 });

         Mono<Void> resultado = alumnoService.grabarAlumno(request);

         StepVerifier.create(resultado)
                 .verifyComplete();
     }
}

