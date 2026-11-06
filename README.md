# LinguaGest — Prototipo

Prototipo funcional en Java con persistencia en MySQL del sistema de gestión académica
para el Instituto de Idiomas, desarrollado como parte del Trabajo Práctico de
Análisis y Diseño de Software (Etapas 1 y 2, PUD).

## Requisitos

- JDK 17 o superior
- MySQL 8.0 o superior
- Maven (o un IDE que lo gestione: IntelliJ, Eclipse, NetBeans)

## 1. Crear la base de datos

Ejecutar en MySQL, en este orden:

```bash
mysql -u root -p < sql/01_crear_tablas.sql
mysql -u root -p < sql/02_datos_prueba.sql
```

(`sql/03_consultas.sql` contiene las consultas documentadas en el informe, para probarlas manualmente si se desea.)

## 2. Configurar la conexión

Editar `src/main/resources/config.properties` con el usuario y la contraseña de tu MySQL local:

```properties
db.url=jdbc:mysql://localhost:3306/linguagest?useSSL=false&serverTimezone=UTC
db.user=root
db.password=TU_PASSWORD_AQUI
```

## 3. Compilar y ejecutar

Con Maven:

```bash
mvn clean package
java -jar target/linguagest.jar
```

(El `pom.xml` genera un jar ejecutable que incluye el driver de MySQL.)

O bien, importar el proyecto como Maven Project en tu IDE y ejecutar la clase `linguagest.Main`.

## Estructura del proyecto

```
linguagest-java/
├── pom.xml
├── sql/
│   ├── 01_crear_tablas.sql
│   ├── 02_datos_prueba.sql
│   └── 03_consultas.sql
└── src/main/
    ├── java/linguagest/
    │   ├── Main.java                  → menú de consola (prototipo operacional)
    │   ├── model/                     → clases de entidad (Alumno, Curso, CursoPresencial, CursoVirtual, Instituto)
    │   ├── db/ConexionBD.java         → conexión JDBC
    │   ├── dao/                       → acceso a datos (una clase por tabla)
    │   └── control/GestorAsistencia.java → clase de control (caso de uso "Registrar asistencia")
    └── resources/config.properties
```

## Correspondencia con el informe (Etapa 2)

| Documento | Código |
|---|---|
| Diagrama de secuencia (CU "Registrar asistencia") | `control/GestorAsistencia.java` |
| Diagrama de clases de diseño | `model/Curso.java`, `CursoPresencial.java`, `CursoVirtual.java`, `Alumno.java`, `Instituto.java` |
| Creación de tablas MySQL | `sql/01_crear_tablas.sql` |
| Inserción / consulta / borrado | `sql/02_datos_prueba.sql`, métodos de los DAO |
| Presentación de consultas SQL | `sql/03_consultas.sql`, métodos `listarInscriptosPorCurso`, `historialPorAlumno`, `promedioPorCurso`, `cursosConCupoDisponible` |

## Etapa 3: conceptos de POO aplicados

| Concepto | Donde |
|---|---|
| Encapsulamiento | Atributos `private`/`protected` con getters/setters; `Curso.validarCupo()` impide cupos <= 0 |
| Herencia | `CursoPresencial` y `CursoVirtual` extienden `Curso` (uso de `super(...)`) |
| Abstraccion | `Curso` es abstracta y declara `registrarAsistencia()`; la interfaz `Describible` (implementada por `Curso` y `Alumno`) define `getDescripcion()` |
| Polimorfismo | `GestorAsistencia` invoca `curso.registrarAsistencia()` y cada subclase responde distinto; la opcion 14 recorre una `List<Describible>` con cursos y alumnos |
| Excepcion propia | `excepcion/CupoExcepcion.java`, lanzada por `GestorInscripciones.inscribir()` |
| try/catch en el menu | `leerEntero()`/`leerDouble()` (NumberFormatException) y `Main.main()` (SQLException, IllegalArgumentException) |
| Cola (LinkedList) | Lista de espera por curso, en `GestorInscripciones` (opciones 3, 9 y 11) |
| Pila (ArrayDeque) | Deshacer ultima inscripcion, en `GestorInscripciones` (opcion 10) |
| Ordenacion y busqueda | `util/Algoritmos.java` (insercion y busqueda binaria), usado por `GestorAlumnos` (opciones 12 y 13) |

## Etapa 4 – Patron MVC, archivos y persistencia

Opcion **15** del menu: modulo de Calificaciones implementado con el patron **MVC**.

| Capa | Paquete | Responsabilidad |
|------|---------|-----------------|
| Modelo | `linguagest.mvc.modelo` | JDBC (SELECT/INSERT/UPDATE/DELETE), `ArrayList<Calificacion>`, arreglo `int[]` de distribucion, archivos CSV (`java.io`) |
| Vista | `linguagest.mvc.vista` | Interfaz `VistaCalificaciones` + implementacion `VistaConsola` |
| Controlador | `linguagest.mvc.controlador` | `ControladorCalificaciones`: une vista y modelo, captura `PersistenciaException` e `IOException` |

Excepcion propia: `linguagest.excepcion.PersistenciaException` (envuelve `SQLException`).

Archivos: la exportacion crea `datos/calificaciones_curso_<id>.csv`; la importacion lee un CSV
(ejemplo: `datos/importar_ejemplo.csv`, formato `alumno_id;curso_id;AAAA-MM-DD;nota`) y registra
las filas rechazadas en `datos/importacion_errores.log`.
