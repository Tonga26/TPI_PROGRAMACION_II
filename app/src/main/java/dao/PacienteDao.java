package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;
import models.Paciente;

/**
 * Interfaz que define las operaciones de acceso a datos específicas para la entidad {@link Paciente}.
 * Extiende la funcionalidad genérica CRUD definida en {@link GenericDao} e incorpora
 * consultas particulares del dominio, como la búsqueda por DNI.
 */
public interface PacienteDao extends GenericDao<Paciente> {

    /**
     * Busca un paciente activo en la base de datos utilizando su número de documento (DNI).
     * Este método gestiona su propia conexión a la base de datos.
     *
     * @param dni El Documento Nacional de Identidad a buscar.
     * @return Un Optional conteniendo el Paciente si existe y no está eliminado, o vacío.
     * @throws SQLException Si ocurre un error de acceso a datos.
     */
    Optional<Paciente> findByDni(String dni) throws SQLException;

    /**
     * Busca un paciente por DNI utilizando una conexión JDBC externa existente.
     * Permite incluir esta consulta dentro de una transacción mayor.
     *
     * @param dni El DNI a buscar.
     * @param con La conexión activa a utilizar.
     * @return Un Optional con el Paciente encontrado o vacío.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    Optional<Paciente> findByDni(String dni, Connection con) throws SQLException;
}
