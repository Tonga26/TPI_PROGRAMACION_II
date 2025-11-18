# Sistema de Gestión de Pacientes - Historias Clínicas

## Trabajo Práctico Integrador - Programación 2

### Roles del Equipo

| Nombre                    | Rol Principal | Responsabilidad Específica |
|:--------------------------| :--- | :--- |
| Matías Ariel Deluca       | **Arquitecto de Software** | Diseño de la estructura en capas, decisiones tecnológicas y revisión de código. |
| Luciano Demian Contreras  | **DBA & DevOps** | Definición de scripts SQL (`01_create.sql`), gestión de la BD y la reproducibilidad del entorno. |
| Gastón Armando Giorgio    | **Backend (DAO/Service)** | Implementación de la lógica transaccional, patrones DAO y Service, y pruebas unitarias. |
| Aldo Manfredi             | **UI de consola / QA** | Diseño de la interfaz (`MenuDisplay`), validación de entradas y ejecución de pruebas funcionales. |

### Descripción del Proyecto

Este Trabajo Práctico Integrador (TPI) tiene como objetivo demostrar la aplicación práctica de los conceptos fundamentales de Programación Orientada a Objetos y Persistencia de Datos. El proyecto consiste en desarrollar un sistema de gestión para la entidad **Paciente** asociada a **Historia Clínica** mediante una **relación 1:1 unidireccional** (Paciente conoce a Historia Clínica).

El sistema permite realizar operaciones CRUD (Crear, Leer, Actualizar, Eliminar) sobre ambas entidades, implementando una arquitectura robusta, transacciones atómicas con JDBC y baja lógica (Soft Delete).

### Documentación Adicional
ENLACE A VIDEO: https://www.youtube.com/watch?v=jWxTnzhZ078

### Objetivos Académicos

El desarrollo de este sistema permite aplicar y consolidar los siguientes conceptos clave de la materia:

**1. Arquitectura en Capas (Layered Architecture)**
- Implementación de separación de responsabilidades en 4 capas diferenciadas.
- Capa de Presentación (Main/UI): Interacción con el usuario mediante consola.
- Capa de Lógica de Negocio (Service): Validaciones y reglas de negocio.
- Capa de Acceso a Datos (DAO): Operaciones de persistencia.
- Capa de Modelo (Models): Representación de entidades del dominio.

**2. Programación Orientada a Objetos**
- Uso de herencia mediante clase abstracta `Base` (para ID y eliminado).
- Implementación de interfaces genéricas (`GenericDAO`, `GenericService`).
- Encapsulamiento con atributos privados y métodos de acceso.
- Definición de tipos de dato complejos (Enum `GrupoSanguineo`).

**3. Persistencia de Datos con JDBC**
- Conexión a base de datos MySQL mediante JDBC.
- Implementación del patrón DAO (Data Access Object) con métodos transaccionales.
- **Uso de PreparedStatements al 100%** para prevenir SQL Injection.
- **Optimización N+1:** Consultas de lectura enriquecidas con `LEFT JOIN` (en `PacienteDAO`).

**4. Manejo de Recursos y Excepciones**
- Uso del patrón try-with-resources para gestión segura de recursos JDBC.
- Manejo de excepciones con propagación controlada en cada capa.

**5. Patrones de Diseño y Transacciones**
- **DAO Pattern** y **Service Layer Pattern** (Separación de lógica y acceso a datos).
- **Service-llama-Service:** Coordinación entre `PacienteService` y `HistoriaClinicaService`.
- **Transacciones Atómicas:** Gestión de `commit` y `rollback` en `insertar`, `actualizar` y **eliminar** compuesto.

**6. Validación de Integridad de Datos**
- Validación de unicidad (DNI y Nro Historia) en la base de datos.
- Validación de campos obligatorios en capa de servicio.
- Implementación de eliminación lógica (`Soft Delete`) para preservar el historial de datos.

***

## Características Principales

- **Gestión de Pacientes (A)**: CRUD completo (Crear, Listar, Buscar por DNI, Actualizar, Eliminar Lógico).
- **Gestión de Historias Clínicas (B)**: CRUD completo (Crear/Insertar vía Paciente, Listar, Actualizar).
- **Relación 1:1 Forzada**: La Historia Clínica está siempre vinculada a un Paciente (`paciente_id` es `NOT NULL` y `UNIQUE`).
- **Eliminación Segura**: La baja lógica de un Paciente elimina lógicamente su Historia Clínica asociada mediante una transacción atómica.
- **Formato Profesional**: Interfaz de consola con uso de `printf` para salidas tabulares y detalladas.

## Requisitos del Sistema

| Componente | Versión Requerida |
|------------|-------------------|
| Java JDK | 17 o superior |
| MySQL | 8.0 o superior (XAMPP/Workbench) |
| Gradle | 8.12 (incluido wrapper) |
| Herramienta | MySQL Workbench o phpMyAdmin |

## Instalación

### 1. Configurar Base de Datos

Ejecutar los siguientes scripts SQL en el orden indicado:

1.  **Creación de Base y Tablas (`01_create.sql`):**
    ```sql
    -- Contenido de 01_create.sql (Creación de la BD, tablas y constraints)
    CREATE DATABASE IF NOT EXISTS tpi_prog2
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

    USE tpi_prog2;

    CREATE TABLE paciente (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        eliminado BOOLEAN DEFAULT FALSE,
        nombre VARCHAR(80) NOT NULL,
        apellido VARCHAR(80) NOT NULL,
        dni VARCHAR(15) NOT NULL UNIQUE, 
        fecha_nacimiento DATE NULL
    );

    CREATE TABLE historia_clinica (
        id BIGINT AUTO_INCREMENT PRIMARY KEY,
        eliminado BOOLEAN DEFAULT FALSE,
        nro_historia VARCHAR(20) UNIQUE, 
        grupo_sanguineo VARCHAR(3) NULL, 
        antecedentes TEXT NULL,
        medicacion_actual TEXT NULL,
        observaciones TEXT NULL,
        paciente_id BIGINT NOT NULL UNIQUE,
        fecha_apertura DATE NULL, 
        CONSTRAINT fk_historia_paciente FOREIGN KEY (paciente_id) REFERENCES paciente(id),
        CONSTRAINT chk_grupo_sanguineo CHECK (grupo_sanguineo IS NULL OR grupo_sanguineo 
        IN ('A+', 'A-', 'B+', 'B-', 'AB+', 'AB-', 'O+', 'O-'))
    );
    ```

2.  **Carga de Datos de Prueba (`02_data.sql`):**
    ```sql
    -- Contenido de 02_data.sql (Datos iniciales para pruebas)
    USE tpi_prog2;

    INSERT INTO paciente (nombre, apellido, dni, fecha_nacimiento)
    VALUES 
    ('Carlos', 'Perez', '30123456', '1985-05-15'),
    ('Rodrigo', 'Mendez', '31987654', '1986-11-20');

    INSERT INTO historia_clinica (nro_historia, grupo_sanguineo, antecedentes, medicacion_actual, paciente_id)
    VALUES
    ('HC-0001', 'A+', 'Alergia al polen.', 'Loratadina 10mg.', 1), 
    ('HC-0002', 'O-', 'Cirugía de apéndice en 2010.', Ninguna., 2);
    ```

### 2. Configurar Conexión

Crear el archivo `db.properties` en la raíz del proyecto (al mismo nivel que `build.gradle`) con las credenciales de tu servidor MySQL local:

```properties
# Configuracion de la Base de Datos
db.url=jdbc:mysql://localhost:3306/tpi_prog2
db.user=root
db.password=
```
(Asume usuario 'root' y contraseña vacía por defecto en XAMPP/MySQL local)


### 3. Ejecución

- Opción 1: Desde IDE (Recomendado)
1. Abrir el proyecto en IntelliJ IDEA o Eclipse.

2. Ejecutar la clase Main.Main.

- Opción 2: Línea de comandos (Con Gradle Wrapper)

1. Compilar el proyecto:
```
# Linux/macOS
./gradlew clean build

# Windows
gradlew.bat clean build
```

2. Ejecutar con el JAR compilado, asegurando el classpath del conector MySQL.4.

### 4. Uso del Sistema

#### Menú Principal
Al ejecutar la aplicación, se mostrará el menú principal para la navegación de operaciones:
```
╔════════════════════════════════════════════════════╗
║           SISTEMA DE GESTIÓN DE CLÍNICA            ║
╠════════════════════════════════════════════════════╣
║ 1. Crear Paciente (con Historia Clínica)           ║
║ 2. Listar todos los Pacientes                      ║
║ 3. Buscar Paciente por DNI                         ║
║ 4. Actualizar Paciente                             ║
║ 5. Actualizar Historia Clínica de un Paciente      ║
║ 6. Listar todas las Historias Clínicas             ║
║ 7. Eliminar Paciente (Baja Lógica)                 ║
╠════════════════════════════════════════════════════╣
║ 0. Salir                                           ║
╚════════════════════════════════════════════════════╝
 ➤ Ingrese una opción: 
```

#### Operaciones Clave
```
Opción      | Entidad   |   Funcionalidad Demostrada
------------------------------------------------------------------
1. Crear    |  A + B    |   Transacción Compuesta: Creación atómica de A y B (commit/rollback).
2, 3        |  A + B    |   Optimización N+1 (Lectura de A y B en 1 query con LEFT JOIN).
4, 5        |  A + B    |   Transacción de Actualización (Atomicidad entre paciente y historia_clinica).
7. Eliminar |  A + B    |   Transacción de Baja Lógica (Marca A y B como eliminados en una sola unidad de trabajo).
```

### 5. Arquitectura y Componentes
#### Estructura en Capas
La aplicación está organizada en paquetes que separan claramente las responsabilidades, siguiendo el patrón de Arquitectura por Capas:
```
Paquete   |     Rol              |       Clases Clave           |         Responsabilidad Principal
------------------------------------------------------------------------------------------------------------------
main      |  Presentación        |   AppMenu, MenuHandler       |  Interacción con el usuario y control del flujo.
service   |  Lógica de Negocio   |   PacienteServiceImpl        |  Validaciones y Transacciones (commit/rollback).
dao/impl  |  Acceso a Datos      |   PacienteDaoImpl            |  Ejecución de SQL con PreparedStatement y Mapeo.
models    |  Dominio             |   Paciente, HistoriaClinica  |  Representación de las entidades y su estado.
config    |  Infraestructura     |   DatabaseConnection         |  Gestión de la conexión JDBC.
```

#### Modelo de Datos (Relación 1:1)
- Relación 1:1 Unidireccional:
  Se garantiza mediante la clave foránea (paciente_id) en la tabla historia_clinica, la cual es forzada a ser única (UNIQUE) para impedir que se asocie más de una historia clínica a un mismo paciente.

- Baja Lógica: Ambas entidades (A y B) heredan el campo eliminado de la clase Base. Las consultas de lectura (readAll, read) filtran por eliminado=0.

### 6. Tecnologías Utilizadas
- Lenguaje: Java 17+
- Build Tool: Gradle 8.12
- Base de Datos: MySQL 8.0
- Persistencia: JDBC (Java Database Connectivity)
- Patrones: DAO, Service Layer, Soft Delete, Inyección de Dependencias Manual.
