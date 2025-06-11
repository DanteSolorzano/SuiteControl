package main.suitecontrol;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.stage.StageStyle;
import main.ResourceBundle.ConfigProperties;
import main.dao.HabitacionDao;
import main.modelo.Cliente;
import main.modelo.Habitacion;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class HabitacionesController implements Initializable {

    public Label labelTipoHabitacion;
    public Label labelNumeroHabitacion;
    public Label labelPrecioNoche;
    public Label labelEstado;
    public Button btnRegresarH;
    public Label labelManejadorDeHrespedes;
    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private ComboBox<String> cboEstado;

    @FXML
    private ComboBox<String> cboTipo;

    @FXML
    private TableView<Habitacion> tableViewHabitaciones;

    @FXML
    private TextField txtNumeroHabitacion;

    @FXML
    private TextField txtPrecio;

    private HabitacionDao habitacionDao;

    private ContextMenu cmOpciones;

    private Habitacion habitacionSeleccion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //poblar de datos los combo box
        String[] opcionesTipo = {"Estandar", "Doble", "Suite"};
        ObservableList<String> itemsTipo = FXCollections.observableArrayList(opcionesTipo);
        cboTipo.setItems(itemsTipo);
        cboTipo.setValue("Seleccione");

        String[] opcionesEstado = {"Disponible", "Reservada", "Fuera de servicio"};
        ObservableList<String> itemsEst = FXCollections.observableArrayList(opcionesEstado);
        cboEstado.setItems(itemsEst);
        cboEstado.setValue("Disponible");

        this.habitacionDao = new HabitacionDao();

        cargarHabitaciones();

        //creamos el menu y extraemos el cliente
        cmOpciones = new ContextMenu();

        MenuItem miEditar = new MenuItem("Editar");
        MenuItem miEliminar = new MenuItem("Eliminar");

        cmOpciones.getItems().addAll(miEditar, miEliminar);


        miEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                int index = tableViewHabitaciones.getSelectionModel().getSelectedIndex();

                habitacionSeleccion = tableViewHabitaciones.getItems().get(index);

                //System.out.println( habitacionSeleccion );

                cboTipo.getSelectionModel().select(habitacionSeleccion.getTipo_habitacion());
                txtNumeroHabitacion.setText(habitacionSeleccion.getNumero_habitacion());
                txtPrecio.setText(habitacionSeleccion.getPrecio_noche());
                cboEstado.getSelectionModel().select(habitacionSeleccion.getEstado());

                btnCancelar.setDisable(false);




            }
        });

        miEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                int index = tableViewHabitaciones.getSelectionModel().getSelectedIndex();

                Habitacion habitacionEliminar = tableViewHabitaciones.getItems().get(index);

                Alert alert = new Alert (Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("¿Estas seguro de eliminar la habitacion " + habitacionEliminar.getNumero_habitacion() + " ?");
                alert.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> result = alert.showAndWait();

                if( result.get() == ButtonType.OK){

                    boolean rsp = habitacionDao.eliminar(habitacionEliminar.getId_habitacion());
                        if (rsp){

                            Alert alert2 = new Alert (Alert.AlertType.INFORMATION);
                            alert2.setTitle("Exito");
                            alert2.setHeaderText(null);
                            alert2.setContentText("Se elimino correctamente la habitacion");
                            alert2.initStyle(StageStyle.UTILITY);
                            alert2.showAndWait();

                            //mandamos llamar cargar clientes para que agregue los nuevos clientes a la lista del tableview
                            cargarHabitaciones();

                        } else {
                            Alert alert2 = new Alert (Alert.AlertType.ERROR);
                            alert2.setTitle("Error");
                            alert2.setHeaderText(null);
                            alert2.setContentText("Hubo un error al eliminar la habitacion");
                            alert2.initStyle(StageStyle.UTILITY);
                            alert2.showAndWait();
                        }
                }
            }
        });

        tableViewHabitaciones.setContextMenu(cmOpciones);


    }

    public void btnGuardadOnAction(ActionEvent event) {

        if(habitacionSeleccion == null){
            Habitacion habitacion = new Habitacion();

            habitacion.setTipo_habitacion(cboTipo.getSelectionModel().getSelectedItem());
            habitacion.setNumero_habitacion(txtNumeroHabitacion.getText());
            habitacion.setPrecio_noche(txtPrecio.getText());
            habitacion.setEstado(cboEstado.getSelectionModel().getSelectedItem());

            System.out.println(habitacion.toString());

            boolean rsp = this.habitacionDao.registrar(habitacion);

            if(rsp ){
                Alert alert = new Alert (Alert.AlertType.INFORMATION);
                alert.setTitle("Exito");
                alert.setHeaderText(null);
                alert.setContentText("Se registro correctamente la habitacion");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                limpiarCampos();
                cargarHabitaciones();
            } else {
                Alert alert = new Alert (Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error con el registro de la habitacion");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }
        } else {

            habitacionSeleccion.setTipo_habitacion(cboTipo.getSelectionModel().getSelectedItem());
            habitacionSeleccion.setNumero_habitacion(txtNumeroHabitacion.getText());
            habitacionSeleccion.setPrecio_noche(txtPrecio.getText());
            habitacionSeleccion.setEstado(cboEstado.getSelectionModel().getSelectedItem());

            boolean rsp = this.habitacionDao.editar(habitacionSeleccion);

            if(rsp ){
                Alert alert = new Alert (Alert.AlertType.INFORMATION);
                alert.setTitle("Exito");
                alert.setHeaderText(null);
                alert.setContentText("Se edito correctamente la habitacion");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                limpiarCampos();
                cargarHabitaciones();

                habitacionSeleccion = null;

                btnCancelar.setDisable(true);
            } else {
                Alert alert = new Alert (Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error con la edicion de la habitacion");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }

        }

    }

    private void limpiarCampos(){
        cboTipo.getSelectionModel().select("Seleccione");
        txtNumeroHabitacion.setText("");
        txtPrecio.setText("");
        cboEstado.getSelectionModel().select("Disponible");
    }

    public void cargarHabitaciones(){

        tableViewHabitaciones.getItems().clear();
        tableViewHabitaciones.getColumns().clear();

        List<Habitacion> habitaciones = this.habitacionDao.listar();

        ObservableList<Habitacion> data = FXCollections.observableArrayList(habitaciones);

        // Columna ID
        TableColumn<Habitacion, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("Id_habitacion"));

        // Columna tipo habitacion
        TableColumn<Habitacion, String> tipoCol = new TableColumn<>("Tipo de habitacion");
        tipoCol.setCellValueFactory(new PropertyValueFactory<>("tipo_habitacion"));

        // Columna numero habiacion
        TableColumn<Habitacion, String> numeroCol = new TableColumn<>("N° habitacion");
        numeroCol.setCellValueFactory(new PropertyValueFactory<>("numero_habitacion"));

        // Columna precio por noche
        TableColumn<Habitacion, String> precioCol = new TableColumn<>("Precio por noche");
        precioCol.setCellValueFactory(new PropertyValueFactory<>("precio_noche"));

        // Columna Estado
        TableColumn<Habitacion, String> estadoCol = new TableColumn<>("Estado");
        estadoCol.setCellValueFactory(new PropertyValueFactory<>("estado"));



        // Agregar todas las columnas a la tabla
        tableViewHabitaciones.getColumns().addAll(idCol, tipoCol, numeroCol, precioCol, estadoCol);

        // Cargar los datos
        tableViewHabitaciones.setItems(data);

    }

    public void btnCancelarOnAction(ActionEvent event) {
        habitacionSeleccion = null;

        limpiarCampos();

        btnCancelar.setDisable(true);
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

        labelTipoHabitacion.setText(bundle.getString("tipo_habitacion"));
        labelNumeroHabitacion.setText(bundle.getString("numero_habitacion"));
        labelPrecioNoche.setText(bundle.getString("precio_x_noche"));
        labelEstado.setText(bundle.getString("estado"));
        btnGuardar.setText(bundle.getString("guardarH"));
        btnCancelar.setText(bundle.getString("cancelarH"));
        btnRegresarH.setText(bundle.getString("regresarH"));
        labelManejadorDeHrespedes.setText(bundle.getString("manejador_habitaciones"));

    }

}
