# Historias de Usuario - Sistema de Gestión de Pacientes y Historias Clínicas

##### Especificaciones funcionales completas del sistema CRUD de Pacientes (A) y Historias Clínicas (B).

## Tabla de Contenidos

- [Épica 1: Gestión de Pacientes (Entidad A)](#épica-1-gestión-de-pacientes-entidad-a)
- [Épica 2: Gestión de Historias Clínicas (Entidad B)](#épica-2-gestión-de-historias-clínicas-entidad-b)
- [Épica 3: Operaciones de Mantenimiento](#épica-3-operaciones-de-mantenimiento)
- [Reglas de Negocio Clave](#reglas-de-negocio-clave)

---

## Épica 1: Gestión de Pacientes (Entidad A)

### HU-001: Crear Paciente con Historia Clínica

**Como** un administrativo de la clínica
**Quiero** crear un nuevo registro de paciente junto con su Historia Clínica
**Para** asegurar que la relación 1:1 sea obligatoria y atómica

#### Criterios de Aceptación (Transaccional)

```
Escenario 1: Creación atómica exitosa
  - Dado que el usuario selecciona "Crear Paciente"
  - Cuando ingresa DNI "40123456" y datos personales válidos
  - y proporciona el Nro Historia "HC-003" y Grupo Sanguíneo "B+"
  - Entonces el sistema inicia la transacción
    1. crea el Paciente (obtiene ID)
    2. crea la Historia Clínica (con el ID de Paciente como FK UNIQUE)
    3. realiza COMMIT
    4. muestra "Paciente creado exitosamente con ID: X"

Escenario 2: Intento de crear con DNI duplicado
  - Dado que existe un Paciente con DNI "40123456"
  - Cuando el usuario intenta crear otro Paciente con el mismo DNI
  - Entonces el sistema muestra "Ya existe un paciente con el DNI: 40123456"
  - y no ejecuta la transacción

Escenario 3: Intento de crear con campos obligatorios vacíos
  - Dado que el usuario ingresa datos para un nuevo Paciente
  - Cuando deja el campo Nombre vacío
  - Entonces el sistema muestra "Nombre obligatorio."
  - y no ejecuta la transacción.
```

#### Reglas de Negocio Aplicables
- RN-001: Nombre, apellido, DNI, Nro Historia Clínica son obligatorios.
- RN-002: El DNI debe ser único en el sistema.
- RN-003: La Historia Clínica es obligatoria (Relación 1:1 no nula).
- RN-004: La operación debe ser atómica (transacción con commit/rollback).
- RN-005: El Nro de Historia Clínica debe ser único.

#### Implementación Técnica
- Clase: MenuHandler.crearPaciente()
- Servicio: PacienteServiceImpl.insertar()
- Flujo:
1. PacienteServiceImpl inicia Connection y setAutoCommit(false).
2. Llama a PacienteDAO.create() (obtiene pacienteId).
3. Llama a HistoriaClinicaService.insertar(hc, con, pacienteId).
4. Si éxito: con.commit(). Si falla: con.rollback().

### HU-002: Listar Todos los Pacientes (Con Enriquecimiento)
**Como** un administrativo de la clínica 
**Quiero** ver un listado de todos los pacientes activos 
**Para** tener una visión general de la población clínica

#### Criterios de Aceptación (Optimizado N+1)
```
Escenario 1: Listar todos los pacientes activos con datos clínicos
- Dado que existen pacientes activos en el sistema
- Cuando el usuario selecciona "Listar todos los Pacientes"
- Entonces el sistema muestra un listado en formato tabular
- Y cada paciente incluye su ID, DNI, Nombre y el Nro de Historia Clínica.
- Y la consulta se resuelve con una única llamada optimizada (LEFT JOIN).

Escenario 2: No hay pacientes activos
- Dado que no existen pacientes con eliminado = FALSE
- Cuando el usuario lista los pacientes
- Entonces el sistema muestra "⚠ No hay pacientes registrados."
```

#### Reglas de Negocio Aplicables
- RN-006: Solo se listan pacientes con eliminado = FALSE.
- RN-007: La Historia Clínica se obtiene mediante LEFT JOIN en el DAO (PacienteDaoImpl).
- RN-008: La capa de servicio (PacienteServiceImpl) llama directamente a DAO.readAll().

#### Implementación Técnica
- Clase: MenuHandler.listarPacientes()
- Servicio: PacienteServiceImpl.getAll()
- DAO: PacienteDaoImpl.readAll(Connection c)
- Query: 
```
SELECT p.*, hc.* FROM paciente p LEFT JOIN historia_clinica hc ... WHERE p.eliminado = 0
```

### HU-003: Buscar Paciente por DNI
**Como** un administrativo de la clínica
**Quiero** buscar un paciente por su DNI único
**Para** encontrar rápidamente su ficha y datos clínicos

#### Criterios de Aceptación (Ficha Técnica)
```
Escenario 1: Buscar paciente existente por DNI
- Dado que existe paciente con DNI "30123456"
- Cuando el usuario selecciona "Buscar Paciente por DNI"
- Y ingresa el DNI "30123456"
- Entonces el sistema muestra la información en formato de ficha técnica
- Y se incluyen los datos personales y todos los datos clínicos asociados (HC).

Escenario 2: Búsqueda sin resultados
- Dado que no existe paciente con DNI "99999999"
- Cuando el usuario busca por "99999999"
- Entonces el sistema muestra "⚠ No se encontró ningún paciente con DNI: 99999999"
```

#### Reglas de Negocio Aplicables
- RN-009: La búsqueda debe ser exacta por DNI.
- RN-010: Se utiliza el método findByDni del DAO para la consulta.
- RN-011: El resultado debe estar enriquecido con la Historia Clínica (N+1 optimizado).

#### Implementación Técnica
- Clase: MenuHandler.buscarPacientePorDni()
- Servicio: PacienteService.findByDni(dni) (delega directamente al DAO optimizado).
- DAO: PacienteDaoImpl.findByDni(dni, con) (usa LEFT JOIN).

### HU-004: Actualizar Paciente
**Como** un administrativo de la clínica
**Quiero** modificar los datos personales de un paciente existente
**Para** mantener actualizada su información.

#### Criterios de Aceptación (Transaccional)
```
Escenario 1: Actualizar solo datos personales
- Dado que existe paciente con ID 1, Nombre "Carlos" y DNI "30123456"
- Cuando el usuario selecciona "Actualizar Paciente" y presiona ENTER en DNI
- Y escribe "Julián" en nombre
- Entonces el sistema inicia la transacción
- Y actualiza el Nombre a "Julián" en la tabla `paciente`
- Y actualiza la Historia Clínica con sus valores originales (transacción compuesta).
- Y realiza COMMIT.

Escenario 2: Intento de actualizar DNI a un valor duplicado
- Dado que existen Pacientes con DNI "111" y "222"
- Cuando el usuario intenta cambiar DNI del Paciente "222" a "111"
- Entonces el sistema realiza ROLLBACK
- Y muestra "Ya existe una persona con el DNI: 111".

Escenario 3: Mantener campos vacíos
- Dado que el usuario actualiza campos
- Cuando el usuario presiona ENTER sin ingresar valor
- Entonces el sistema mantiene el valor original del campo.
```

#### Reglas de Negocio Aplicables
- RN-012: Se requiere ID > 0 para actualizar.
- RN-013: La validación de DNI único permite al paciente conservar su propio DNI.
- RN-014: La operación de actualización debe ser atómica (UPDATE A y UPDATE B).

#### Implementación Técnica
- Clase: MenuHandler.actualizarPaciente()
- Servicio: PacienteServiceImpl.actualizar(p)
- Flujo: 
1. PacienteServiceImpl inicia Connection y setAutoCommit(false).
2. Llama a PacienteDAO.update(p, con). 
3. Llama a HcService.actualizar(h, con) (versión transaccional).con.commit() o con.rollback().

### HU-005: Eliminar Paciente (Baja Lógica)
**Como** un administrativo de la clínica
**Quiero** dar de baja a un paciente del sistema
**Para** que no aparezca en listados activos, manteniendo su registro histórico.

#### Criterios de Aceptación (Transacción de Baja Lógica)
```
Escenario 1: Eliminación lógica exitosa
- Dado que existe Paciente con ID 1 y HC con Nro "HC-001"
- Cuando el usuario selecciona "Eliminar Paciente" e ingresa ID 1
- Entonces el sistema inicia la transacción
- Y marca el Paciente como eliminado = TRUE
- Y marca la Historia Clínica "HC-001" como eliminado = TRUE
- Y realiza COMMIT
- Y la persona ya no aparece en el listado de Pacientes.

Escenario 2: Eliminación de ID inexistente
- Dado que no existe Paciente con ID 999
- Cuando el usuario intenta eliminar Paciente ID 999
- Entonces el sistema no realiza ninguna operación de persistencia.
- Y muestra "Error transaccional al eliminar: No se encontró Historia Clínica para el paciente ID: 999" (O similar).
```

#### Reglas de Negocio Aplicables
- RN-015: La eliminación debe ser siempre lógica (UPDATE ... SET eliminado = TRUE).
- RN-016: La baja del Paciente y la Historia Clínica deben ser atómicas y coordinadas por el Servicio (Transacción de Baja Lógica).

#### Implementación Técnica
- Clase: MenuHandler.eliminarPaciente()
- Servicio: PacienteServiceImpl.eliminar(id)
- Flujo: 
1. PacienteServiceImpl inicia transacción.
2. Llama a HcService.eliminarPorPacienteId(id, con).
3. Llama a PacienteDAO.delete(id, con).con.commit() o con.rollback().

## Épica 2: Gestión de Historias Clínicas (Entidad B)

### HU-006: Actualizar Historia Clínica
**Como** un médico o administrativo
**Quiero** modificar los datos clínicos de un paciente existente
**Para** registrar nuevos diagnósticos, medicación o antecedentes.

#### Criterios de Aceptación (Enriquecimiento)
```
Escenario 1: Actualizar solo antecedentes y grupo sanguíneo
- Dado que existe Paciente ID 1 y su HC con Grupo Sanguíneo "A+"
- Cuando el usuario actualiza la HC del Paciente ID 1
- Y cambia Grupo Sanguíneo a "O-"
- Y agrega "Diagnóstico de diabetes" en Antecedentes
- Entonces el sistema actualiza solo esos campos en la tabla `historia_clinica`.
- Y los demás campos de la HC (ej. Nro Historia) permanecen sin cambios.

Escenario 2: Actualizar HC de un paciente inexistente
- Dado que no existe Paciente con ID 999
- Cuando el usuario intenta actualizar su HC
- Entonces el sistema muestra "No se encontró un Paciente con ese ID."
```
#### Reglas de Negocio Aplicables
- RN-017: Se debe buscar y validar que el Paciente y su HC existan antes de actualizar.
- RN-018: Si un campo de entrada se deja vacío (ENTER), el valor original se mantiene.
- RN-019: La actualización de la HC es una operación simple (UPDATE HC), no necesita coordinación con Paciente.

#### Implementación Técnica
- Clase: MenuHandler.actualizarHistoriaClinica()
- Servicio: HcService.actualizar(h)
- Flujo: 
1. MenuHandler busca el paciente, obtiene el objeto HC anidado.
2. MenuHandler solicita entradas (lógica de "Enter para mantener"). 
3. Llama a HcService.actualizar(h) (versión simple o transaccional).

### HU-007: Listar Historias Clínicas
**Como** un administrativo o médico
**Quiero** ver todas las Historias Clínicas registradas
**Para** generar reportes o consultas rápidas de datos clínicos.

#### Criterios de Aceptación (Formato Ficha)
```
Escenario 1: Listar HCs mostrando textos completos
- Dado que existen varias Historias Clínicas registradas
- Cuando el usuario selecciona "Listar todas las Historias Clínicas"
- Entonces el sistema muestra un listado en formato de Bloque (Ficha)
- Y cada ficha muestra el texto completo de Antecedentes, Medicación y Observaciones.
- Y solo se muestran registros con eliminado = FALSE.

Escenario 2: Listar HCs sin resultados
- Dado que no existen Historias Clínicas activas
- Cuando el usuario lista las HCs
- Entonces el sistema muestra "⚠ No hay historias clínicas registradas."
```

#### Reglas de Negocio Aplicables
- RN-020: El formato de salida debe adaptarse a textos largos (bloques verticales).
- RN-021: La consulta debe filtrar por eliminado = FALSE.

#### Implementación Técnica
- Clase: MenuHandler.listarHistoriasClinicas()
- Servicio: HcService.getAll()
- DAO: HcDAO.readAll()
- UI: Uso de System.out.printf y System.out.println para generar el formato de bloque.

## Épica 3: Operaciones de Mantenimiento

### HU-008: Verificar Búsqueda por DNI
**Como** un desarrollador o QA
**Quiero** verificar la funcionalidad de búsqueda de pacientes por DNI
**Para** asegurarme de que la optimización N+1 es efectiva.

#### Criterios de Aceptación (Técnico)
```
Escenario 1: Búsqueda y Enriquecimiento Completo
- Dado que el Paciente existe en la BD
- Cuando se llama a PacienteService.findByDni(dni)
- Entonces el Paciente retornado contiene su objeto HistoriaClinica completamente mapeado
- Y el DAO ejecutó solo una consulta SQL (SELECT con LEFT JOIN).
```

## Reglas de negocio clave de toda la aplicación
```
Código  | Descripción                                                                                | Clase/Capa
RN-002  | DNI debe ser único.                                                                        | Service (Validación) + DB (Constraint)
RN-003  | La Historia Clínica es obligatoria (1:1).                                                  | Service (validate(p)) + DB (paciente_id NOT NULL UNIQUE)
RN-004  | La creación (A + B) debe ser atómica.                                                      | PacienteServiceImpl.insertar()
RN-013  | El Paciente puede actualizar su DNI, a menos que el nuevo DNI exista en otro paciente.     | Service (Validación)
RN-014  | La actualización (A + B) debe ser atómica.                                                 | PacienteServiceImpl.actualizar()
RN-015  | La eliminación es siempre baja lógica (eliminado = TRUE).                                  | Todos los DAOs (UPDATE ... SET eliminado=1)
RN-016  | La baja lógica de A debe incluir la baja lógica de B (transacción compuesta).              | PacienteServiceImpl.eliminar()
RN-018  | Los campos vacíos (ENTER) mantienen el valor original en las actualizaciones.              | MenuHandler (Lógica de if (!input.isBlank()))
RN-022  | Los textos largos (antecedentes, medicación) deben ser mostrados completos en la interfaz. | MenuHandler (listarHistoriasClinicas en formato de bloque)
```