package main.suitecontrol;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import main.ResourceBundle.ConfigProperties;
import main.dao.HabitacionDao;

import java.io.IOException;
import java.util.EventObject;
import java.util.Locale;
import java.util.ResourceBundle;

public class MenuController {
    @FXML
    private Button btnAnalisiG;

    @FXML
    private Button btnClientesG;

    @FXML
    private Button btnHabitacionesG;

    @FXML
    private Button btnRegresar;

    @FXML
    private Button btnReportesG;

    @FXML
    private Button btnReservacionesG;

    @FXML
    private Label labelOpciones;

    //botones para interaccion entre escenas
    private Stage stage;
    private Scene scene;
    private Parent root;




    public void SwitchToInicio(ActionEvent event) throws IOException {
        Locale locale = ConfigProperties.getLocale();
        ResourceBundle bundle = ResourceBundle.getBundle("labels", locale);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("hello-view.fxml"));
        loader.setResources(bundle);
        Parent root = loader.load();

        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }



    public void buttonClientes(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("clientes-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void buttonHabitaciones(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("habitaciones-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void buttonReservaciones(ActionEvent event) throws IOException {
        HabitacionDao habitacionDao = new HabitacionDao();
        habitacionDao.actualizarEstadosSegunReservas();


        Parent root = FXMLLoader.load(getClass().getResource("reservaciones-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void buttonAnalisis(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("analisis-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void buttonReportes(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("reportes-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // Cambiar idioma a inglés
    public void ButtonEn(ActionEvent event) {
        loadLanguage("en", "US"); // "en" sin país
    }

    // Cambiar idioma a español (México)
    public void buttonEsp(ActionEvent event) {
        loadLanguage("es", "MX");
    }
    private void loadLanguage(String language, String country) {
        Locale locale = new Locale(language, country);
        ResourceBundle bundle = ResourceBundle.getBundle("labels", locale);

        btnRegresar.setText(bundle.getString("regresar"));
        btnClientesG.setText(bundle.getString("gestion_de_clientes"));
        btnHabitacionesG.setText(bundle.getString("gestion_de_habitaciones"));
        btnReservacionesG.setText(bundle.getString("gestion_de_reservaciones"));
        btnReportesG.setText(bundle.getString("generacion_de_reportes"));
        btnAnalisiG.setText(bundle.getString("analisis_de_datos"));
        labelOpciones.setText(bundle.getString("opciones"));
    }
}

