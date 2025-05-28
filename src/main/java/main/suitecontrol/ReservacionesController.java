package main.suitecontrol;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import main.dao.ClienteDao;
import main.dao.HabitacionDao;
import main.dao.ReservacionDao;
import main.modelo.Cliente;
import main.modelo.Habitacion;
import main.modelo.Reservacion;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservacionesController implements Initializable {

    @FXML
    private ComboBox<Cliente> cboCliente;

    @FXML
    private ComboBox<Habitacion> cboHabitacion;

    @FXML
    private DatePicker fecha_final;

    @FXML
    private DatePicker fecha_inicio;

    @FXML
    private TableView<Reservacion> tvReservaciones;

    public Button btnCancelarReserva;

    private ReservacionDao reservacionDao;

    private ContextMenu cmOpciones;

    private Reservacion reservacionSeleccion;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        //Poblamos de datos los combo  box con informacion de las tablas y ayuda de sus clases
        ClienteDao clienteDao = new ClienteDao();
        List<Cliente> listaClientes = clienteDao.listar();
        ObservableList<Cliente> clientesObservable = FXCollections.observableArrayList(listaClientes);
        cboCliente.setItems(clientesObservable);

        //Formatear las opciones del cbo
        cboCliente.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Cliente cliente, boolean empty) {
                super.updateItem(cliente, empty);
                if (empty || cliente == null) {
                    setText(null);
                } else {
                    setText(cliente.getNombre() + " " + cliente.getApellido_p() + " " + cliente.getApellido_m());
                }
            }
        });
        cboCliente.setButtonCell(cboCliente.getCellFactory().call(null));

        HabitacionDao habitacionDao = new HabitacionDao();
        List<Habitacion> listaHabitaciones = habitacionDao.listarDisponible();
        ObservableList<Habitacion> habitacionObservable = FXCollections.observableArrayList(listaHabitaciones);
        cboHabitacion.setItems(habitacionObservable);

        //formatear las opciones de habitacion
        cboHabitacion.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Habitacion habitacion, boolean empty) {
                super.updateItem(habitacion, empty);
                if (empty || habitacion == null) {
                    setText(null);
                } else {
                    setText(habitacion.getTipo_habitacion() + " - " + habitacion.getNumero_habitacion());
                }
            }
        });
        cboHabitacion.setButtonCell(cboHabitacion.getCellFactory().call(null));


        this.reservacionDao = new ReservacionDao();

        cargarReservaciones();

        //creamos el menu y extraemos el cliente
        cmOpciones = new ContextMenu();

        MenuItem miEditar = new MenuItem("Editar");
        MenuItem miEliminar = new MenuItem("Eliminar");

        cmOpciones.getItems().addAll(miEditar, miEliminar);

        miEditar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = tvReservaciones.getSelectionModel().getSelectedIndex();

                if (index >= 0) {
                    reservacionSeleccion = tvReservaciones.getItems().get(index);

                    for (Cliente c : cboCliente.getItems()) {
                        if (c.getId_cliente() == reservacionSeleccion.getId_cliente()) {
                            cboCliente.getSelectionModel().select(c);
                            break;
                        }
                    }

                    for (Habitacion h : cboHabitacion.getItems()) {
                        if (h.getId_habitacion() == reservacionSeleccion.getId_habitacion()) {
                            cboHabitacion.getSelectionModel().select(h);
                            break;
                        }
                    }

                    fecha_inicio.setValue(LocalDate.parse(reservacionSeleccion.getFecha_entrada()));
                    fecha_final.setValue(LocalDate.parse(reservacionSeleccion.getFecha_salida()));

                    btnCancelarReserva.setDisable(false);
                }

            }
        });

        miEliminar.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int index = tvReservaciones.getSelectionModel().getSelectedIndex();

                Reservacion reservacionEliminar = tvReservaciones.getItems().get(index);

                Alert alert = new Alert (Alert.AlertType.CONFIRMATION);
                alert.setHeaderText(null);
                alert.setContentText("¿Estas seguro de eliminar la reservacion n°" + reservacionEliminar.getId_reservacion() + " ?");
                alert.initStyle(StageStyle.UTILITY);
                Optional<ButtonType> result = alert.showAndWait();

                if( result.get() == ButtonType.OK){

                    boolean rsp = reservacionDao.eliminar(reservacionEliminar.getId_cliente());
                    if (rsp){

                        Alert alert2 = new Alert (Alert.AlertType.INFORMATION);
                        alert2.setTitle("Exito");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Se elimino correctamente el la reservacion n°"+ reservacionEliminar.getId_reservacion());
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();

                        //mandamos llamar cargar reservaciones para que agregue las nuevas reservaciones a la lista del tableview
                        cargarReservaciones();

                    } else {
                        Alert alert2 = new Alert (Alert.AlertType.ERROR);
                        alert2.setTitle("Error");
                        alert2.setHeaderText(null);
                        alert2.setContentText("Hubo un error al eliminar la reservacion");
                        alert2.initStyle(StageStyle.UTILITY);
                        alert2.showAndWait();
                    }
                }
            }

        });

        tvReservaciones.setContextMenu(cmOpciones);

    }

    public void btnGuardarOnAction(ActionEvent event) {

        if( reservacionSeleccion == null){
                Reservacion reservacion = new Reservacion();
                Cliente clienteSeleccionado = cboCliente.getSelectionModel().getSelectedItem();
                Habitacion habitacionSeleccionada = cboHabitacion.getSelectionModel().getSelectedItem();

                reservacion.setId_cliente(clienteSeleccionado.getId_cliente());
                reservacion.setId_habitacion(habitacionSeleccionada.getId_habitacion());
                reservacion.setFecha_entrada(fecha_inicio.getValue().toString());
                reservacion.setFecha_salida(fecha_final.getValue().toString());

                //obtener dias
                long dias = ChronoUnit.DAYS.between(fecha_inicio.getValue(), fecha_final.getValue());

                //obtener precio por noche

                double precio = Double.parseDouble(habitacionSeleccionada.getPrecio_noche());

                //obtener precio con iva configuracion dinamica de impuestos
                double iva = 0.16;
                double precioConIva = precio * (1 + iva);

                //obtener total y agregarlo a la tabla

                double total = dias * precioConIva;
                reservacion.setTotal(total);

                //llamada a consola auxiliar
                //System.out.println( reservacion );

                boolean rsp = this.reservacionDao.registrar(reservacion);

                if (rsp){

                    Alert alert = new Alert (Alert.AlertType.INFORMATION);
                    alert.setTitle("Exito");
                    alert.setHeaderText(null);
                    alert.setContentText("Se registro correctamente la reservacion");
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                    //mandamos llamar el metodo para limpiar despues de que se registro la reservacion
                    limpiarCampos();
                    //mandamos llamar cargar clientes para que agregue los nuevos clientes a la lista del tableview
                    cargarReservaciones();
                } else {
                    Alert alert = new Alert (Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText(null);
                    alert.setContentText("Hubo un error con el registro de la reservacion");
                    alert.initStyle(StageStyle.UTILITY);
                    alert.showAndWait();
                }

        } else {
            Cliente clienteSeleccionado = cboCliente.getSelectionModel().getSelectedItem();
            Habitacion habitacionSeleccionada = cboHabitacion.getSelectionModel().getSelectedItem();

            reservacionSeleccion.setId_cliente(clienteSeleccionado.getId_cliente());
            reservacionSeleccion.setId_habitacion(habitacionSeleccionada.getId_habitacion());
            reservacionSeleccion.setFecha_entrada(fecha_inicio.getValue().toString());
            reservacionSeleccion.setFecha_salida(fecha_final.getValue().toString());
            //obtener dias
            long dias = ChronoUnit.DAYS.between(fecha_inicio.getValue(), fecha_final.getValue());

            //obtener precio por noche

            double precio = Double.parseDouble(habitacionSeleccionada.getPrecio_noche());

            //obtener precio con iva configuracion dinamica de impuestos
            double iva = 0.16;
            double precioConIva = precio * (1 + iva);

            //obtener total y agregarlo a la tabla

            double total = dias * precioConIva;
            reservacionSeleccion.setTotal(total);

            boolean rsp = this.reservacionDao.editar(reservacionSeleccion);

            if (rsp){

                Alert alert = new Alert (Alert.AlertType.INFORMATION);
                alert.setTitle("Exito");
                alert.setHeaderText(null);
                alert.setContentText("Se edito correctamente la reservacion");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                //mandamos llamar el metodo para limpiar despues de que se registro la reservacion
                limpiarCampos();
                //mandamos llamar cargar clientes para que agregue los nuevos clientes a la lista del tableview
                cargarReservaciones();
                reservacionSeleccion = null;
                btnCancelarReserva.setDisable(true);
            } else {
                Alert alert = new Alert (Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error con editar la reservacion");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }
        }
    }

    private void limpiarCampos(){
        cboCliente.getSelectionModel().clearSelection();
        cboHabitacion.getSelectionModel().clearSelection();
        fecha_inicio.setValue(null);
        fecha_final.setValue(null);
    }

    public void cargarReservaciones(){

        tvReservaciones.getItems().clear();
        tvReservaciones.getColumns().clear();

        List<Reservacion> reservaciones = this.reservacionDao.listar();

        ObservableList<Reservacion> data = FXCollections.observableArrayList(reservaciones);

        // Columna ID
        TableColumn<Reservacion, Integer> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id_reservacion"));

        // Columna Nombre Cliente
        TableColumn<Reservacion, String> nombreClienteCol = new TableColumn<>("Cliente");
        nombreClienteCol.setCellValueFactory(new PropertyValueFactory<>("nombre_cliente"));

        // Columna Número de Habitación
        TableColumn<Reservacion, String> numeroHabCol = new TableColumn<>("Habitación");
        numeroHabCol.setCellValueFactory(new PropertyValueFactory<>("numero_habitacion"));

        // Columna Fecha Entrada
        TableColumn<Reservacion, String> entradaCol = new TableColumn<>("Entrada");
        entradaCol.setCellValueFactory(new PropertyValueFactory<>("fecha_entrada"));

        // Columna Fecha Salida
        TableColumn<Reservacion, String> salidaCol = new TableColumn<>("Salida");
        salidaCol.setCellValueFactory(new PropertyValueFactory<>("fecha_salida"));

        // Columna Total
        TableColumn<Reservacion, Double> totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Agregar columnas a la tabla
        tvReservaciones.getColumns().addAll(idCol, nombreClienteCol, numeroHabCol, entradaCol, salidaCol, totalCol);

        // Cargar los datos
        tvReservaciones.setItems(data);

    }

    @FXML
    void btnCancelarROnAction(ActionEvent event) {
        reservacionSeleccion = null;
        limpiarCampos();
        btnCancelarReserva.setDisable(true);

    }

    //logica de navegacion entre escenas
    private Stage stage;
    private Scene scene;
    private Parent root;


    public void buttonRegresar(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("menu-view.fxml"));
        stage = (Stage)((Node)event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}
