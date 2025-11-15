package dao.impl;

import config.DatabaseConnection;
import dao.HistoriaClinicaDao;
import models.HistoriaClinica;
import java.sql.*;
import java.util.*;

/**
 * Implementación concreta del DAO para la entidad {@link HistoriaClinica}.
 * Utiliza JDBC y {@link PreparedStatement} para interactuar con la base de datos MySQL.
 * <p>
 * Contiene métodos de conveniencia (autogestionan la conexión) y métodos transaccionales
 * (reciben la conexión externa).
 * </p>
 */
public class HistoriaClinicaDaoImpl implements HistoriaClinicaDao {

    /**
     * Mapea el resultado de una fila del {@link ResultSet} a un objeto {@link HistoriaClinica}.
     *
     * @param rs El conjunto de resultados JDBC.
     * @return Un objeto HistoriaClinica.
     * @throws SQLException Si ocurre un error al leer los datos.
     */
    private HistoriaClinica map(ResultSet rs) throws SQLException {
        HistoriaClinica h = new HistoriaClinica();
        h.setId(rs.getLong("id"));
        h.setEliminado(rs.getBoolean("eliminado"));
        h.setNroHistoria(rs.getString("nro_historia"));
        String gs = rs.getString("grupo_sanguineo");
        h.setGrupoSanguineo(HistoriaClinica.GrupoSanguineo.fromDb(gs));
        h.setAntecedentes(rs.getString("antecedentes"));
        h.setMedicacionActual(rs.getString("medicacion_actual"));
        h.setObservaciones(rs.getString("observaciones"));
        java.sql.Date f = rs.getDate("fecha_apertura");
        h.setFechaApertura(f != null ? f.toLocalDate() : null);
        return h;
    }

    // --- Métodos de Conveniencia (Autoconexión) ---

    /**
     * Persiste una nueva Historia Clínica, delegando al método transaccional.
     */
    @Override
    public HistoriaClinica create(HistoriaClinica h) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            return create(h, c);
        }
    }

    /**
     * Busca una Historia Clínica por ID, delegando al método transaccional.
     */
    @Override
    public Optional<HistoriaClinica> read(long id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            return read(id, c);
        }
    }

    /**
     * Recupera todas las Historias Clínicas activas, delegando al método transaccional.
     */
    @Override
    public java.util.List<HistoriaClinica> readAll() throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            return readAll(c);
        }
    }

    /**
     * Actualiza una Historia Clínica existente, delegando al método transaccional.
     */
    @Override
    public void update(HistoriaClinica h) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            update(h, c);
        }
    }

    /**
     * Realiza la baja lógica de una Historia Clínica por ID, delegando al método transaccional.
     */
    @Override
    public void delete(long id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            delete(id, c);
        }
    }

    // --- Métodos Transaccionales (Conexión Inyectada) ---

    /**
     * Lanza una excepción para forzar el uso del método create con {@code pacienteId}.
     *
     * @throws SQLException Siempre lanza una excepción.
     */
    @Override
    public HistoriaClinica create(HistoriaClinica h, Connection c) throws SQLException {
        throw new SQLException("Use create(h, c, pacienteId)");
    }

    /**
     * Persiste una nueva Historia Clínica asociándola al ID del paciente.
     *
     * @param h La Historia Clínica a crear.
     * @param c La conexión JDBC activa.
     * @param pacienteId El ID del paciente al que se asociará la historia.
     * @return La Historia Clínica con su ID generado actualizado.
     * @throws SQLException Si ocurre un error al insertar.
     */
    public HistoriaClinica create(HistoriaClinica h, Connection c, long pacienteId) throws SQLException {
        String sql = "INSERT INTO historia_clinica (eliminado,nro_historia,grupo_sanguineo,antecedentes,medicacion_actual,observaciones,fecha_apertura,paciente_id) VALUES (?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, h.isEliminado());
            ps.setString(2, h.getNroHistoria());
            if (h.getGrupoSanguineo() != null) {
                ps.setString(3, h.getGrupoSanguineo().db());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            ps.setString(4, h.getAntecedentes());
            ps.setString(5, h.getMedicacionActual());
            ps.setString(6, h.getObservaciones());
            if (h.getFechaApertura() != null) {
                ps.setDate(7, java.sql.Date.valueOf(h.getFechaApertura()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            ps.setLong(8, pacienteId);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    h.setId(rs.getLong(1));
                }
            }
            return h;
        }
    }

    /**
     * Busca una Historia Clínica por ID.
     *
     * @param id El ID de la historia a buscar.
     * @param c La conexión JDBC activa.
     * @return Un Optional que contiene la Historia Clínica o vacío.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public Optional<HistoriaClinica> read(long id, Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM historia_clinica WHERE id=?")) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Recupera todas las Historias Clínicas activas.
     *
     * @param c La conexión JDBC activa.
     * @return Una lista de Historias Clínicas.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public java.util.List<HistoriaClinica> readAll(Connection c) throws SQLException {
        List<HistoriaClinica> list = new ArrayList<HistoriaClinica>();
        try (PreparedStatement ps = c.prepareStatement("SELECT * FROM historia_clinica WHERE eliminado=0 ORDER BY id DESC"); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /**
     * Actualiza los datos de una Historia Clínica existente.
     *
     * @param h La Historia Clínica con los nuevos datos.
     * @param c La conexión JDBC activa.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public void update(HistoriaClinica h, Connection c) throws SQLException {
        String sql = "UPDATE historia_clinica SET eliminado=?, nro_historia=?, grupo_sanguineo=?, antecedentes=?, medicacion_actual=?, observaciones=?, fecha_apertura=? WHERE id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, h.isEliminado());
            ps.setString(2, h.getNroHistoria());
            if (h.getGrupoSanguineo() != null) {
                ps.setString(3, h.getGrupoSanguineo().db());
            } else {
                ps.setNull(3, Types.VARCHAR);
            }
            ps.setString(4, h.getAntecedentes());
            ps.setString(5, h.getMedicacionActual());
            ps.setString(6, h.getObservaciones());
            if (h.getFechaApertura() != null) {
                ps.setDate(7, java.sql.Date.valueOf(h.getFechaApertura()));
            } else {
                ps.setNull(7, Types.DATE);
            }
            ps.setLong(8, h.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Realiza la baja lógica de una Historia Clínica por ID.
     *
     * @param id El ID de la historia a eliminar.
     * @param c La conexión JDBC activa.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public void delete(long id, Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE historia_clinica SET eliminado=1 WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Realiza la baja lógica de una Historia Clínica buscando por la clave foránea {@code paciente_id}.
     *
     * @param pacienteId El ID del paciente cuya historia se eliminará.
     * @param c La conexión JDBC activa.
     * @throws SQLException Si ocurre un error al ejecutar la baja lógica o si el registro no es encontrado.
     */
    @Override
    public void deleteByPacienteId(long pacienteId, Connection c) throws SQLException {
        String sql = "UPDATE historia_clinica SET eliminado=1 WHERE paciente_id=?";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, pacienteId);

            int affectedRows = ps.executeUpdate();

            if (affectedRows == 0) {
                // Si no se afectó ninguna fila, lanzamos una excepción
                // para forzar el ROLLBACK en el PacienteServiceImpl.
                throw new SQLException("Error de integridad: No se encontró Historia Clínica activa para el paciente ID: " + pacienteId);
            }
        }
    }
}