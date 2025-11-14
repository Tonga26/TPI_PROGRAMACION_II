package service;

import dao.HistoriaClinicaDao;
import dao.impl.HistoriaClinicaDaoImpl;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import models.HistoriaClinica;

/**
 * Implementación de la capa de servicio para la entidad {@link HistoriaClinica}.
 * <p>
 * Se encarga de la lógica de negocio y las validaciones específicas para las historias clínicas.
 * Esta clase es clave en las transacciones compuestas, ya que es llamada por {@code PacienteService}
 * para asegurar la atomicidad de las operaciones 1:1.
 * </p>
 */
public class HistoriaClinicaServiceImpl implements HistoriaClinicaService {

    private HistoriaClinicaDao hcDao;

    /**
     * Constructor que inicializa las dependencias.
     */
    public HistoriaClinicaServiceImpl() {
        this.hcDao = new HistoriaClinicaDaoImpl();
    }

    /**
     * Realiza las validaciones de datos de la entidad antes de cualquier operación de persistencia.
     *
     * @param h La historia clínica a validar.
     * @throws IllegalArgumentException Si la historia clínica es nula o si el número de historia es nulo o vacío.
     */
    private void validar(HistoriaClinica h) {
        if (h == null) {
            throw new IllegalArgumentException("La Historia Clínica no puede ser nula.");
        }
        if (h.getNroHistoria() == null || h.getNroHistoria().isBlank()) {
            throw new IllegalArgumentException("El Nro. de Historia es obligatorio.");
        }
    }

    /**
     * Inserta una Historia Clínica. Este método no es compatible con la regla 1:1 (obligatoria)
     * y lanza una excepción para forzar el uso de la versión transaccional que incluye {@code pacienteId}.
     *
     * @param h La Historia Clínica a insertar.
     * @return No retorna normalmente (lanza excepción).
     * @throws UnsupportedOperationException Siempre lanza una excepción.
     * @throws SQLException Nunca lanza directamente.
     */
    @Override
    public HistoriaClinica insertar(HistoriaClinica h) throws SQLException {
        validar(h);
        throw new UnsupportedOperationException(
                "No se puede crear una Historia Clínica sin un Paciente asociado. Use el método transaccional."
        );
    }

    /**
     * Inserta una Historia Clínica dentro de una transacción activa.
     *
     * @param h La Historia Clínica a insertar.
     * @param con La conexión JDBC de la transacción activa.
     * @param pacienteId El ID del Paciente al que se vincula la historia.
     * @return La Historia Clínica con su ID generado.
     * @throws SQLException Si ocurre un error al insertar o al acceder a la base de datos.
     */
    @Override
    public HistoriaClinica insertar(HistoriaClinica h, Connection con, long pacienteId) throws SQLException {
        validar(h);

        // Uso de cast para acceder al método específico del DAO que recibe el pacienteId.
        HistoriaClinicaDaoImpl daoConcreto = (HistoriaClinicaDaoImpl) hcDao;

        return daoConcreto.create(h, con, pacienteId);
    }

    /**
     * Actualiza una Historia Clínica existente, utilizando una conexión autogestionada (método de conveniencia).
     *
     * @param h La Historia Clínica con los datos actualizados.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     * @throws IllegalArgumentException Si el ID es inválido.
     */
    @Override
    public void actualizar(HistoriaClinica h) throws SQLException {
        validar(h);
        if (h.getId() == null || h.getId() <= 0) {
            throw new IllegalArgumentException("El ID de la Historia Clínica es inválido para actualizar.");
        }
        hcDao.update(h);
    }

    /**
     * Realiza la eliminación lógica de una Historia Clínica por ID (método de conveniencia).
     *
     * @param id El ID de la historia clínica a eliminar.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     * @throws IllegalArgumentException Si el ID es inválido.
     */
    @Override
    public void eliminar(long id) throws SQLException {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID debe ser mayor a 0.");
        }
        hcDao.delete(id);
    }

    /**
     * Ejecuta la eliminación lógica (baja) de la Historia Clínica dentro de una transacción activa.
     * Este método es llamado por {@code PacienteService} para asegurar la atomicidad de la baja compuesta.
     *
     * @param pacienteId El ID del paciente cuya HC se eliminará.
     * @param con La conexión JDBC de la transacción activa.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     * @throws IllegalArgumentException Si el ID del paciente es inválido.
     */
    @Override
    public void eliminarPorPacienteId(long pacienteId, Connection con) throws SQLException {
        if (pacienteId <= 0) {
            throw new IllegalArgumentException("El ID de Paciente debe ser mayor a 0.");
        }

        // Delega la baja lógica por clave foránea al DAO
        hcDao.deleteByPacienteId(pacienteId, con);
    }


    /**
     * Busca una Historia Clínica por ID, delegando al DAO.
     *
     * @param id El ID de la historia clínica a buscar.
     * @return Un Optional que contiene la Historia Clínica o vacío.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public Optional<HistoriaClinica> getById(long id) throws SQLException {
        return hcDao.read(id);
    }

    /**
     * Recupera todas las Historias Clínicas activas, delegando al DAO.
     *
     * @return Una lista de Historias Clínicas.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public List<HistoriaClinica> getAll() throws SQLException {
        return hcDao.readAll();
    }

    /**
     * Actualiza una Historia Clínica dentro de una transacción activa.
     *
     * @param h La Historia Clínica con los datos actualizados.
     * @param con La conexión JDBC de la transacción activa.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     * @throws IllegalArgumentException Si el ID es inválido.
     */
    @Override
    public void actualizar(HistoriaClinica h, Connection con) throws SQLException {
        validar(h);
        if (h.getId() == null || h.getId() <= 0) {
            throw new IllegalArgumentException("El ID de la Historia Clínica es inválido para actualizar.");
        }

        // Llama al método del DAO que utiliza la conexión transaccional
        hcDao.update(h, con);
    }
}