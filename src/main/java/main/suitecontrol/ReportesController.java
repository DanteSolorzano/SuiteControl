package main.suitecontrol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import main.ResourceBundle.ConfigProperties;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

public class ReportesController {
    @FXML
    private Button buttonGraficas;

    @FXML
    private Button buttonReporteOcupacion;

    @FXML
    private ComboBox<?> cboAñoAnalisis;

    @FXML
    private ComboBox<?> cboMesAnalisis;


    //logica de navegacion entre escenas
    private Stage stage;
    private Scene scene;
    private Parent root;


    public void buttonRegresar(ActionEvent event) throws IOException {
        // 1. Obtener el locale configurado (usando tu clase ConfigProperties)
        Locale locale = ConfigProperties.getLocale();

        // 2. Cargar el ResourceBundle con el locale actual
        ResourceBundle bundle = ResourceBundle.getBundle("labels", locale);

        // 3. Crear el FXMLLoader configurado
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
        loader.setResources(bundle);  // ¡Clave para la internacionalización!

        // 4. Cargar el Parent
        Parent root = loader.load();

        // 5. Configurar la escena y mostrar
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    public void buttonReporteOcupacion(ActionEvent event) {
        try {
            // Cargar el archivo .jrxml como recurso desde el classpath
            InputStream jrxmlStream = getClass().getResourceAsStream("/reports/reporteOcupacion.jrxml");
            if (jrxmlStream == null) {
                throw new FileNotFoundException("Archivo reporteOcupacion.jrxml no encontrado");
            }

            // Compilar directamente desde el stream (evita problemas de versión)
            //JasperReport reporte = JasperCompileManager.compileReport("reporteOcupacion.jrxml");
            JasperReport reporte =(JasperReport) JRLoader.loadObjectFromFile("reporteOcupacion.jasper");
            // Parámetros (si los necesitas)
            Map<String, Object> parametros = new HashMap<>();

            // Conexión a SQLite
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

            // Generar el reporte
            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, conn);

            // Mostrar el reporte
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Reporte de Ocupación");
            viewer.setVisible(true);


            // Cerrar conexión
            conn.close();

        } catch (Exception e) {
            System.err.println("Error al generar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void buttonReporteIngresosOA(ActionEvent event) {
        try {
            // Cargar el archivo .jrxml como recurso desde el classpath
            InputStream jrxmlStream = getClass().getResourceAsStream("/reports/ReporteIngresos.jrxml");
            if (jrxmlStream == null) {
                throw new FileNotFoundException("Archivo reporteIngresos.jrxml no encontrado");
            }

            // Compilar directamente desde el stream (evita problemas de versión)
            //JasperReport reporte = JasperCompileManager.compileReport("reporteOcupacion.jrxml");
            JasperReport reporte =(JasperReport) JRLoader.loadObjectFromFile("ReporteIngresos.jasper");
            // Parámetros (si los necesitas)
            Map<String, Object> parametros = new HashMap<>();

            // Conexión a SQLite
            Connection conn = DriverManager.getConnection("jdbc:sqlite:database.db");

            // Generar el reporte
            JasperPrint print = JasperFillManager.fillReport(reporte, parametros, conn);

            // Mostrar el reporte
            JasperViewer viewer = new JasperViewer(print, false);
            viewer.setTitle("Reporte de Ingresos");
            viewer.setVisible(true);

            // Cerrar conexión
            conn.close();

        } catch (Exception e) {
            System.err.println("Error al generar el reporte: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void buttonReportePrediccionOA(ActionEvent event) {
    }
}