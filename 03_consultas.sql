USE linguagest;

-- Consulta 1: Alumnos inscriptos en un curso especifico
SELECT a.legajo, a.nombre, a.apellido, c.idioma, c.nivel
FROM alumnos a
JOIN inscripciones i ON a.alumno_id = i.alumno_id
JOIN cursos c ON i.curso_id = c.curso_id
WHERE c.curso_id = 1;

-- Consulta 2: Historial de asistencia de un alumno
SELECT al.nombre, al.apellido, c.idioma, ast.fecha_clase, ast.presente
FROM asistencias ast
JOIN inscripciones i ON ast.inscripcion_id = i.inscripcion_id
JOIN alumnos al ON i.alumno_id = al.alumno_id
JOIN cursos c ON i.curso_id = c.curso_id
WHERE al.alumno_id = 1;

-- Consulta 3: Promedio de notas por curso
SELECT c.idioma, c.nivel, AVG(cal.nota) AS promedio
FROM calificaciones cal
JOIN inscripciones i ON cal.inscripcion_id = i.inscripcion_id
JOIN cursos c ON i.curso_id = c.curso_id
GROUP BY c.curso_id;

-- Consulta 4: Cursos con cupo disponible
SELECT c.curso_id, c.idioma, c.nivel, c.cupo_maximo,
       COUNT(i.inscripcion_id) AS inscriptos_actuales,
       (c.cupo_maximo - COUNT(i.inscripcion_id)) AS lugares_disponibles
FROM cursos c
LEFT JOIN inscripciones i
       ON c.curso_id = i.curso_id AND i.estado = 'activa'
GROUP BY c.curso_id
HAVING lugares_disponibles > 0;
