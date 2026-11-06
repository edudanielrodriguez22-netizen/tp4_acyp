CREATE DATABASE IF NOT EXISTS linguagest;
USE linguagest;

CREATE TABLE alumnos (
    alumno_id     INT AUTO_INCREMENT PRIMARY KEY,
    legajo        VARCHAR(15) NOT NULL UNIQUE,
    nombre        VARCHAR(60) NOT NULL,
    apellido      VARCHAR(60) NOT NULL,
    email         VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE cursos (
    curso_id      INT AUTO_INCREMENT PRIMARY KEY,
    idioma        VARCHAR(40) NOT NULL,
    nivel         VARCHAR(20) NOT NULL,
    cupo_maximo   INT NOT NULL,
    modalidad     ENUM('presencial', 'virtual') NOT NULL
);

CREATE TABLE inscripciones (
    inscripcion_id     INT AUTO_INCREMENT PRIMARY KEY,
    alumno_id          INT NOT NULL,
    curso_id           INT NOT NULL,
    fecha_inscripcion  DATE NOT NULL,
    estado             ENUM('activa', 'finalizada', 'baja') NOT NULL DEFAULT 'activa',
    CONSTRAINT fk_inscripcion_alumno
        FOREIGN KEY (alumno_id) REFERENCES alumnos(alumno_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT fk_inscripcion_curso
        FOREIGN KEY (curso_id) REFERENCES cursos(curso_id)
        ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT uq_alumno_curso UNIQUE (alumno_id, curso_id)
);

CREATE TABLE asistencias (
    asistencia_id   INT AUTO_INCREMENT PRIMARY KEY,
    inscripcion_id  INT NOT NULL,
    fecha_clase     DATE NOT NULL,
    presente        BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_asistencia_inscripcion
        FOREIGN KEY (inscripcion_id) REFERENCES inscripciones(inscripcion_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE calificaciones (
    calificacion_id     INT AUTO_INCREMENT PRIMARY KEY,
    inscripcion_id      INT NOT NULL,
    fecha_evaluacion    DATE NOT NULL,
    nota                DECIMAL(4,2) NOT NULL,
    CONSTRAINT fk_calificacion_inscripcion
        FOREIGN KEY (inscripcion_id) REFERENCES inscripciones(inscripcion_id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT chk_nota_rango CHECK (nota >= 0 AND nota <= 10)
);
