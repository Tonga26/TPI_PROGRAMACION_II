package dao;

import models.HistoriaClinica;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interfaz que define las operaciones de acceso a datos específicas para la entidad {@link HistoriaClinica}.
 * Extiende todas las operaciones CRUD definidas en {@link GenericDao}.
 */
public interface HistoriaClinicaDao extends GenericDao<HistoriaClinica> {

    /**
     * Realiza la eliminación lógica (baja) de una Historia Clínica utilizando el ID del Paciente asociado.
     * Este método es transaccional y requiere una conexión activa para formar parte de una operación compuesta.
     *
     * @param pacienteId El ID del Paciente al que está vinculada la Historia Clínica.
     * @param con La conexión JDBC activa a utilizar.
     * @throws SQLException Si ocurre un error al ejecutar la baja lógica en la base de datos.
     */
    void deleteByPacienteId(long pacienteId, Connection con) throws SQLException;
}