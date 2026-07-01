package pe.challenge.digitalFactoryPeru.controller;


import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import pe.challenge.digitalFactoryPeru.dto.AlumnoDTOs.AlumnoRequest;
import pe.challenge.digitalFactoryPeru.dto.AlumnoDTOs.AlumnoResponse;
import pe.challenge.digitalFactoryPeru.exception.AlumnosException;
import pe.challenge.digitalFactoryPeru.service.AlumnoService;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class AlumnoControllerTest {

    @Autowired private WebTestClient webTestClient;
    @MockitoBean private AlumnoService alumnoService;

    @Test
    @DisplayName("POST si es exitoso debe retornar 201 sin body")
    void grabarAlumnoEndpoint() {
        AlumnoRequest request = new AlumnoRequest("1", "Nicolas", "Cruz", "activo", 15);
        when(alumnoService.grabarAlumno(any())).thenReturn(Mono.empty());

        webTestClient.post()
                .uri("/api/alumnos")
                .bodyValue(request)
                .exchange()
                .expectStatus().isCreated()
                .expectBody().isEmpty();
    }

    @Test
    @DisplayName("POST debe retornar Error 400 por validación DTO")
    void grabarAlumnoEndpoint_ValidacionFalla() {
        // Edad incorrecta (menor a 5) y estado incorrecto
        AlumnoRequest request = new AlumnoRequest("1", "Nicolas", "Cruz", "desconocido", 1);

        webTestClient.post()
                .uri("/api/alumnos")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.edad").exists()
                .jsonPath("$.estado").exists();
    }

    @Test
    @DisplayName("POST debe retornar Error 400 cuando el ID del alumno ya existe")
    void grabarAlumnoEndpoint_IdDuplicado() {
        AlumnoRequest request = new AlumnoRequest("1", "Nicolas", "Cruz", "activo", 15);
        when(alumnoService.grabarAlumno(any()))
                .thenReturn(Mono.error(new AlumnosException("No se pudo registrar al alumno: El Id '1' ya existe.")));

        webTestClient.post()
                .uri("/api/alumnos")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.error").exists();
    }

    @Test
    @DisplayName("POST falla cuando el ID del alumno está vacío")
    void grabarAlumnoEndpoint_IdVacio() {
        AlumnoRequest request = new AlumnoRequest("", "Nicolas", "Cruz", "activo", 15);

        webTestClient.post()
                .uri("/api/alumnos")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("POST falla cuando el nombre del alumno está vacío")
    void grabarAlumnoEndpoint_NombreVacio() {
        AlumnoRequest request = new AlumnoRequest("1", "", "Cruz", "activo", 15);

        webTestClient.post()
                .uri("/api/alumnos")
                .bodyValue(request)
                .exchange()
                .expectStatus().isBadRequest();
    }

    @Test
    @DisplayName("GET debe retornar lista de alumnos activos")
    void obtenerAlumnosActivosEndpoint_ConDatos() {
        AlumnoResponse alumno1 = new AlumnoResponse("1", "Nicolas", "Cruz", "activo", 15);
        AlumnoResponse alumno2 = new AlumnoResponse("2", "Rosa", "Cruz", "activo", 16);

        when(alumnoService.obtenerAlumnosActivos())
                .thenReturn(Flux.just(alumno1, alumno2));

        webTestClient.get()
                .uri("/api/alumnos/activos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AlumnoResponse.class)
                .hasSize(2)
                .contains(alumno1, alumno2);
    }

    @Test
    @DisplayName("GET debe retornar lista vacía cuando no hay alumnos activos")
    void obtenerAlumnosActivosEndpoint_SinDatos() {
        when(alumnoService.obtenerAlumnosActivos())
                .thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/alumnos/activos")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(AlumnoResponse.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("GET retorna status OK con tipo de contenido correcto")
    void obtenerAlumnosActivosEndpoint_VerificaContentType() {
        AlumnoResponse alumno = new AlumnoResponse("1", "Nicolas", "Cruz", "activo", 15);

        when(alumnoService.obtenerAlumnosActivos())
                .thenReturn(Flux.just(alumno));

        webTestClient.get()
                .uri("/api/alumnos/activos")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith("application/json");
    }
}
