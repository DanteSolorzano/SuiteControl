package main.dao;

import main.modelo.Cliente;
import main.modelo.Reservacion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ReservacionDao {

    String cadena = "jdbc:sqlite:database.db";

    public boolean registrar(Reservacion reservacion) {
        try {

            Connection conexion = DriverManager.getConnection(cadena);

            String insertar = "INSERT INTO reservaciones(id_cliente, id_habitacion, fecha_entrada, fecha_salida, total) VALUES ( ?, ?, ?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(insertar);

            ps.setInt(1, reservacion.getId_cliente());
            ps.setInt(2, reservacion.getId_habitacion());
            ps.setString(3, reservacion.getFecha_entrada());
            ps.setString(4, reservacion.getFecha_salida());
            ps.setDouble(5, reservacion.getTotal());

            ps.executeUpdate();
            ps.close();
            conexion.close();

            return true;


        } catch (Exception ex) {
            System.err.println(ex);
            System.err.println("ocurrio un error al generar la reservacion");
            System.err.println("ERROR: " + ex.getMessage());
            return false;
        }
    }

    public List<Reservacion> listar() {

        String cadena = "jdbc:sqlite:database.db";
        List<Reservacion> listaReservaciones = new ArrayList<>();


        try {
            Connection conexion = DriverManager.getConnection(cadena);

            String insertar = """
                        SELECT
                            r.id_reservacion,
                            r.id_cliente,
                            r.id_habitacion,
                            c.nombre,
                            c.apellido_p,
                            c.apellido_m,
                            h.numero_habitacion,
                            r.fecha_entrada,
                            r.fecha_salida,
                            r.total
                        FROM reservaciones r
                        JOIN clientes c ON r.id_cliente = c.id_cliente
                        JOIN habitaciones h ON r.id_habitacion = h.id_habitacion;
                    """;

            PreparedStatement ps = conexion.prepareStatement(insertar);
            ResultSet data = ps.executeQuery();

            while (data.next()) {

                Reservacion reservacion = new Reservacion();

                reservacion.setId_reservacion(data.getInt("id_reservacion"));
                reservacion.setId_cliente(data.getInt("id_cliente"));       // Necesario para editar
                reservacion.setId_habitacion(data.getInt("id_habitacion")); //  Necesario para editar
                reservacion.setNombre_cliente(
                        data.getString("nombre") + " " +
                                data.getString("apellido_p") + " " +
                                data.getString("apellido_m")
                );
                reservacion.setNumero_habitacion(data.getString("numero_habitacion"));
                reservacion.setFecha_entrada(data.getString("fecha_entrada"));
                reservacion.setFecha_salida(data.getString("fecha_salida"));
                reservacion.setTotal(data.getDouble("total"));

                listaReservaciones.add(reservacion);


            }
            data.close();
            ps.close();
            conexion.close();

        } catch (Exception ex) {
            System.err.println(ex);
            System.err.println("ocurrio un erro al listar la reservacion");
            System.err.println("ERROR: " + ex.getMessage());

        }
        return listaReservaciones;
    }

    public boolean editar(Reservacion reservacion) {
        String cadena = "jdbc:sqlite:database.db";

        try {
            String sql = "UPDATE reservaciones SET id_cliente = ?, id_habitacion = ?, fecha_entrada = ?, fecha_salida = ?, total = ? WHERE id_reservacion = ?;";

            Connection conexion = DriverManager.getConnection(cadena);
            PreparedStatement ps = conexion.prepareStatement(sql);

            ps.setInt(1, reservacion.getId_cliente());
            ps.setInt(2, reservacion.getId_habitacion());
            ps.setString(3, reservacion.getFecha_entrada());
            ps.setString(4, reservacion.getFecha_salida());
            ps.setDouble(5, reservacion.getTotal());
            ps.setInt(6, reservacion.getId_reservacion());

            ps.executeUpdate();

            ps.close();
            conexion.close();

            return true;

        } catch (Exception ex) {
            System.err.println(ex);
            System.err.println("Ocurrió un error al editar una reservación");
            System.err.println("ERROR: " + ex.getMessage());
            return false;
        }
    }

    public boolean eliminar(int id_reservacion){
        String cadena = "jdbc:sqlite:database.db";

        try{

            String insertar = "delete from reservaciones where id_reservacion = ?;";

            Connection conexion = DriverManager.getConnection(cadena);
            PreparedStatement ps = conexion.prepareStatement(insertar);

            ps.setInt(1, id_reservacion);
            ps.executeUpdate();
            ps.close();

            return true;
        }catch (Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al eliminar un cliente");
            System.err.println("ERROR: " + ex.getMessage());

            return false;
        }
    }

    public boolean estaDisponible(int idHabitacion, String fechaEntrada, String fechaSalida, int excluirReservaId) {
        String sql = """
        SELECT COUNT(*) 
        FROM reservaciones 
        WHERE id_habitacion = ?
          AND id_reservacion != ?
          AND (
            ? <= fecha_salida AND ? >= fecha_entrada
          )
        """;

        try (Connection conexion = DriverManager.getConnection(cadena);
             PreparedStatement ps = conexion.prepareStatement(sql)) {

            ps.setInt(1, idHabitacion);
            ps.setInt(2, excluirReservaId);
            ps.setString(3, fechaEntrada);
            ps.setString(4, fechaSalida);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (Exception ex) {
            System.err.println("Error al verificar disponibilidad: " + ex.getMessage());
            ex.printStackTrace();
        }
        return false;
    }

}
