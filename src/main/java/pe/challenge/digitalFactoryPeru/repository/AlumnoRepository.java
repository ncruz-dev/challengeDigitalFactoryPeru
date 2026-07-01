package pe.challenge.digitalFactoryPeru.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import pe.challenge.digitalFactoryPeru.entity.Alumno;
import reactor.core.publisher.Flux;

public interface AlumnoRepository extends ReactiveCrudRepository<Alumno, String> {

    Flux<Alumno> findByEstadoIgnoreCase(String estado);
}

