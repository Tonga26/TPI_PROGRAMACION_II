package main;

/**
 * Punto de entrada principal (Entry Point) de la aplicación.
 * <p>
 * Su única responsabilidad es instanciar el controlador del menú principal
 * ({@link AppMenu}) e invocar su método {@link AppMenu#start()}
 * para comenzar la ejecución del programa.
 * </p>
 */
public class Main {

    /**
     * Método principal que se ejecuta al iniciar la aplicación.
     *
     * @param args Argumentos de línea de comandos (no utilizados en esta aplicación).
     */
    public static void main(String[] args) {
        AppMenu app = new AppMenu();
        app.start();
    }
}
