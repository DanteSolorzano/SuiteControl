package main.suitecontrol;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import javafx.scene.control.Alert;

public class AnalisisController implements Initializable {

    @FXML
    private Button buttonGraficas;

    @FXML
    private Button buttonResultados;

    @FXML
    private ComboBox<Integer> cboAñoAnalisis;

    @FXML
    private ComboBox<String> cboMesAnalisis;




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


    //logica de navegacion entre escenas
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void buttonRegresar(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("menu-view.fxml"));
        Parent root = loader.load();
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
