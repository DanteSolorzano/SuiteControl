package main.modelo;

public class RegistroOcupacion {
    private int fechaNumerica; // Ej: 202401
    private int totalReservaciones;

    public RegistroOcupacion(int fechaNumerica, int totalReservaciones) {
        this.fechaNumerica = fechaNumerica;
        this.totalReservaciones = totalReservaciones;
    }

    public int getFechaNumerica() {
        return fechaNumerica;
    }

    public int getTotalReservaciones() {
        return totalReservaciones;
    }
}
