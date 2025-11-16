package dao.impl;

import config.DatabaseConnection;
import dao.PacienteDao;
import models.HistoriaClinica;
import models.Paciente;
import java.sql.*;
import java.util.*;

/**
 * Implementación concreta del DAO para la entidad {@link Paciente}.
 * Utiliza JDBC y {@link PreparedStatement} para interactuar con la base de datos MySQL.
 * <p>
 * Implementa la optimización N+1 utilizando un LEFT JOIN en los métodos de lectura
 * para recuperar el Paciente junto con su {@link HistoriaClinica} asociada en una sola consulta.
 * </p>
 */
public class PacienteDaoImpl implements PacienteDao {

    /**
     * Mapea el resultado de una fila del {@link ResultSet} a un objeto {@link Paciente} completo.
     * Lee columnas de la tabla 'paciente' y las columnas de 'historia_clinica' (si existen).
     *
     * @param rs El conjunto de resultados JDBC.
     * @return Un objeto Paciente con su Historia Clínica asociada si existe.
     * @throws SQLException Si ocurre un error al leer los datos.
     */
    private Paciente map(ResultSet rs) throws SQLException {
        Paciente p = new Paciente();
        p.setId(rs.getLong("id"));
        p.setEliminado(rs.getBoolean("eliminado"));
        p.setNombre(rs.getString("nombre"));
        p.setApellido(rs.getString("apellido"));
        p.setDni(rs.getString("dni"));
        java.sql.Date f = rs.getDate("fecha_nacimiento");
        p.setFechaNacimiento(f != null ? f.toLocalDate() : null);

        long hcId = rs.getLong("hc_id");

        if (hcId > 0) {
            HistoriaClinica h = new HistoriaClinica();
            h.setId(hcId);
            h.setEliminado(rs.getBoolean("hc_eliminado"));
            h.setNroHistoria(rs.getString("nro_historia"));
            String gs = rs.getString("grupo_sanguineo");
            h.setGrupoSanguineo(HistoriaClinica.GrupoSanguineo.fromDb(gs));
            h.setAntecedentes(rs.getString("antecedentes"));
            h.setMedicacionActual(rs.getString("medicacion_actual"));
            h.setObservaciones(rs.getString("observaciones"));
            java.sql.Date fApertura = rs.getDate("fecha_apertura");
            h.setFechaApertura(fApertura != null ? fApertura.toLocalDate() : null);

            p.setHistoriaClinica(h); // asigna la historia clinica al paciente
        }

        return p;
    }

    // --- Métodos de Conveniencia (Autoconexión) ---

    /**
     * Persiste un nuevo paciente, delegando al método transaccional.
     */
    @Override
    public Paciente create(Paciente p) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            return create(p, c);
        }
    }

    /**
     * Busca un paciente por ID, delegando al método transaccional.
     */
    @Override
    public Optional<Paciente> read(long id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            return read(id, c);
        }
    }

    /**
     * Recupera todos los pacientes activos, delegando al método transaccional.
     */
    @Override
    public List<Paciente> readAll() throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            return readAll(c);
        }
    }

    /**
     * Actualiza un paciente existente, delegando al método transaccional.
     */
    @Override
    public void update(Paciente p) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            update(p, c);
        }
    }

    /**
     * Realiza la baja lógica de un paciente por ID, delegando al método transaccional.
     */
    @Override
    public void delete(long id) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            delete(id, c);
        }
    }

    /**
     * Busca un paciente por DNI, delegando al método transaccional.
     */
    @Override
    public Optional<Paciente> findByDni(String dni) throws SQLException {
        try (Connection c = DatabaseConnection.getConnection()) {
            return findByDni(dni, c);
        }
    }

    // --- Métodos Transaccionales (Conexión Inyectada) ---

    /**
     * Persiste un nuevo paciente en la base de datos.
     *
     * @param p El paciente a crear.
     * @param c La conexión JDBC activa.
     * @return El paciente con su ID generado actualizado.
     * @throws SQLException Si ocurre un error al insertar o al obtener las claves generadas.
     */
    @Override
    public Paciente create(Paciente p, Connection c) throws SQLException {
        String sql = "INSERT INTO paciente (eliminado,nombre,apellido,dni,fecha_nacimiento) VALUES (?,?,?,?,?)";
        try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setBoolean(1, p.isEliminado());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellido());
            ps.setString(4, p.getDni());
            if (p.getFechaNacimiento() != null) {
                ps.setDate(5, java.sql.Date.valueOf(p.getFechaNacimiento()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    p.setId(rs.getLong(1));
                }
            }
            return p;
        }
    }

    /**
     * Busca un paciente por ID. Utiliza LEFT JOIN para recuperar la Historia Clínica en la misma consulta.
     *
     * @param id El ID del paciente a buscar.
     * @param c La conexión JDBC activa.
     * @return Un Optional que contiene el paciente completo o vacío.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public Optional<Paciente> read(long id, Connection c) throws SQLException {
        String sql = "SELECT p.*, " +
                "hc.id AS hc_id, hc.eliminado AS hc_eliminado, hc.nro_historia, " +
                "hc.grupo_sanguineo, hc.antecedentes, hc.medicacion_actual, " +
                "hc.observaciones, hc.fecha_apertura " +
                "FROM paciente p " +
                "LEFT JOIN historia_clinica hc ON p.id = hc.paciente_id AND hc.eliminado = 0 " +
                "WHERE p.id = ? AND p.eliminado = 0";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
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
     * Recupera todos los pacientes activos. Utiliza LEFT JOIN para recuperar la Historia Clínica en la misma consulta.
     *
     * @param c La conexión JDBC activa.
     * @return Una lista de pacientes completos.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public java.util.List<Paciente> readAll(Connection c) throws SQLException {
        String sql = "SELECT p.*, " +
                "hc.id AS hc_id, hc.eliminado AS hc_eliminado, hc.nro_historia, " +
                "hc.grupo_sanguineo, hc.antecedentes, hc.medicacion_actual, " +
                "hc.observaciones, hc.fecha_apertura " +
                "FROM paciente p " +
                "LEFT JOIN historia_clinica hc ON p.id = hc.paciente_id AND hc.eliminado = 0 " +
                "WHERE p.eliminado = 0 " +
                "ORDER BY p.id DESC";

        List<Paciente> list = new ArrayList<Paciente>();

        try (PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    /**
     * Actualiza los datos de un paciente existente.
     *
     * @param p El paciente con los nuevos datos.
     * @param c La conexión JDBC activa.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public void update(Paciente p, Connection c) throws SQLException {
        String sql = "UPDATE paciente SET eliminado=?, nombre=?, apellido=?, dni=?, fecha_nacimiento=? WHERE id=?";
        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, p.isEliminado());
            ps.setString(2, p.getNombre());
            ps.setString(3, p.getApellido());
            ps.setString(4, p.getDni());
            if (p.getFechaNacimiento() != null) {
                ps.setDate(5, java.sql.Date.valueOf(p.getFechaNacimiento()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setLong(6, p.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Realiza la baja lógica de un paciente (estableciendo el campo 'eliminado' en true).
     *
     * @param id El ID del paciente a eliminar.
     * @param c La conexión JDBC activa.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public void delete(long id, Connection c) throws SQLException {
        try (PreparedStatement ps = c.prepareStatement("UPDATE paciente SET eliminado=1 WHERE id=?")) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Busca un paciente por DNI. Utiliza LEFT JOIN para recuperar la Historia Clínica en la misma consulta.
     *
     * @param dni El DNI del paciente a buscar.
     * @param c La conexión JDBC activa.
     * @return Un Optional que contiene el paciente completo o vacío.
     * @throws SQLException Si ocurre un error al acceder a la base de datos.
     */
    @Override
    public Optional<Paciente> findByDni(String dni, Connection c) throws SQLException {
        String sql = "SELECT p.*, " +
                "hc.id AS hc_id, hc.eliminado AS hc_eliminado, hc.nro_historia, " +
                "hc.grupo_sanguineo, hc.antecedentes, hc.medicacion_actual, " +
                "hc.observaciones, hc.fecha_apertura " +
                "FROM paciente p " +
                "LEFT JOIN historia_clinica hc ON p.id = hc.paciente_id AND hc.eliminado = 0 " +
                "WHERE p.dni = ? AND p.eliminado = 0";

        try (PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, dni);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(map(rs));
                }
            }
        }
        return Optional.empty();
    }
}