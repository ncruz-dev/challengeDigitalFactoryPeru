package pe.challenge.digitalFactoryPeru.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.r2dbc.core.DatabaseClient;
import pe.challenge.digitalFactoryPeru.entity.Alumno;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@DataR2dbcTest
class AlumnoRepositoryTest {

    @Autowired private AlumnoRepository alumnoRepository;
    @Autowired private DatabaseClient databaseClient;

    @BeforeEach
    void limpiarBaseDeDatos() {
        databaseClient.sql("delete from alumnos").fetch().rowsUpdated().block();
    }

    @Test
    @DisplayName("Debe guardar y luego recuperar alumnos por estado activo")
    void testGuardarYBuscarActivos() {
        Alumno activo1 = Alumno.builder()
                .id("1")
                .nombre("Nicolas")
                .apellido("Cruz")
                .estado("activo")
                .edad(10)
                .isNew(true).build();
        Alumno inactivo = Alumno.builder()
                .id("2")
                .nombre("Rosa")
                .apellido("Cruz")
                .estado("inactivo")
                .edad(11)
                .isNew(true).build();

        // Guardamos de manera reactiva secuencial
        Mono<Void> setup = alumnoRepository.save(activo1)
                .then(alumnoRepository.save(inactivo))
                .then();

        Flux<Alumno> busqueda = setup.thenMany(alumnoRepository.findByEstadoIgnoreCase("activo"));

        StepVerifier.create(busqueda)
                .expectNextMatches(alumno -> {
                    Assertions.assertNotNull(alumno.getId());
                    return alumno.getId().equals("1") && alumno.getEstado().equals("activo");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe guardar un alumno y recuperarlo por ID")
    void guardarYRecuperarPorId() {
        Alumno alumno = Alumno.builder()
                .id("3")
                .nombre("Juan")
                .apellido("Cruz")
                .estado("activo")
                .edad(14)
                .isNew(true).build();

        Mono<Alumno> alumnoGuardado = alumnoRepository.save(alumno);
        Mono<Alumno> alumnoRecuperado = alumnoGuardado.flatMap(a -> alumnoRepository.findById("3"));

        StepVerifier.create(alumnoRecuperado)
                .assertNext(a -> {
                    assertThat(a.getId()).isEqualTo("3");
                    assertThat(a.getNombre()).isEqualTo("Juan");
                    assertThat(a.getApellido()).isEqualTo("Cruz");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("Debe retornar flujo vacío cuando busca estado diferente a activo o inactivo")
    void buscarPorEstadoSinResultados() {
        Alumno alumno = Alumno.builder()
                .id("4")
                .nombre("Karen")
                .apellido("Cruz")
                .estado("activo")
                .edad(13)
                .isNew(true).build();

        Mono<Void> setup = alumnoRepository.save(alumno).then();
        Flux<Alumno> busqueda = setup.thenMany(alumnoRepository.findByEstadoIgnoreCase("suspendido"));

        StepVerifier.create(busqueda)
                .verifyComplete();
    }

}

