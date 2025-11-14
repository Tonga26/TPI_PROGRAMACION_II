package service;

import config.DatabaseConnection;
import dao.PacienteDao;
import dao.impl.PacienteDaoImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import models.HistoriaClinica;
import models.Paciente;

/**
 * Implementación de la capa de servicio para la entidad {@link Paciente}.
 * <p>
 * Actúa como orquestador, implementando la lógica de negocio, validaciones y
 * gestionando las transacciones complejas que involucran a {@link Paciente} y
 * {@link HistoriaClinica}. Sigue el patrón Service-llama-Service.
 * </p>
 */
public class PacienteServiceImpl implements PacienteService {

    private PacienteDao pacienteDao;
    private HistoriaClinicaService hcService;

    /**
     * Constructor que inicializa las dependencias (Inyección de Dependencias manual).
     */
    public PacienteServiceImpl() {
        this.pacienteDao = new PacienteDaoImpl();
        this.hcService = new HistoriaClinicaServiceImpl();
    }

    /**
     * Realiza las validaciones de datos de la entidad antes de cualquier operación de persistencia.
     *
     * @param p El paciente a validar.
     * @throws IllegalArgumentException Si algún campo obligatorio (nombre, apellido, dni, historiaClinica) está ausente.
     */
    private void validar(Paciente p) {
        if (p == null) throw new IllegalArgumentException("Paciente nulo.");
        if (p.getNombre() == null || p.getNombre().isBlank()) throw new IllegalArgumentException("Nombre obligatorio.");
        if (p.getApellido() == null || p.getApellido().isBlank()) throw new IllegalArgumentException("Apellido obligatorio.");
        if (p.getDni() == null || p.getDni().isBlank()) throw new IllegalArgumentException("DNI obligatorio.");
        if (p.getHistoriaClinica() == null) throw new IllegalArgumentException("Historia clínica obligatoria (Relación 1-1).");
    }

    /**
     * Implementa la transacción de inserción de un Paciente y su Historia Clínica asociada.
     * <p>
     * Se asegura de que ambas operaciones se realicen correctamente o se reviertan (commit/rollback).
     * </p>
     *
     * @param p El paciente a insertar.
     * @return El paciente con su ID actualizado.
     * @throws SQLException Si ocurre un error transaccional (ej. BD caída o DNI duplicado).
     */
    @Override
    public Paciente insertar(Paciente p) throws SQLException {
        validar(p);

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // 1. Inicia transacción

            pacienteDao.create(p, con); // 2. Crea el Paciente (obtiene el id).

            hcService.insertar(p.getHistoriaClinica(), con, p.getId()); // 2. Crea la Historia Clínica (usa el ID y la conexión transaccional).

            con.commit(); // 4. Confirma
            return p;

        } catch (Exception ex) {
            if (con != null) {
                con.rollback(); // 5. Revierte
            }
            throw new SQLException("Error transaccional al insertar: " + ex.getMessage(), ex);

        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignore) {}
                try { con.close(); } catch (Exception ignore) {}
            }
        }
    }

    /**
     * Implementa la transacción de actualización para el Paciente y su Historia Clínica.
     * <p>
     * Ambas actualizaciones se realizan dentro de la misma unidad de trabajo.
     * </p>
     *
     * @param p El paciente con los datos actualizados.
     * @throws SQLException Si ocurre un error transaccional.
     * @throws IllegalArgumentException Si el ID del paciente es nulo.
     */
    @Override
    public void actualizar(Paciente p) throws SQLException {
        if (p.getId() == null) throw new IllegalArgumentException("Id requerido.");
        validar(p);

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // 1. Inicia transacción

            pacienteDao.update(p, con); // 2. Actualiza el Paciente.

            hcService.actualizar(p.getHistoriaClinica(), con); // 3. Actualiza la Historia Clínica (usa la conexión transaccional).

            con.commit(); // 4. Confirma

        } catch (Exception ex) {
            if (con != null) {
                con.rollback(); // 5. Revierte
            }
            throw new SQLException("Error transaccional al actualizar: " + ex.getMessage(), ex);
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignore) {}
                try { con.close(); } catch (Exception ignore) {}
            }
        }
    }

    /**
     * Implementa la transacción de eliminación lógica de un Paciente y su Historia Clínica.
     * <p>
     * Realiza la baja lógica de ambas entidades, asegurando la integridad referencial (atomicidad).
     * </p>
     *
     * @param id El ID del paciente a eliminar.
     * @throws SQLException Si ocurre un error transaccional o de base de datos.
     * @throws IllegalArgumentException Si el ID es inválido.
     */
    @Override
    public void eliminar(long id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID de Paciente es inválido.");
        }

        Connection con = null;
        try {
            con = DatabaseConnection.getConnection();
            con.setAutoCommit(false); // 1. Inicia transacción

            hcService.eliminarPorPacienteId(id, con); // 2. Baja lógica de la Historia Clínica (Service-llama-Service).

            pacienteDao.delete(id, con);  // 3. Baja lógica del Paciente.

            con.commit(); // 4. Confirma

        } catch (Exception ex) {
            if (con != null) {
                con.rollback(); // 5. Revierte
            }
            throw new SQLException("Error transaccional al eliminar: " + ex.getMessage(), ex);

        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); } catch (Exception ignore) {}
                try { con.close(); } catch (Exception ignore) {}
            }
        }
    }

    /**
     * Busca un paciente por su ID.
     * <p>
     * La operación delega completamente al DAO, el cual es responsable de cargar
     * también la Historia Clínica (optimizada con LEFT JOIN).
     * </p>
     *
     * @param id El ID del paciente.
     * @return Un Optional que contiene el paciente completo o vacío.
     * @throws SQLException Si ocurre un error de acceso a datos.
     */
    @Override
    public Optional<Paciente> getById(long id) throws SQLException {
        return pacienteDao.read(id);
    }

    /**
     * Recupera todos los pacientes activos.
     * <p>
     * La operación delega al DAO, el cual usa LEFT JOIN para cargar los datos completos de forma eficiente.
     * </p>
     *
     * @return Una lista de pacientes completos.
     * @throws SQLException Si ocurre un error de acceso a datos.
     */
    @Override
    public List<Paciente> getAll() throws SQLException {
        return pacienteDao.readAll();
    }

    /**
     * Busca un paciente por su DNI.
     * <p>
     * La operación delega al DAO, el cual usa LEFT JOIN para cargar los datos completos de forma eficiente.
     * </p>
     *
     * @param dni El DNI a buscar.
     * @return Un Optional con el paciente completo o vacío.
     * @throws SQLException Si ocurre un error de acceso a datos.
     */
    @Override
    public Optional<Paciente> findByDni(String dni) throws SQLException {
        return pacienteDao.findByDni(dni);
    }
}