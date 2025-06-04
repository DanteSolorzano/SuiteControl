package main.suitecontrol;
import javafx.scene.chart.NumberAxis;
import main.dao.OcupacionDao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;
import main.ResourceBundle.ConfigProperties;
import main.modelo.RegistroOcupacion;

public class AnalisisController implements Initializable {

    @FXML
    private Button buttonGraficas;

    @FXML
    private Button buttonResultados;

    @FXML
    private ComboBox<Integer> cboAñoAnalisis;

    @FXML
    private ComboBox<String> cboMesAnalisis;

    @FXML
    private LineChart<Number, Number> lineChartOcupacion;





    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        //llenar los combo box con opciones
        String[] opciones = {"Enero", "Febero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};
        ObservableList<String> items = FXCollections.observableArrayList(opciones);
        cboMesAnalisis.setItems(items);
        cboMesAnalisis.setValue("Selecciones el mes");

        // Llenar combo box de años dinámicamente
        int añoActual = Year.now().getValue();
        List<Integer> años = new ArrayList<>();
        for (int i = añoActual; i <= añoActual + 10; i++) { // 10 años futuros
            años.add(i);
        }
        cboAñoAnalisis.setItems(FXCollections.observableArrayList(años));
        cboAñoAnalisis.setValue(añoActual); // Establecer año actual como predeterminado

    }

    @FXML
    private void buttonResultados(ActionEvent event) {
        String mesTexto = cboMesAnalisis.getValue();
        Integer año = cboAñoAnalisis.getValue();

        if (mesTexto == null || año == null || mesTexto.equals("Selecciones el mes")) {
            mostrarAlerta("Error", "Debe seleccionar un mes y un año válidos.");
            return;
        }

        int mes = cboMesAnalisis.getItems().indexOf(mesTexto) + 1;
        int fechaNumerica = año * 100 + mes;

        List<RegistroOcupacion> datos = obtenerDatosOcupacion();

        if (datos.size() < 2) {
            mostrarAlerta("Error", "No hay suficientes datos históricos para predecir.");
            return;
        }

        double[] resultado = calcularRegresionLineal(datos);
        double m = resultado[0];
        double b = resultado[1];
        double prediccion = m * fechaNumerica + b;

        mostrarAlerta("Predicción de ocupación", "Reservaciones estimadas para " + mesTexto + " " + año + ": " + Math.round(prediccion));
    }

    private List<RegistroOcupacion> obtenerDatosOcupacion() {
        List<RegistroOcupacion> lista = new ArrayList<>();

        String cadena = "jdbc:sqlite:database.db";


        try (
                Connection conexion = DriverManager.getConnection(cadena);


                PreparedStatement ps = conexion.prepareStatement("SELECT STRFTIME('%Y-%m', fecha_entrada) AS mes, COUNT(id_reservacion) AS total_reservaciones FROM reservaciones GROUP BY mes ORDER BY mes");
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String fecha = rs.getString("mes"); // formato YYYY-MM
                int total = rs.getInt("total_reservaciones");

                String[] partes = fecha.split("-");
                int año = Integer.parseInt(partes[0]);
                int mes = Integer.parseInt(partes[1]);
                int fechaNumerica = año * 100 + mes;

                lista.add(new RegistroOcupacion(fechaNumerica, total));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    private double[] calcularRegresionLineal(List<RegistroOcupacion> datos) {
        int n = datos.size();
        double sumaX = 0, sumaY = 0, sumaXY = 0, sumaX2 = 0;

        for (RegistroOcupacion r : datos) {
            double x = r.getFechaNumerica();
            double y = r.getTotalReservaciones();

            sumaX += x;
            sumaY += y;
            sumaXY += x * y;
            sumaX2 += x * x;
        }

        double m = (n * sumaXY - sumaX * sumaY) / (n * sumaX2 - sumaX * sumaX);
        double b = (sumaY - m * sumaX) / n;

        return new double[]{m, b}; // pendiente y ordenada
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }



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

    @FXML
    private void graficarPrediccion(ActionEvent event) {
        // Obtener año seleccionado del ComboBox normal
        Integer añoBase = cboAñoAnalisis.getValue();
        if (añoBase == null) {
            mostrarAlerta("Advertencia", "Debes seleccionar un año para graficar.");
            return;
        }

        // Obtener datos históricos y entrenar modelo
        List<RegistroOcupacion> datos = obtenerDatosOcupacion();
        if (datos.size() < 2) {
            mostrarAlerta("Error", "No hay suficientes datos históricos para generar una gráfica.");
            return;
        }

        OcupacionDao dao = new OcupacionDao();
        dao.entrenarModelo(datos);

        // Limpiar gráfica
        lineChartOcupacion.getData().clear();

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName("Ocupación estimada");

        int contadorMes = 1;
        for (int i = 0; i < 3; i++) { // 3 años desde añoBase
            int año = añoBase + i;
            for (int mes = 1; mes <= 12; mes++) {
                double pred = dao.predecir(año, mes);
                series.getData().add(new XYChart.Data<>(contadorMes++, pred));
            }
        }

        lineChartOcupacion.getData().add(series);
    }

    @FXML
    public void buttonGraficar(ActionEvent event) {
        String mesTexto = cboMesAnalisis.getValue();
        Integer añoSeleccionado = cboAñoAnalisis.getValue();

        if (mesTexto == null || añoSeleccionado == null || mesTexto.equals("Selecciones el mes")) {
            mostrarAlerta("Error", "Debe seleccionar un mes y un año válidos.");
            return;
        }

        int mes = cboMesAnalisis.getItems().indexOf(mesTexto) + 1;

        List<RegistroOcupacion> datos = obtenerDatosOcupacion();

        if (datos.size() < 2) {
            mostrarAlerta("Error", "No hay suficientes datos históricos para graficar predicciones.");
            return;
        }

        // Entrenar el modelo
        OcupacionDao modelo = new OcupacionDao();
        modelo.entrenarModelo(datos);

        // Limpiar gráfica anterior
        lineChartOcupacion.getData().clear();

        // Configurar ejes
        NumberAxis xAxis = (NumberAxis) lineChartOcupacion.getXAxis();
        xAxis.setLabel("Año");
        xAxis.setAutoRanging(false);
        xAxis.setLowerBound(añoSeleccionado - 1);
        xAxis.setUpperBound(añoSeleccionado + 3); // Mostrar desde año actual + 3 años
        xAxis.setTickUnit(1);

        NumberAxis yAxis = (NumberAxis) lineChartOcupacion.getYAxis();
        yAxis.setLabel("Reservaciones estimadas");
        yAxis.setAutoRanging(true);

        // Crear serie para predicciones
        XYChart.Series<Number, Number> seriePrediccion = new XYChart.Series<>();
        seriePrediccion.setName("Predicción para " + mesTexto);

        // Solo 3 puntos: año actual + 1, +2 y +3
        for (int i = 0; i < 3; i++) {
            int añoPrediccion = añoSeleccionado + i;
            double prediccion = modelo.predecir(añoPrediccion, mes);

            // Usar el año como valor X (no la fecha numérica completa)
            seriePrediccion.getData().add(new XYChart.Data<>(añoPrediccion, prediccion));
        }

        // Añadir serie a la gráfica
        lineChartOcupacion.getData().add(seriePrediccion);

    }






}
