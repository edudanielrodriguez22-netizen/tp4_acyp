USE linguagest;

INSERT INTO alumnos (legajo, nombre, apellido, email) VALUES
('LG-1001', 'Martina', 'Gómez', 'martina.gomez@mail.com'),
('LG-1002', 'Lucas', 'Fernández', 'lucas.fernandez@mail.com'),
('LG-1003', 'Sofía', 'Ramírez', 'sofia.ramirez@mail.com');

INSERT INTO cursos (idioma, nivel, cupo_maximo, modalidad) VALUES
('Inglés', 'B1', 20, 'presencial'),
('Portugués', 'A2', 15, 'virtual'),
('Francés', 'A1', 18, 'presencial');

INSERT INTO inscripciones (alumno_id, curso_id, fecha_inscripcion, estado) VALUES
(1, 1, '2026-03-02', 'activa'),
(2, 1, '2026-03-02', 'activa'),
(3, 2, '2026-03-03', 'activa');

INSERT INTO asistencias (inscripcion_id, fecha_clase, presente) VALUES
(1, '2026-03-09', TRUE),
(1, '2026-03-16', FALSE),
(2, '2026-03-09', TRUE);

INSERT INTO calificaciones (inscripcion_id, fecha_evaluacion, nota) VALUES
(1, '2026-04-10', 8.50),
(2, '2026-04-10', 6.75);
