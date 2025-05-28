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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.stage.StageStyle;
import main.dao.ClienteDao;
import main.modelo.Cliente;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;



public class ClientesController implements Initializable {
    public TextField txtAmaterno;
    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private ComboBox<String> cboID;

    @FXML
    private TextField txtApaterno;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TableView<Cliente> tableViewClientes;

    private ClienteDao clienteDao;

    private ContextMenu cmOPciones;

    private Cliente clienteSeleccion;


    //Agrega las opciones al combo box
    @Override
    public void initialize(URL url, ResourceBundle rb){

        String[] opciones = {"INE", "Pasaporte", "Cartilla militar", "ID(extranjeros)"};

        ObservableList<String> items = FXCollections.observableArrayList(opciones);

        cboID.setItems(items);
        cboID.setValue("Seleccione");

        this.clienteDao = new ClienteDao();

        cargarClientes();

        //creamos el menu y extraemos el cliente
        cmOPciones = new ContextMenu();

        MenuItem miEditar = new MenuItem("Editar");
        MenuItem miEliminar = new MenuItem("Eliminar");

        cmOPciones.getItems().addAll(miEditar, miEliminar);


        miEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                int index = tableViewClientes.getSelectionModel().getSelectedIndex();

                clienteSeleccion = tableViewClientes.getItems().get(index);

                //System.out.println( clienteSeleccion );

                txtNombre.setText(clienteSeleccion.getNombre());
                txtApaterno.setText(clienteSeleccion.getApellido_p());
                txtAmaterno.setText(clienteSeleccion.getApellido_m());
                txtTelefono.setText(clienteSeleccion.getNumero());
                txtCorreo.setText(clienteSeleccion.getCorreo());
                cboID.getSelectionModel().select(clienteSeleccion.getIdentificacion());

                btnCancelar.setDisable(false);




            }
        });

        miEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                int index = tableViewClientes.getSelectionModel().getSelectedIndex();

                Cliente clienteEliminar = tableViewClientes.getItems().get(index);

                Alert alert = new Alert (Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("¿Estas seguro de eliminar el usuario " + clienteEliminar.getNombre() + " ?");
                alert.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> result = alert.showAndWait();

                if( result.get() == ButtonType.OK){

                    boolean rsp = clienteDao.eliminar(clienteEliminar.getId_cliente());
                    if (rsp){

                        Alert alert2 = new Alert (Alert.AlertType.INFORMATION);
                        alert2.setTitle("Exito");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Se elimino correctamente el usuario");
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();

                        //mandamos llamar cargar clientes para que agregue los nuevos clientes a la lista del tableview
                        cargarClientes();

                    } else {
                        Alert alert2 = new Alert (Alert.AlertType.ERROR);
                        alert2.setTitle("Error");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Hubo un error al eliminar el usuario");
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();
                    }
                }
            }
        });

        tableViewClientes.setContextMenu(cmOPciones);

    }

    @FXML
    void btnGuardadOnAction(ActionEvent event) {

        if (clienteSeleccion == null){
                Cliente cliente = new Cliente();

                cliente.setNombre( txtNombre.getText());
                cliente.setApellido_p( txtApaterno.getText());
                cliente.setApellido_m( txtAmaterno.getText());
                cliente.setCorreo( txtCorreo.getText());
                cliente.setNumero(txtTelefono.getText());
                cliente.setIdentificacion(cboID.getSelectionModel().getSelectedItem());

                //System.out.println( cliente.toString() );

                boolean rsp = this.clienteDao.registrar(cliente);

                if (rsp){

                    Alert alert = new Alert (Alert.AlertType.INFORMATION);
                    alert.setTitle("Exito");
                    alert.setHeaderText(null);
                    alert.setContentText("Se registro correctamente el usuario");
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                    //mandamos llamar el metodo para limpiar despues de que se registro la tarea
                    limpiarCampos();
                    //mandamos llamar cargar clientes para que agregue los nuevos clientes a la lista del tableview
                    cargarClientes();

                } else {
                    Alert alert = new Alert (Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Hubo un error con el registro del usuario");
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }
        } else {

            clienteSeleccion.setNombre(txtNombre.getText());
            clienteSeleccion.setApellido_p(txtApaterno.getText());
            clienteSeleccion.setApellido_m(txtAmaterno.getText());
            clienteSeleccion.setNumero(txtTelefono.getText());
            clienteSeleccion.setCorreo(txtCorreo.getText());
            clienteSeleccion.setIdentificacion(cboID.getSelectionModel().getSelectedItem());

            boolean rsp = this.clienteDao.editar(clienteSeleccion);

            if (rsp){

                Alert alert = new Alert (Alert.AlertType.INFORMATION);
                alert.setTitle("Exito");
                alert.setHeaderText(null);
                alert.setContentText("Se actualizo correctamente el usuario");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                //mandamos llamar el metodo para limpiar despues de que se registro la tarea
                limpiarCampos();
                //mandamos llamar cargar clientes para que agregue los nuevos clientes a la lista del tableview
                cargarClientes();

                clienteSeleccion = null;

                btnCancelar.setDisable(true);

            } else {
                Alert alert = new Alert (Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error al editar el usuario");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }

        }

    }

    private void limpiarCampos(){
        txtNombre.setText("");
        txtApaterno.setText("");
        txtAmaterno.setText("");
        txtTelefono.setText("");
        txtCorreo.setText("");
        cboID.getSelectionModel().select("Seleccione");
    }

    public void cargarClientes(){

        tableViewClientes.getItems().clear();
        tableViewClientes.getColumns().clear();

        List<Cliente> clientes = this.clienteDao.listar();

        ObservableList<Cliente> data = FXCollections.observableArrayList(clientes);

        // Columna ID
        TableColumn<Cliente, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id_cliente"));

        // Columna Nombre
        TableColumn<Cliente, String> nombreCol = new TableColumn<>("Nombre");
        nombreCol.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Columna Apellido Paterno
        TableColumn<Cliente, String> apellidoPCol = new TableColumn<>("Apellido P.");
        apellidoPCol.setCellValueFactory(new PropertyValueFactory<>("apellido_p"));

        // Columna Apellido Materno
        TableColumn<Cliente, String> apellidoMCol = new TableColumn<>("Apellido M.");
        apellidoMCol.setCellValueFactory(new PropertyValueFactory<>("apellido_m"));

        // Columna Teléfono
        TableColumn<Cliente, String> telefonoCol = new TableColumn<>("Teléfono");
        telefonoCol.setCellValueFactory(new PropertyValueFactory<>("numero"));

        // Columna Correo
        TableColumn<Cliente, String> correoCol = new TableColumn<>("Correo");
        correoCol.setCellValueFactory(new PropertyValueFactory<>("correo"));

        //Columna Identificacion
        TableColumn<Cliente, String> identificacionCol = new TableColumn<>("Identificacion");
        identificacionCol.setCellValueFactory(new PropertyValueFactory<>("identificacion"));

        // Agregar todas las columnas a la tabla
        tableViewClientes.getColumns().addAll(idCol, nombreCol, apellidoPCol, apellidoMCol, telefonoCol, correoCol, identificacionCol);

        // Cargar los datos
        tableViewClientes.setItems(data);

    }

    //boton cancelar
    @FXML
    void btnCancelarOnAction(ActionEvent event) {
        clienteSeleccion = null;
        limpiarCampos();
        btnCancelar.setDisable(true);
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
