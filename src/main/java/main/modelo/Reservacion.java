package main.modelo;

public class Reservacion {

    private int id_reservacion;
    private int id_cliente;
    private int id_habitacion;
    private String fecha_entrada;
    private String fecha_salida;
    private double total;
    private String nombre_cliente;         // nombre + apellido paterno + apellido materno
    private String numero_habitacion;


    public Reservacion() {
    }

    @Override
    public String toString() {
        return "Reservacion{" +
                "total=" + total +
                ", fecha_salida='" + fecha_salida + '\'' +
                ", fecha_entrada='" + fecha_entrada + '\'' +
                ", id_habitacion=" + id_habitacion +
                ", id_cliente=" + id_cliente +
                ", id_reservacion=" + id_reservacion +
                '}';
    }

    public int getId_reservacion() {
        return id_reservacion;
    }

    public void setId_reservacion(int id_reservacion) {
        this.id_reservacion = id_reservacion;
    }

    public int getId_cliente() {
        return id_cliente;
    }

    public void setId_cliente(int id_cliente) {
        this.id_cliente = id_cliente;
    }

    public int getId_habitacion() {
        return id_habitacion;
    }

    public void setId_habitacion(int id_habitacion) {
        this.id_habitacion = id_habitacion;
    }

    public String getFecha_entrada() {
        return fecha_entrada;
    }

    public void setFecha_entrada(String fecha_entrada) {
        this.fecha_entrada = fecha_entrada;
    }

    public String getFecha_salida() {
        return fecha_salida;
    }

    public void setFecha_salida(String fecha_salida) {
        this.fecha_salida = fecha_salida;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Getter y Setter para nombre_cliente
    public String getNombre_cliente() {
        return nombre_cliente;
    }

    public void setNombre_cliente(String nombre_cliente) {
        this.nombre_cliente = nombre_cliente;
    }

    // Getter y Setter para numero_habitacion
    public String getNumero_habitacion() {
        return numero_habitacion;
    }

    public void setNumero_habitacion(String numero_habitacion) {
        this.numero_habitacion = numero_habitacion;
    }


}
