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
import main.ResourceBundle.ConfigProperties;
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
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;

public class ReservacionesController implements Initializable {

    public Label labelHuesped;
    public Label labelHabitacion;
    public Label label_entrada;
    public Label label_salida;
    public Button btnAceptar;
    public Button btnRegresar;
    public Label labelGestionReservaciones;
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

    private TableColumn<Reservacion, Integer> idCol;
    private TableColumn<Reservacion, String> nombreClienteCol;
    private TableColumn<Reservacion, String> numeroHabCol;
    private TableColumn<Reservacion, String> entradaCol;
    private TableColumn<Reservacion, String> salidaCol;
    private TableColumn<Reservacion, Double> totalCol;

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
        List<Habitacion> listaHabitaciones = habitacionDao.listarDisponibles();
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

        inicializarColumnas();
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
                if (index >= 0) {
                    Reservacion reservacionEliminar = tvReservaciones.getItems().get(index);

                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setHeaderText(null);
                    alert.setContentText("¿Estas seguro de eliminar la reservacion n°" + reservacionEliminar.getId_reservacion() + "?");
                    Optional<ButtonType> result = alert.showAndWait();

                    if(result.get() == ButtonType.OK){
                        boolean rsp = reservacionDao.eliminar(reservacionEliminar.getId_reservacion());

                        if (rsp) {
                            // Eliminar directamente del TableView
                            tvReservaciones.getItems().remove(index);

                            Alert alert2 = new Alert(Alert.AlertType.INFORMATION);
                            alert2.setTitle("Exito");
                            alert2.setHeaderText(null);
                            alert2.setContentText("Reservación eliminada correctamente");
                            alert2.showAndWait();
                        } else {
                            Alert alert2 = new Alert(Alert.AlertType.ERROR);
                            alert2.setTitle("Error");
                            alert2.setHeaderText(null);
                            alert2.setContentText("Error al eliminar la reservación");
                            alert2.showAndWait();
                        }
                    }
                }
            }
        });

        tvReservaciones.setContextMenu(cmOpciones);

    }


    public void btnGuardarOnAction(ActionEvent event) {
        // Obtener valores comunes
        Cliente clienteSeleccionado = cboCliente.getSelectionModel().getSelectedItem();
        Habitacion habitacionSeleccionada = cboHabitacion.getSelectionModel().getSelectedItem();
        LocalDate entrada = fecha_inicio.getValue();
        LocalDate salida = fecha_final.getValue();

        // Validación básica de campos
        if (clienteSeleccionado == null || habitacionSeleccionada == null ||
                entrada == null || salida == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Por favor, complete todos los campos");
            alert.showAndWait();
            return;
        }

        // Validar disponibilidad ANTES de procesar
        if (!validarDisponibilidad(habitacionSeleccionada, entrada, salida)) {
            return;
        }

        if( reservacionSeleccion == null){
            Reservacion reservacion = new Reservacion();
            // Usamos las variables ya declaradas al inicio
            reservacion.setId_cliente(clienteSeleccionado.getId_cliente());
            reservacion.setId_habitacion(habitacionSeleccionada.getId_habitacion());
            reservacion.setFecha_entrada(entrada.toString());
            reservacion.setFecha_salida(salida.toString());

            // Obtener días
            long dias = ChronoUnit.DAYS.between(entrada, salida);

            // Obtener precio por noche
            double precio = Double.parseDouble(habitacionSeleccionada.getPrecio_noche());

            // Obtener precio con IVA
            double iva = 0.16;
            double precioConIva = precio * (1 + iva);

            // Calcular total
            double total = dias * precioConIva;
            reservacion.setTotal(total);

            boolean rsp = this.reservacionDao.registrar(reservacion);

            if (rsp){
                // Actualizar estado de la habitación a "Reservada"
                HabitacionDao habitacionDao = new HabitacionDao();
                habitacionDao.actualizarEstado(
                        habitacionSeleccionada.getId_habitacion(),
                        "Reservada"
                    );
                Alert alert = new Alert (Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Se registró correctamente la reservación");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                limpiarCampos();
                cargarReservaciones();
            } else {

                Alert alert = new Alert (Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error con el registro de la reservación");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
            }

        } else {
            //Guardar el ID de habitacion original antes de la edicion
            int idHabitacionOriginal = reservacionSeleccion.getId_habitacion();

            // Usamos las variables ya declaradas al inicio
            reservacionSeleccion.setId_cliente(clienteSeleccionado.getId_cliente());
            reservacionSeleccion.setId_habitacion(habitacionSeleccionada.getId_habitacion());
            reservacionSeleccion.setFecha_entrada(entrada.toString());
            reservacionSeleccion.setFecha_salida(salida.toString());

            // Obtener días
            long dias = ChronoUnit.DAYS.between(entrada, salida);

            // Obtener precio por noche
            double precio = Double.parseDouble(habitacionSeleccionada.getPrecio_noche());

            // Obtener precio con IVA
            double iva = 0.16;
            double precioConIva = precio * (1 + iva);

            // Calcular total
            double total = dias * precioConIva;
            reservacionSeleccion.setTotal(total);

            boolean rsp = this.reservacionDao.editar(reservacionSeleccion);

            if (rsp){
                // Actualizar estados de habitaciones
                HabitacionDao habitacionDao = new HabitacionDao();

                // ctualizar nueva habitación a "Reservada"
                habitacionDao.actualizarEstado(
                        habitacionSeleccionada.getId_habitacion(),
                        "Reservada"
                );

                //Si cambió de habitación, actualizar la ANTERIOR a "Disponible"
                if(idHabitacionOriginal != habitacionSeleccionada.getId_habitacion()) {
                    habitacionDao.actualizarEstado(
                            idHabitacionOriginal,
                            "Disponible"
                    );
                }

                Alert alert = new Alert (Alert.AlertType.INFORMATION);
                alert.setTitle("Éxito");
                alert.setHeaderText(null);
                alert.setContentText("Se editó correctamente la reservación");
                alert.initStyle(StageStyle.UTILITY);
                alert.showAndWait();
                limpiarCampos();
                cargarReservaciones();
                reservacionSeleccion = null;
                btnCancelarReserva.setDisable(true);
            } else {
                Alert alert = new Alert (Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Hubo un error al editar la reservación");
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

    private void inicializarColumnas() {
        // Limpiar columnas existentes
        tvReservaciones.getColumns().clear();

        // Crear columnas
        idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id_reservacion"));

        nombreClienteCol = new TableColumn<>("Cliente");
        nombreClienteCol.setCellValueFactory(new PropertyValueFactory<>("nombre_cliente"));

        numeroHabCol = new TableColumn<>("Habitación");
        numeroHabCol.setCellValueFactory(new PropertyValueFactory<>("numero_habitacion"));

        entradaCol = new TableColumn<>("Entrada");
        entradaCol.setCellValueFactory(new PropertyValueFactory<>("fecha_entrada"));

        salidaCol = new TableColumn<>("Salida");
        salidaCol.setCellValueFactory(new PropertyValueFactory<>("fecha_salida"));

        totalCol = new TableColumn<>("Total");
        totalCol.setCellValueFactory(new PropertyValueFactory<>("total"));

        // Agregar columnas a la tabla
        tvReservaciones.getColumns().addAll(idCol, nombreClienteCol, numeroHabCol, entradaCol, salidaCol, totalCol);
    }

    public void cargarReservaciones() {
        // Limpiar solo los items, no las columnas
        tvReservaciones.getItems().clear();

        List<Reservacion> reservaciones = this.reservacionDao.listar();
        tvReservaciones.getItems().addAll(reservaciones);
    }

    private boolean validarDisponibilidad(Habitacion habitacion, LocalDate entrada, LocalDate salida) {
        if (habitacion == null || entrada == null || salida == null) {
            return false;
        }

        // Validar que la fecha de salida sea posterior a la de entrada
        if (salida.isBefore(entrada) || salida.isEqual(entrada)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error en fechas");
            alert.setHeaderText(null);
            alert.setContentText("La fecha de salida debe ser posterior a la fecha de entrada");
            alert.showAndWait();
            return false;
        }

        // Validar disponibilidad en la base de datos
        int reservaIdExcluir = (reservacionSeleccion != null) ? reservacionSeleccion.getId_reservacion() : -1;

        if (!reservacionDao.estaDisponible(
                habitacion.getId_habitacion(),
                entrada.toString(),
                salida.toString(),
                reservaIdExcluir
        )) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de disponibilidad");
            alert.setHeaderText(null);
            alert.setContentText("La habitación no está disponible en las fechas seleccionadas");
            alert.showAndWait();
            return false;
        }

        return true;
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

        labelHuesped.setText(bundle.getString("seleccione_huesped"));
        labelHabitacion.setText(bundle.getString("seleccione_habitacion"));
        label_entrada.setText(bundle.getString("fecha_entrada"));
        label_salida.setText(bundle.getString("fecha_salida"));
        btnAceptar.setText(bundle.getString("aceptar"));
        btnCancelarReserva.setText(bundle.getString("cancelarR"));
        btnRegresar.setText(bundle.getString("regresarR"));
        labelGestionReservaciones.setText(bundle.getString("gestion_reservaciones"));
    }

}
