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

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class HelloController {

    private Stage stage;
    private Scene scene;
    private Parent root;

    // Referencias a los nodos
    @FXML
    private Label labelUsuario;
    @FXML
    private Label labelContrasena;
    @FXML
    private Button btnIniciar;
    @FXML
    private Button btnCrear;

    // Cambiar idioma a inglés
    public void ButtonEn(ActionEvent event) {
        loadLanguage("en", "US"); // "en" sin país
    }

    // Cambiar idioma a español (México)
    public void buttonEsp(ActionEvent event) {
        loadLanguage("es", "MX");
    }

    // Método generalizado para cargar idioma
    private void loadLanguage(String language, String country) {
        Locale locale = new Locale(language, country);
        ResourceBundle bundle = ResourceBundle.getBundle("labels", locale);

        labelUsuario.setText(bundle.getString("usuario"));
        labelContrasena.setText(bundle.getString("contrasena"));
        btnIniciar.setText(bundle.getString("iniciar_sesion"));
        btnCrear.setText(bundle.getString("crear_usuario"));
    }
    @FXML
    public void initialize() {
        // Este método se puede dejar vacío si usamos el ResourceBundle directamente en el FXML
    }

    public void SwitchToMenu(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
