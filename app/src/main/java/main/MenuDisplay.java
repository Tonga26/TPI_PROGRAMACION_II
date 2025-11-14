package main;

/**
 * Clase utilitaria responsable de la capa de presentación visual en la consola.
 * Contiene métodos estáticos dedicados exclusivamente a imprimir menús y opciones
 * para el usuario, separando la interfaz visual de la lógica de negocio.
 */
public class MenuDisplay {

    /**
     * Muestra el menú principal del sistema en la consola con un formato visual estilizado.
     * Lista todas las operaciones disponibles para la gestión de Pacientes e Historias Clínicas.
     */
    public static void mostrarMenuPrincipal() {
        System.out.println("\n");
        System.out.println("╔════════════════════════════════════════════════════╗");
        System.out.println("║           SISTEMA DE GESTIÓN DE CLÍNICA            ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║ 1. Crear Paciente (con Historia Clínica)           ║");
        System.out.println("║ 2. Listar todos los Pacientes                      ║");
        System.out.println("║ 3. Buscar Paciente por DNI                         ║");
        System.out.println("║ 4. Actualizar Paciente                             ║");
        System.out.println("║ 5. Actualizar Historia Clínica de un Paciente      ║");
        System.out.println("║ 6. Listar todas las Historias Clínicas             ║");
        System.out.println("║ 7. Eliminar Paciente (Baja Lógica)                 ║");
        System.out.println("╠════════════════════════════════════════════════════╣");
        System.out.println("║ 0. Salir                                           ║");
        System.out.println("╚════════════════════════════════════════════════════╝");
        System.out.print(" ➤ Ingrese una opción: ");
    }
}