create table if not exists alumnos (
    id varchar(20) primary key,
    nombre varchar(50) not null,
    apellido varchar(50) not null,
    estado varchar(10) not null,
    edad int not null
    );

delete from alumnos;

insert into alumnos (id, nombre, apellido, estado, edad) values
('1', 'Nicolas', 'Cruz', 'activo', 20),
('2', 'Maria', 'Cruz', 'activo', 20);