package main;

import java.util.Scanner;

/**
 * Clase principal que gestiona el flujo de control de la aplicación a través de un menú interactivo en consola.
 * Actúa como orquestador, coordinando la interacción entre la entrada del usuario y la ejecución
 * de las operaciones de lógica de negocio delegadas al MenuHandler.
 */
public class AppMenu {

    private MenuHandler menuHandler;
    private Scanner scanner;

    /**
     * Constructor que inicializa los componentes necesarios para el funcionamiento del menú.
     * Crea una instancia única de Scanner para la entrada de datos y configura el manejador del menú.
     */
    public AppMenu() {
        this.scanner = new Scanner(System.in);
        this.menuHandler = new MenuHandler(this.scanner);
    }

    /**
     * Inicia el ciclo de vida principal de la aplicación.
     * Mantiene un bucle activo que muestra las opciones del menú, captura la selección del usuario
     * y delega la ejecución a los métodos correspondientes.
     * <p>
     * El método incluye manejo de excepciones para asegurar que la aplicación no se detenga
     * ante entradas inválidas (no numéricas) o errores inesperados durante la ejecución.
     * </p>
     */
    public void start() {
        boolean running = true;

        while (running) {
            try {
                MenuDisplay.mostrarMenuPrincipal();
                int opcion = Integer.parseInt(scanner.nextLine());

                switch (opcion) {
                    case 1:
                        menuHandler.crearPaciente();
                        break;
                    case 2:
                        menuHandler.listarPacientes();
                        break;
                    case 3:
                        menuHandler.buscarPacientePorDni();
                        break;
                    case 4:
                        menuHandler.actualizarPaciente();
                        break;
                    case 5:
                        menuHandler.actualizarHistoriaClinica();
                        break;
                    case 6:
                        menuHandler.listarHistoriasClinicas();
                        break;
                    case 7:
                        menuHandler.eliminarPaciente();
                        break;
                    case 0:
                        System.out.println("Saliendo del sistema...");
                        running = false;
                        break;
                    default:
                        System.out.println("Opción no válida. Intente de nuevo.");
                        break;
                }
            } catch (NumberFormatException e) {
                System.err.println("Entrada inválida. Por favor, ingrese un número.");
            } catch (Exception e) {
                System.err.println("Ha ocurrido un error inesperado: " + e.getMessage());
            }
        }

        scanner.close();
    }
}