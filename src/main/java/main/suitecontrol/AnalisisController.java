package main.suitecontrol;
import javafx.scene.chart.NumberAxis;

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
        for (int i = añoActual; i <= añoActual + 10; i++) { // 10 años futuros, establecido en .properties pero aun no implementado
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

        // Calcular regresión lineal
        double[] resultado = calcularRegresionLineal(datos);
        double m = resultado[0];
        double b = resultado[1];

        // Limpiar gráfico anterior
        lineChartOcupacion.getData().clear();

        NumberAxis xAxis = (NumberAxis) lineChartOcupacion.getXAxis();
        xAxis.setLabel("Año");
        xAxis.setAutoRanging(true);

        XYChart.Series<Number, Number> serie = new XYChart.Series<>();
        serie.setName("Predicción para " + mesTexto);

        // Llamar a recursividad para generar predicciones desde añoSeleccionado hasta +2
        agregarPrediccionesRecursivas(añoSeleccionado, mes, m, b, 0, 3, serie);

        lineChartOcupacion.getData().add(serie);

        // Etiquetas personalizadas del eje X
        xAxis.setTickLabelFormatter(new NumberAxis.DefaultFormatter(xAxis) {
            @Override
            public String toString(Number object) {
                int index = object.intValue() - 1;
                return String.valueOf(añoSeleccionado + index);
            }
        });
    }

    private void agregarPrediccionesRecursivas(int añoBase, int mes, double m, double b, int contador, int limite, XYChart.Series<Number, Number> serie) {
        if (contador >= limite) {
            return;
        }

        int añoActual = añoBase + contador;
        int fechaNumerica = añoActual * 100 + mes;
        double prediccion = m * fechaNumerica + b;
        double valorRedondeado = Math.round(prediccion);

        // Eje X: contador + 1 (para empezar en 1)
        serie.getData().add(new XYChart.Data<>(contador + 1, valorRedondeado));

        // Imprimir para depurar
        //System.out.printf("Predicción para %d: %.2f%n", añoActual, valorRedondeado);

        // Llamada recursiva
        agregarPrediccionesRecursivas(añoBase, mes, m, b, contador + 1, limite, serie);
    }









}
