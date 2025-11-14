package models;

import java.time.LocalDate;

/**
 * Representa a un Paciente dentro del sistema de gestión.
 * <p>
 * Esta entidad actúa como el propietario de la relación uno a uno (1:1) unidireccional
 * con la clase {@link HistoriaClinica}. Extiende de {@link Base} para heredar
 * el identificador y el comportamiento de eliminación lógica.
 * </p>
 */
public class Paciente extends Base {

    private String nombre;
    private String apellido;
    private String dni;
    private LocalDate fechaNacimiento;
    private HistoriaClinica historiaClinica;

    /**
     * Constructor por defecto.
     * Inicializa una instancia de Paciente lista para ser utilizada.
     */
    public Paciente() {
        super();
    }

    /**
     * Obtiene el nombre de pila del paciente.
     *
     * @return El nombre del paciente.
     */
    public String getNombre() {
        return nombre;
    }

    /**
     * Establece el nombre de pila del paciente.
     *
     * @param nombre El nuevo nombre.
     */
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    /**
     * Obtiene el apellido del paciente.
     *
     * @return El apellido del paciente.
     */
    public String getApellido() {
        return apellido;
    }

    /**
     * Establece el apellido del paciente.
     *
     * @param apellido El nuevo apellido.
     */
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    /**
     * Obtiene el Documento Nacional de Identidad (DNI).
     *
     * @return El DNI del paciente.
     */
    public String getDni() {
        return dni;
    }

    /**
     * Establece el Documento Nacional de Identidad (DNI).
     *
     * @param dni El nuevo DNI.
     */
    public void setDni(String dni) {
        this.dni = dni;
    }

    /**
     * Obtiene la fecha de nacimiento del paciente.
     *
     * @return La fecha de nacimiento.
     */
    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    /**
     * Establece la fecha de nacimiento del paciente.
     *
     * @param fechaNacimiento La nueva fecha de nacimiento.
     */
    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    /**
     * Obtiene la Historia Clínica asociada a este paciente.
     *
     * @return La Historia Clínica, o null si no tiene una asignada.
     */
    public HistoriaClinica getHistoriaClinica() {
        return historiaClinica;
    }

    /**
     * Asocia una Historia Clínica a este paciente.
     * Representa la relación unidireccional donde el Paciente conoce a su Historia Clínica.
     *
     * @param historiaClinica La Historia Clínica a asociar.
     */
    public void setHistoriaClinica(HistoriaClinica historiaClinica) {
        this.historiaClinica = historiaClinica;
    }

    /**
     * Genera una representación en cadena del objeto Paciente.
     * Incluye identificadores, datos personales y un resumen breve de la historia clínica.
     *
     * @return Cadena descriptiva del paciente.
     */
    @Override
    public String toString() {
        return "Paciente{id=" + getId() + ", dni='" + dni + "', nombre='" + nombre + "', apellido='" + apellido
                + "', hc=" + (historiaClinica != null ? historiaClinica.brief() : "null") + "}";
    }
}