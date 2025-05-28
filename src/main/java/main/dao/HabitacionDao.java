package main.dao;

import main.modelo.Cliente;
import main.modelo.Habitacion;
import main.modelo.Reservacion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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

    public List<Habitacion> listarDisponible(){

        String cadena = "jdbc:sqlite:database.db";
        List<Habitacion> listaHabitacionesDisponible = new ArrayList<>();

        try{
            Connection conexion = DriverManager.getConnection(cadena);

            String insertar = "SELECT * FROM habitaciones WHERE estado = 'Disponible';";
            PreparedStatement ps = conexion.prepareStatement(insertar);
            ResultSet data = ps.executeQuery();

            while(data.next()){

                Habitacion habitacion = new Habitacion();

                habitacion.setId_habitacion(data.getInt(1));
                habitacion.setTipo_habitacion(data.getString(2));
                habitacion.setNumero_habitacion(data.getString(3));
                habitacion.setPrecio_noche(data.getString(4));
                habitacion.setEstado(data.getString(5));



                listaHabitacionesDisponible.add(habitacion);
            }
            data.close();
            ps.close();
            conexion.close();

        }catch (Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al listar una habitacion");
            System.err.println("ERROR: " + ex.getMessage());
        }
        return listaHabitacionesDisponible;
    }



}
