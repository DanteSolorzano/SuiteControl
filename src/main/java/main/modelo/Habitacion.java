package main.modelo;

public class Habitacion {

    int id_habitacion;
    String tipo_habitacion;
    String numero_habitacion;
    String precio_noche;
    String estado;

    public Habitacion() {
    }

    public int getId_habitacion() {
        return id_habitacion;
    }

    public void setId_habitacion(int id_habitacion) {
        this.id_habitacion = id_habitacion;
    }

    public String getTipo_habitacion() {
        return tipo_habitacion;
    }

    public void setTipo_habitacion(String tipo_habitacion) {
        this.tipo_habitacion = tipo_habitacion;
    }

    public String getNumero_habitacion() {
        return numero_habitacion;
    }

    public void setNumero_habitacion(String numero_habitacion) {
        this.numero_habitacion = numero_habitacion;
    }

    public String getPrecio_noche() {
        return precio_noche;
    }

    public void setPrecio_noche(String precio_noche) {
        this.precio_noche = precio_noche;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    @Override
    public String toString() {
        return "Habitacion{" +
                "id_habitacion=" + id_habitacion +
                ", tipo_habitacion='" + tipo_habitacion + '\'' +
                ", numero_habitacion='" + numero_habitacion + '\'' +
                ", precio_noche='" + precio_noche + '\'' +
                ", estado='" + estado + '\'' +
                '}';
    }
}
