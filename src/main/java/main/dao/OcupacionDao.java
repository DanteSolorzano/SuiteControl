package main.dao;


import main.modelo.RegistroOcupacion;
import java.util.List;

public class OcupacionDao {

    private double a = 0;
    private double b = 0;

    // Entrenar modelo usando lista de registros históricos
    public void entrenarModelo(List<RegistroOcupacion> datos) {
        int n = datos.size();
        if (n == 0) return;

        double sumaX = 0, sumaY = 0, sumaXY = 0, sumaX2 = 0;

        for (RegistroOcupacion reg : datos) {
            int x = reg.getFechaNumerica();
            int y = reg.getTotalReservaciones();

            sumaX += x;
            sumaY += y;
            sumaXY += x * y;
            sumaX2 += x * x;
        }

        b = (n * sumaXY - sumaX * sumaY) / (n * sumaX2 - sumaX * sumaX);
        a = (sumaY - b * sumaX) / n;
    }


    // Nuevo método para predecir usando año y mes como componentes separados
    public double predecir(int año, int mes) {
        return a + b * (año * 100 + mes);
    }

    // Método auxiliar para obtener el valor base de X
    public double getBaseValue(int año, int mes) {
        return año + (mes - 1) / 12.0;
    }

    public double getPendiente() {
        return b;
    }

    public double getInterseccion() {
        return a;
    }
}

