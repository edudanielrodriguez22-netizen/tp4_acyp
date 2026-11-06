set -e
git rm -q --ignore-unmatch download
mkdir -p datos && git mv importar_ejemplo.csv datos/importar_ejemplo.csv
mkdir -p sql && git mv 01_crear_tablas.sql sql/01_crear_tablas.sql
mkdir -p sql && git mv 02_datos_prueba.sql sql/02_datos_prueba.sql
mkdir -p sql && git mv 03_consultas.sql sql/03_consultas.sql
mkdir -p src/main/java/linguagest && git mv Main.java src/main/java/linguagest/Main.java
mkdir -p src/main/java/linguagest/control && git mv GestorAlumnos.java src/main/java/linguagest/control/GestorAlumnos.java
mkdir -p src/main/java/linguagest/control && git mv GestorAsistencia.java src/main/java/linguagest/control/GestorAsistencia.java
mkdir -p src/main/java/linguagest/control && git mv GestorInscripciones.java src/main/java/linguagest/control/GestorInscripciones.java
mkdir -p src/main/java/linguagest/dao && git mv AlumnoDAO.java src/main/java/linguagest/dao/AlumnoDAO.java
mkdir -p src/main/java/linguagest/dao && git mv AsistenciaDAO.java src/main/java/linguagest/dao/AsistenciaDAO.java
mkdir -p src/main/java/linguagest/dao && git mv CalificacionDAO.java src/main/java/linguagest/dao/CalificacionDAO.java
mkdir -p src/main/java/linguagest/dao && git mv CursoDAO.java src/main/java/linguagest/dao/CursoDAO.java
mkdir -p src/main/java/linguagest/dao && git mv InscripcionDAO.java src/main/java/linguagest/dao/InscripcionDAO.java
mkdir -p src/main/java/linguagest/db && git mv ConexionBD.java src/main/java/linguagest/db/ConexionBD.java
mkdir -p src/main/java/linguagest/excepcion && git mv CupoExcepcion.java src/main/java/linguagest/excepcion/CupoExcepcion.java
mkdir -p src/main/java/linguagest/excepcion && git mv PersistenciaException.java src/main/java/linguagest/excepcion/PersistenciaException.java
mkdir -p src/main/java/linguagest/model && git mv Alumno.java src/main/java/linguagest/model/Alumno.java
mkdir -p src/main/java/linguagest/model && git mv Curso.java src/main/java/linguagest/model/Curso.java
mkdir -p src/main/java/linguagest/model && git mv CursoPresencial.java src/main/java/linguagest/model/CursoPresencial.java
mkdir -p src/main/java/linguagest/model && git mv CursoVirtual.java src/main/java/linguagest/model/CursoVirtual.java
mkdir -p src/main/java/linguagest/model && git mv Describible.java src/main/java/linguagest/model/Describible.java
mkdir -p src/main/java/linguagest/model && git mv Instituto.java src/main/java/linguagest/model/Instituto.java
mkdir -p src/main/java/linguagest/mvc/controlador && git mv ControladorCalificaciones.java src/main/java/linguagest/mvc/controlador/ControladorCalificaciones.java
mkdir -p src/main/java/linguagest/mvc/modelo && git mv ArchivoCalificaciones.java src/main/java/linguagest/mvc/modelo/ArchivoCalificaciones.java
mkdir -p src/main/java/linguagest/mvc/modelo && git mv Calificacion.java src/main/java/linguagest/mvc/modelo/Calificacion.java
mkdir -p src/main/java/linguagest/mvc/modelo && git mv ModeloCalificaciones.java src/main/java/linguagest/mvc/modelo/ModeloCalificaciones.java
mkdir -p src/main/java/linguagest/mvc/vista && git mv VistaCalificaciones.java src/main/java/linguagest/mvc/vista/VistaCalificaciones.java
mkdir -p src/main/java/linguagest/mvc/vista && git mv VistaConsola.java src/main/java/linguagest/mvc/vista/VistaConsola.java
mkdir -p src/main/java/linguagest/util && git mv Algoritmos.java src/main/java/linguagest/util/Algoritmos.java
mkdir -p src/main/resources && git mv config.properties src/main/resources/config.properties
cat > .gitignore <<'EOF'
# Archivos generados al compilar
target/
out/
*.class

# Configuracion de IDEs
.idea/
.vscode/
*.iml
.classpath
.project
.settings/

# Archivos generados por el modulo de calificaciones
datos/calificaciones_curso_*.csv
datos/importacion_errores.log
EOF
git add -A && git commit -qm "Etapa 4: ordenar estructura de carpetas" && git push && ls
