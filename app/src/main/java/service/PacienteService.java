package service;

import java.sql.SQLException;
import java.util.Optional;
import models.Paciente;

/**
 * Interfaz que define las operaciones de lógica de negocio específicas para la entidad {@link Paciente} (Entidad A).
 * Hereda el contrato CRUD básico de {@link GenericService} y añade métodos particulares del dominio,
 * como la búsqueda por DNI.
 */
public interface PacienteService extends GenericService<Paciente> {

    /**
     * Busca un Paciente activo por su Documento Nacional de Identidad (DNI).
     *
     * @param dni El DNI del paciente a buscar.
     * @return Un Optional que contiene el Paciente completo si se encuentra, o vacío si no existe.
     * @throws SQLException Si ocurre un error de acceso a la base de datos.
     */
    Optional<Paciente> findByDni(String dni) throws SQLException;
}