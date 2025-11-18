package config;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Clase de configuración responsable de establecer la conexión con la base de datos.
 * Lee los parámetros de conexión (URL, usuario, contraseña) desde un archivo externo 'db.properties'
 * cargándolo desde el Classpath del proyecto.
 */
public class DatabaseConnection {

    /**
     * Carga las propiedades de configuración buscando el archivo 'db.properties'
     * dentro del Classpath (carpeta src/main/resources).
     *
     * @return Objeto Properties con los datos cargados.
     * @throws RuntimeException Si el archivo 'db.properties' no se encuentra en la ubicación de recursos.
     */
    private static Properties loadProps() {
        Properties p = new Properties();

        try (InputStream fis = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {

            if (fis == null) {
                throw new IOException("No se encontró el archivo db.properties en el Classpath. Verifique la carpeta src/main/resources.");
            }

            p.load(fis);

        } catch (IOException e) {
            throw new RuntimeException("Error al cargar db.properties: " + e.getMessage(), e);
        }

        return p;
    }

    /**
     * Obtiene una nueva conexión a la base de datos MySQL.
     *
     * @return Una conexión activa a la base de datos.
     * @throws SQLException Si ocurre un error al intentar conectar (ej. credenciales inválidas o servidor no disponible).
     */
    public static Connection getConnection() throws SQLException {
        Properties p = loadProps();
        return DriverManager.getConnection(
                p.getProperty("db.url"),
                p.getProperty("db.user"),
                p.getProperty("db.password")
        );
    }
}