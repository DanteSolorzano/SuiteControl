package main.suitecontrol;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static java.sql.DriverManager.getConnection;

public class Conexion {
    public static void main(String[] args) {
        String cadena = "jdbc:sqlite:database.db";
        try{
            Connection conexion = DriverManager.getConnection(cadena);

            String insertar = "INSERT INTO clientes(id_cliente, nombre, apellido_p, apellido_m, telefono, correo, identificacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(insertar);

            ps.setInt(1, 1);
            ps.setString(2, "Dante");
            ps.setString(3, "Solorzano");
            ps.setString(4, "Ferrer");
            ps.setInt(5, 462160519);
            ps.setString(6, "solorzanodante19@gmail.com");
            ps.setString(7, "INE");
            ps.execute();



        } catch (Exception ex){
            System.err.println(ex);
        }

    }
}
