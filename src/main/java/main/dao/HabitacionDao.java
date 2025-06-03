package main.dao;

import main.modelo.Cliente;
import main.modelo.Habitacion;
import main.modelo.Reservacion;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class HabitacionDao {

    public boolean registrar(Habitacion habitacion){

        String cadena = "jdbc:sqlite:database.db";

        try {
            Connection conexion = DriverManager.getConnection(cadena);

            String insertar = "INSERT INTO habitaciones(tipo_habitacion, numero_habitacion, precio_noche, estado) VALUES ( ?, ?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(insertar);

            ps.setString(1, habitacion.getTipo_habitacion());
            ps.setString(2, habitacion.getNumero_habitacion());
            ps.setString(3, habitacion.getPrecio_noche());
            ps.setString(4, habitacion.getEstado());


            ps.executeUpdate();
            ps.close();
            conexion.close();

            return true;

        } catch(Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al registrar una habitacion");
            System.err.println("ERROR: " + ex.getMessage());
            return false;

        }
    }

    public List<Habitacion> listar(){

        String cadena = "jdbc:sqlite:database.db";
        List<Habitacion> listaHabitaciones = new ArrayList<>();

        try{
            Connection conexion = DriverManager.getConnection(cadena);

            String insertar = "SELECT * FROM habitaciones;";
            PreparedStatement ps = conexion.prepareStatement(insertar);
            ResultSet data = ps.executeQuery();

            while(data.next()){

                Habitacion habitacion = new Habitacion();

                habitacion.setId_habitacion(data.getInt(1));
                habitacion.setTipo_habitacion(data.getString(2));
                habitacion.setNumero_habitacion(data.getString(3));
                habitacion.setPrecio_noche(data.getString(4));
                habitacion.setEstado(data.getString(5));



                listaHabitaciones.add(habitacion);
            }
            data.close();
            ps.close();
            conexion.close();

        }catch (Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al listar una habitacion");
            System.err.println("ERROR: " + ex.getMessage());
        }
        return listaHabitaciones;
    }

    public boolean editar (Habitacion habitacion){

        String cadena = "jdbc:sqlite:database.db";

        try{

            String insertar = "update habitaciones set tipo_habitacion=?, numero_habitacion=?, precio_noche=?, estado=? WHERE id_habitacion=?;";
            Connection conexion = DriverManager.getConnection(cadena);
            PreparedStatement ps = conexion.prepareStatement(insertar);


            ps.setString(1, habitacion.getTipo_habitacion());
            ps.setString(2, habitacion.getNumero_habitacion()); ;
            ps.setString(3, habitacion.getPrecio_noche());
            ps.setString(4, habitacion.getEstado());

            ps.setInt(5, habitacion.getId_habitacion());

            ps.executeUpdate();
            ps.close();
            conexion.close();

            return true;

        }catch (Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al editar un una habitacion");
            System.err.println("ERROR: " + ex.getMessage());

            return false;
        }
    }

    public boolean eliminar(int id_habitacion){

        String cadena = "jdbc:sqlite:database.db";

        try{

            String insertar = "delete from habitaciones where id_habitacion = ?;";

            Connection conexion = DriverManager.getConnection(cadena);
            PreparedStatement ps = conexion.prepareStatement(insertar);

            ps.setInt(1, id_habitacion);
            ps.executeUpdate();
            ps.close();

            return true;
        }catch (Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al eliminar una habitacion");
            System.err.println("ERROR: " + ex.getMessage());

            return false;
        }
    }

    public boolean actualizarEstado(int idHabitacion, String estado) {
        String cadena = "jdbc:sqlite:database.db";

        String insertar = "UPDATE habitaciones SET estado = ? WHERE id_habitacion = ?";
        try (Connection conexion = DriverManager.getConnection(cadena);
             PreparedStatement ps = conexion.prepareStatement(insertar)) {
            ps.setString(1, estado);
            ps.setInt(2, idHabitacion);
            return ps.executeUpdate() > 0;
        } catch (Exception ex) {
            System.err.println("Error al actualizar estado: " + ex.getMessage());
            return false;
        }
    }

    public void actualizarEstadosSegunReservas() {
        String cadena = "jdbc:sqlite:database.db";

        String sql = """
        UPDATE habitaciones
        SET estado = CASE 
            WHEN id_habitacion IN (
                SELECT id_habitacion 
                FROM reservaciones 
                WHERE DATE('now') BETWEEN fecha_entrada AND fecha_salida
            ) THEN 'Reservada'
            ELSE 'Disponible'
        END
        WHERE estado NOT IN ('Fuera de servicio', 'Reservada')  -- Respetar estados manuales
        """;

        try (Connection conexion = DriverManager.getConnection(cadena);
             PreparedStatement ps = conexion.prepareStatement(sql)) {
            ps.executeUpdate();
        } catch (Exception ex) {
            System.err.println("Error al actualizar estados: " + ex.getMessage());
        }
    }

    public List<Habitacion> listarDisponibles() {
        String cadena = "jdbc:sqlite:database.db";
        List<Habitacion> listaHabitaciones = new ArrayList<>();


        try {
            Connection conexion = DriverManager.getConnection(cadena);
            // Cambio importante: usar parámetro en lugar de string directo
            String sql = "SELECT * FROM habitaciones WHERE estado <> ?";
            PreparedStatement ps = conexion.prepareStatement(sql);
            ps.setString(1, "Fuera de servicio"); // Filtrar explícitamente

            ResultSet data = ps.executeQuery();

            while (data.next()) {
                Habitacion habitacion = new Habitacion();
                habitacion.setId_habitacion(data.getInt("id_habitacion"));
                habitacion.setTipo_habitacion(data.getString("tipo_habitacion"));
                habitacion.setNumero_habitacion(data.getString("numero_habitacion"));
                habitacion.setPrecio_noche(data.getString("precio_noche"));
                habitacion.setEstado(data.getString("estado"));
                listaHabitaciones.add(habitacion);
            }
            data.close();
            ps.close();
            conexion.close();
        } catch (Exception ex) {
            System.err.println("Error al listar habitaciones disponibles: " + ex.getMessage());
        }
        return listaHabitaciones;

    }

}
