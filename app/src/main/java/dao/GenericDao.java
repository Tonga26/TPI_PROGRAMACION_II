package dao;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica que define el contrato estándar para los Objetos de Acceso a Datos (DAO).
 * <p>
 * Proporciona las operaciones CRUD (Crear, Leer, Actualizar, Eliminar) básicas
 * que deben implementar todos los DAOs del sistema.
 * </p>
 *
 * @param <T> El tipo de la entidad del modelo que gestiona este DAO.
 */
public interface GenericDao<T> {

    // --- Métodos de Conveniencia (Gestión automática de conexión) ---

    /**
     * Persiste una nueva entidad en la base de datos.
     * Este método gestiona su propia conexión.
     *
     * @param t La entidad a crear.
     * @return La entidad creada con su ID generado actualizado.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    T create(T t) throws SQLException;

    /**
     * Busca una entidad por su identificador único.
     * Este método gestiona su propia conexión.
     *
     * @param id El ID de la entidad a buscar.
     * @return Un Optional que contiene la entidad si se encuentra, o vacío si no existe.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    Optional<T> read(long id) throws SQLException;

    /**
     * Recupera todas las entidades activas (no eliminadas lógicamente) de la base de datos.
     * Este método gestiona su propia conexión.
     *
     * @return Una lista con todas las entidades encontradas.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    List<T> readAll() throws SQLException;

    /**
     * Actualiza los datos de una entidad existente.
     * Este método gestiona su propia conexión.
     *
     * @param t La entidad con los datos actualizados.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    void update(T t) throws SQLException;

    /**
     * Elimina (generalmente mediante baja lógica) una entidad por su ID.
     * Este método gestiona su propia conexión.
     *
     * @param id El ID de la entidad a eliminar.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    void delete(long id) throws SQLException;

    // --- Métodos Transaccionales (Conexión externa inyectada) ---

    /**
     * Persiste una nueva entidad utilizando una conexión existente.
     * Permite que esta operación forme parte de una transacción externa.
     *
     * @param t La entidad a crear.
     * @param c La conexión JDBC activa a utilizar.
     * @return La entidad creada con su ID generado.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    T create(T t, Connection c) throws SQLException;

    /**
     * Busca una entidad por su ID utilizando una conexión existente.
     * Permite que esta operación forme parte de una transacción externa.
     *
     * @param id El ID de la entidad.
     * @param c La conexión JDBC activa.
     * @return Un Optional con la entidad encontrada o vacío.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    Optional<T> read(long id, Connection c) throws SQLException;

    /**
     * Recupera todas las entidades utilizando una conexión existente.
     * Permite que esta operación forme parte de una transacción externa.
     *
     * @param c La conexión JDBC activa.
     * @return Lista de entidades.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    List<T> readAll(Connection c) throws SQLException;

    /**
     * Actualiza una entidad utilizando una conexión existente.
     * Permite que esta operación forme parte de una transacción externa.
     *
     * @param t La entidad a actualizar.
     * @param c La conexión JDBC activa.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    void update(T t, Connection c) throws SQLException;

    /**
     * Elimina una entidad utilizando una conexión existente.
     * Permite que esta operación forme parte de una transacción externa.
     *
     * @param id El ID de la entidad a eliminar.
     * @param c La conexión JDBC activa.
     * @throws SQLException Si ocurre un error en la base de datos.
     */
    void delete(long id, Connection c) throws SQLException;
}