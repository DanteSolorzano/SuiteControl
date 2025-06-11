package main.ResourceBundle;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class ConfigProperties {
    private static final String CONFIG_FILE = "config.properties";
    private static Properties properties = new Properties();

    static {
        try (InputStream input = ConfigProperties.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                properties.load(input);
            } else {
                System.err.println("Archivo config.properties no encontrado, se usarán valores por defecto.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static Locale getLocale() {
        String idioma = properties.getProperty("idioma", "es");
        String country = properties.getProperty("country", "MX");
        return new Locale(idioma, country);
    }
}
