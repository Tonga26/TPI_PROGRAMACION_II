package service;

import java.sql.Connection;
import java.sql.SQLException;
import models.HistoriaClinica;

/**
 * Interfaz que define la lógica de negocio específica para la entidad {@link HistoriaClinica} (Entidad B).
 * Hereda las operaciones básicas de {@link GenericService} y añade métodos esenciales
 * para gestionar esta entidad como parte dependiente de una transacción.
 */
public interface HistoriaClinicaService extends GenericService<HistoriaClinica> {

    /**
     * Inserta un registro de Historia Clínica y lo vincula al Paciente especificado.
     * Este método es transaccional y requiere que la conexión sea gestionada por la capa de servicio superior
     * (e.g., {@code PacienteService}).
     *
     * @param h La HistoriaClínica a crear.
     * @param con La conexión JDBC de la transacción activa a utilizar.
     * @param pacienteId El ID del Paciente al que se va a vincular la Historia Clínica.
     * @return La HistoriaClínica creada con su ID generado actualizado.
     * @throws SQLException Si ocurre un error de base de datos durante la inserción.
     */
    HistoriaClinica insertar(HistoriaClinica h, Connection con, long pacienteId) throws SQLException;

    /**
     * Ejecuta la eliminación lógica (baja) de la Historia Clínica buscando por el ID del Paciente asociado.
     * Es crucial para asegurar la atomicidad de la operación de borrado compuesto.
     *
     * @param pacienteId El ID del paciente cuya HC se eliminará.
     * @param con La conexión JDBC de la transacción activa a utilizar.
     * @throws SQLException Si ocurre un error de base de datos durante la eliminación.
     */
    void eliminarPorPacienteId(long pacienteId, Connection con) throws SQLException;

    /**
     * Realiza la actualización de los datos de la Historia Clínica dentro de una transacción activa.
     *
     * @param h La HistoriaClínica con los datos actualizados.
     * @param con La conexión JDBC de la transacción activa a utilizar.
     * @throws SQLException Si ocurre un error de base de datos durante la actualización.
     */
    void actualizar(HistoriaClinica h, Connection con) throws SQLException;
}