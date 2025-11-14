package main;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import models.HistoriaClinica;
import models.HistoriaClinica.GrupoSanguineo;
import models.Paciente;
import service.HistoriaClinicaService;
import service.HistoriaClinicaServiceImpl;
import service.PacienteService;
import service.PacienteServiceImpl;

/**
 * Controlador que maneja la lógica de interacción con el usuario.
 * <p>
 * Esta clase actúa como intermediaria entre la entrada de datos (consola) y la capa de servicios.
 * Se encarga de solicitar datos, validar entradas básicas, invocar los métodos de negocio
 * correspondientes y formatear la salida visual para el usuario.
 * </p>
 */
public class MenuHandler {

    private Scanner scanner;
    private PacienteService pacienteService;
    private HistoriaClinicaService hcService;

    /**
     * Constructor que inicializa el manejador con el Scanner y los servicios necesarios.
     *
     * @param scanner Instancia compartida de Scanner para la lectura de datos.
     */
    public MenuHandler(Scanner scanner) {
        this.scanner = scanner;
        this.pacienteService = new PacienteServiceImpl();
        this.hcService = new HistoriaClinicaServiceImpl();
    }

    /**
     * Gestiona el flujo de creación de un nuevo Paciente y su Historia Clínica.
     * Solicita todos los datos requeridos y opcionales, construye los objetos del modelo
     * y delega la persistencia al servicio principal.
     */
    public void crearPaciente() {
        try {
            System.out.println("== Alta Paciente ==");
            System.out.print("Nombre: ");
            String nombre = scanner.nextLine();
            System.out.print("Apellido: ");
            String apellido = scanner.nextLine();
            System.out.print("DNI: ");
            String dni = scanner.nextLine();
            System.out.print("Fecha nacimiento (YYYY-MM-DD, opcional): ");
            String fn = scanner.nextLine();
            LocalDate fNac = fn.isBlank() ? null : LocalDate.parse(fn);

            System.out.println("== Historia Clínica (Obligatoria) ==");
            System.out.print("Nro historia: ");
            String nro = scanner.nextLine();
            System.out.print("Grupo sanguíneo (A+,A-,B+,B-,AB+,AB-,O+,O- o vacío): ");
            String gs = scanner.nextLine().trim().toUpperCase();
            GrupoSanguineo grupo = gs.isBlank() ? null : GrupoSanguineo.fromDb(gs);
            System.out.print("Antecedentes (opcional): ");
            String ant = scanner.nextLine();
            System.out.print("Medicación actual (opcional): ");
            String med = scanner.nextLine();
            System.out.print("Observaciones (opcional): ");
            String obs = scanner.nextLine();

            Paciente p = new Paciente();
            p.setNombre(nombre);
            p.setApellido(apellido);
            p.setDni(dni);
            p.setFechaNacimiento(fNac);

            HistoriaClinica h = new HistoriaClinica();
            h.setNroHistoria(nro);
            h.setGrupoSanguineo(grupo);
            h.setAntecedentes(ant);
            h.setMedicacionActual(med);
            h.setObservaciones(obs);
            h.setFechaApertura(LocalDate.now());

            p.setHistoriaClinica(h);

            pacienteService.insertar(p);

            System.out.println("¡Paciente creado exitosamente!");
            System.out.println(p.toString());

        } catch (Exception e) {
            System.err.println("Error al crear paciente: " + e.getMessage());
        }
    }

    /**
     * Muestra un listado tabular de todos los pacientes registrados y activos.
     * Utiliza formato de columnas alineadas para mejorar la legibilidad.
     */
    public void listarPacientes() {
        try {
            List<Paciente> pacientes = pacienteService.getAll();
            if (pacientes.isEmpty()) {
                System.out.println("⚠ No hay pacientes registrados.");
                return;
            }

            String formato = "| %-4d | %-10s | %-15s | %-15s | %-12s | %-5s |%n";
            String linea   = "+------+------------+-----------------+-----------------+--------------+-------+";

            System.out.println("\n=== LISTADO DE PACIENTES ===");
            System.out.println(linea);
            System.out.printf("| %-4s | %-10s | %-15s | %-15s | %-12s | %-5s |%n", "ID", "DNI", "NOMBRE", "APELLIDO", "NRO HC", "GRUPO");
            System.out.println(linea);

            for (Paciente p : pacientes) {
                String nroHc = (p.getHistoriaClinica() != null) ? p.getHistoriaClinica().getNroHistoria() : "S/D";
                String grupo = (p.getHistoriaClinica() != null && p.getHistoriaClinica().getGrupoSanguineo() != null)
                        ? p.getHistoriaClinica().getGrupoSanguineo().db() : "-";

                System.out.printf(formato,
                        p.getId(),
                        p.getDni(),
                        p.getNombre(),
                        p.getApellido(),
                        nroHc,
                        grupo
                );
            }
            System.out.println(linea);

        } catch (Exception e) {
            System.err.println("Error al listar pacientes: " + e.getMessage());
        }
    }

    /**
     * Solicita un DNI y busca al paciente correspondiente.
     * Muestra la información encontrada en formato de "Ficha Técnica", incluyendo
     * datos personales y detalles de la historia clínica.
     */
    public void buscarPacientePorDni() {
        try {
            System.out.print("Ingrese DNI a buscar: ");
            String dni = scanner.nextLine();

            Optional<Paciente> opt = pacienteService.findByDni(dni);

            if (opt.isPresent()) {
                Paciente p = opt.get();
                HistoriaClinica h = p.getHistoriaClinica();

                System.out.println("\n════════════ FICHA DEL PACIENTE ════════════");
                System.out.printf(" %-20s: %s %s%n", "Nombre Completo", p.getNombre(), p.getApellido());
                System.out.printf(" %-20s: %s%n",    "DNI", p.getDni());
                System.out.printf(" %-20s: %s%n",    "Fecha Nacimiento", (p.getFechaNacimiento() != null ? p.getFechaNacimiento() : "No registrada"));
                System.out.println("────────────────────────────────────────────");
                System.out.println(" DATOS CLÍNICOS");
                if (h != null) {
                    System.out.printf(" %-20s: %s%n", "Nro. Historia", h.getNroHistoria());
                    System.out.printf(" %-20s: %s%n", "Grupo Sanguíneo", (h.getGrupoSanguineo() != null ? h.getGrupoSanguineo().db() : "N/A"));
                    System.out.printf(" %-20s: %s%n", "Observaciones", (h.getObservaciones() != null ? h.getObservaciones() : "-"));
                } else {
                    System.out.println(" (Sin Historia Clínica asociada)");
                }
                System.out.println("════════════════════════════════════════════");
            } else {
                System.out.println("\n⚠ No se encontró ningún paciente con DNI: " + dni);
            }

        } catch (Exception e) {
            System.err.println("Error al buscar paciente: " + e.getMessage());
        }
    }

    /**
     * Gestiona la actualización de datos de un Paciente existente.
     * Permite al usuario modificar campos individuales (Nombre, Apellido, DNI, Fecha)
     * manteniendo los valores actuales si se presiona ENTER.
     */
    public void actualizarPaciente() {
        try {
            System.out.print("Ingrese el ID del Paciente a actualizar: ");
            long pacienteId = Long.parseLong(scanner.nextLine());

            Optional<Paciente> opt = pacienteService.getById(pacienteId);
            if (opt.isEmpty()) {
                System.out.println("No se encontró un Paciente con ese ID.");
                return;
            }

            Paciente p = opt.get();
            System.out.println("Editando Paciente: " + p.toString());
            System.out.println("(Presione ENTER para mantener el valor actual)");

            System.out.print("Nuevo Nombre [" + p.getNombre() + "]: ");
            String nombre = scanner.nextLine();
            if (!nombre.isBlank()) p.setNombre(nombre);

            System.out.print("Nuevo Apellido [" + p.getApellido() + "]: ");
            String apellido = scanner.nextLine();
            if (!apellido.isBlank()) p.setApellido(apellido);

            System.out.print("Nuevo DNI [" + p.getDni() + "]: ");
            String dni = scanner.nextLine();
            if (!dni.isBlank()) p.setDni(dni);

            String fechaActual = (p.getFechaNacimiento() != null) ? p.getFechaNacimiento().toString() : "N/A";
            System.out.print("Nueva Fecha Nacimiento (YYYY-MM-DD) [" + fechaActual + "]: ");
            String fn = scanner.nextLine();
            if (!fn.isBlank()) p.setFechaNacimiento(LocalDate.parse(fn));

            pacienteService.actualizar(p);

            System.out.println("¡Paciente actualizado con éxito!");

        } catch (NumberFormatException e) {
            System.err.println("Error: Ingrese un ID numérico válido.");
        } catch (Exception e) {
            System.err.println("Error al actualizar el Paciente: " + e.getMessage());
        }
    }

    /**
     * Gestiona la actualización específica de la Historia Clínica asociada a un paciente.
     * Permite modificar campos médicos como el grupo sanguíneo y observaciones.
     */
    public void actualizarHistoriaClinica() {
        try {
            System.out.print("Ingrese el ID del Paciente cuya Historia Clínica desea actualizar: ");
            long pacienteId = Long.parseLong(scanner.nextLine());

            Optional<Paciente> opt = pacienteService.getById(pacienteId);
            if (opt.isEmpty()) {
                System.out.println("No se encontró un Paciente con ese ID.");
                return;
            }
            if (opt.get().getHistoriaClinica() == null) {
                System.out.println("Este paciente no tiene Historia Clínica (¡Error de datos!).");
                return;
            }

            HistoriaClinica h = opt.get().getHistoriaClinica();
            System.out.println("Editando Historia Clínica: " + h.brief());

            System.out.print("Nuevo grupo (A+,A-,etc. o vacío para dejar): ");
            String gs = scanner.nextLine().trim().toUpperCase();
            if (!gs.isBlank()) h.setGrupoSanguineo(GrupoSanguineo.fromDb(gs));

            System.out.print("Nuevas observaciones (o vacío para dejar): ");
            String obs = scanner.nextLine();
            if (!obs.isBlank()) h.setObservaciones(obs);

            hcService.actualizar(h);

            System.out.println("¡Historia Clínica actualizada con éxito!");

        } catch (NumberFormatException e) {
            System.err.println("Error: Ingrese un ID numérico válido.");
        } catch (Exception e) {
            System.err.println("Error al actualizar la Historia Clínica: " + e.getMessage());
        }
    }

    /**
     * Muestra un listado detallado de todas las Historias Clínicas.
     * Utiliza un formato de bloques verticales para permitir la visualización completa
     * de campos de texto extenso como antecedentes, medicación y observaciones.
     */
    public void listarHistoriasClinicas() {
        try {
            List<HistoriaClinica> historias = hcService.getAll();

            if (historias.isEmpty()) {
                System.out.println("⚠ No hay historias clínicas registradas.");
                return;
            }

            System.out.println("\n════════════ LISTADO DE HISTORIAS CLÍNICAS (" + "Cantidad: " + historias.size() + ") ════════════");

            for (HistoriaClinica h : historias) {
                String nro = (h.getNroHistoria() != null) ? h.getNroHistoria() : "S/D";
                String grupo = (h.getGrupoSanguineo() != null) ? h.getGrupoSanguineo().db() : "No def.";
                String fecha = (h.getFechaApertura() != null) ? h.getFechaApertura().toString() : "-";

                String ant = (h.getAntecedentes() != null && !h.getAntecedentes().isBlank())
                        ? h.getAntecedentes() : "Ninguno";

                String med = (h.getMedicacionActual() != null && !h.getMedicacionActual().isBlank())
                        ? h.getMedicacionActual() : "Ninguna";

                String obs = (h.getObservaciones() != null && !h.getObservaciones().isBlank())
                        ? h.getObservaciones() : "Sin observaciones";

                System.out.println("────────────────────────────────────────────────────────────");
                System.out.printf(" ID: %-4d | Nro HC: %-10s | Grupo: %-5s | Fecha: %s%n",
                        h.getId(), nro, grupo, fecha);
                System.out.println("────────────────────────────────────────────────────────────");

                System.out.println(" • Antecedentes:");
                System.out.println("   " + ant);
                System.out.println();

                System.out.println(" • Medicación Actual:");
                System.out.println("   " + med);
                System.out.println();

                System.out.println(" • Observaciones:");
                System.out.println("   " + obs);
                System.out.println();
            }
            System.out.println("════════════════════════════════════════════════════════════");

        } catch (Exception e) {
            System.err.println("Error al listar historias: " + e.getMessage());
        }
    }

    /**
     * Solicita el ID de un Paciente y ejecuta su eliminación lógica.
     * Esta operación desencadena una transacción que elimina tanto al Paciente
     * como a su Historia Clínica asociada.
     */
    public void eliminarPaciente() {
        try {
            System.out.print("Ingrese el ID del Paciente a eliminar (Baja Lógica): ");
            long id = Long.parseLong(scanner.nextLine());

            pacienteService.eliminar(id);

            System.out.println("Paciente (ID: " + id + ") eliminado con éxito (baja lógica).");

        } catch (NumberFormatException e) {
            System.err.println("Error: Ingrese un ID numérico válido.");
        } catch (Exception e) {
            System.err.println("Error al eliminar paciente: " + e.getMessage());
        }
    }
}