package service;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz genérica que define el contrato estándar de la capa de Lógica de Negocio (Service).
 * <p>
 * Proporciona las operaciones básicas de negocio para la gestión de cualquier entidad,
 * delegando la gestión transaccional a sus implementaciones concretas.
 * </p>
 *
 * @param <T> El tipo de la entidad del modelo que gestiona este servicio.
 */
public interface GenericService<T> {

    /**
     * Valida la entidad y coordina la inserción en la base de datos.
     * La implementación es responsable de iniciar y cerrar la transacción si es necesario.
     *
     * @param t La entidad a insertar.
     * @return La entidad insertada con su ID asignado.
     * @throws SQLException Si ocurre un error durante la persistencia o la transacción.
     */
    T insertar(T t) throws SQLException;

    /**
     * Valida la entidad y coordina la actualización en la base de datos.
     * La implementación es responsable de asegurar la atomicidad de la operación.
     *
     * @param t La entidad con los datos actualizados.
     * @throws SQLException Si ocurre un error durante la persistencia o la transacción.
     */
    void actualizar(T t) throws SQLException;

    /**
     * Valida el ID y coordina la eliminación lógica de la entidad y sus componentes asociados.
     * La implementación debe asegurar la atomicidad de la operación de eliminación compuesta.
     *
     * @param id El ID de la entidad a eliminar.
     * @throws SQLException Si ocurre un error durante la eliminación o la transacción.
     */
    void eliminar(long id) throws SQLException;

    /**
     * Busca una entidad por su identificador único.
     * La implementación debe 'enriquecer' el objeto, cargando todas sus dependencias (ej. Historia Clínica).
     *
     * @param id El ID de la entidad a buscar.
     * @return Un Optional que contiene la entidad completa si se encuentra, o vacío.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    Optional<T> getById(long id) throws SQLException;

    /**
     * Recupera una lista de todas las entidades activas y 'enriquecidas' del dominio.
     *
     * @return Una lista con todas las entidades encontradas.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    List<T> getAll() throws SQLException;
}