package main.dao;

import main.modelo.Cliente;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ClienteDao {

    public boolean registrar(Cliente cliente){

        String cadena = "jdbc:sqlite:database.db";

        try {
            Connection conexion = DriverManager.getConnection(cadena);

            String insertar = "INSERT INTO clientes(nombre, apellido_p, apellido_m, telefono, correo, identificacion) VALUES ( ?, ?, ?, ?, ?, ?)";
            PreparedStatement ps = conexion.prepareStatement(insertar);

            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido_p());
            ps.setString(3, cliente.getApellido_m());
            ps.setString(4, cliente.getNumero());
            ps.setString(5, cliente.getCorreo());
            ps.setString(6, cliente.getIdentificacion());


            ps.executeUpdate();
            ps.close();
            conexion.close();

            return true;

        } catch(Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al registrar la tarea");
            System.err.println("ERROR: " + ex.getMessage());
        }
        return true;
    }

    public List<Cliente> listar(){

        String cadena = "jdbc:sqlite:database.db";
        List<Cliente> listaClientes = new ArrayList<>();

        try{
            Connection conexion = DriverManager.getConnection(cadena);

            String insertar = "SELECT * FROM clientes;";
            PreparedStatement ps = conexion.prepareStatement(insertar);
            ResultSet data = ps.executeQuery();

            while(data.next()){

                Cliente cliente = new Cliente();

                cliente.setId_cliente(data.getInt(1));
                cliente.setNombre(data.getString(2));
                cliente.setApellido_p(data.getString(3));
                cliente.setApellido_m(data.getString(4));
                cliente.setNumero(data.getString(5));
                cliente.setCorreo(data.getString(6));
                cliente.setIdentificacion(data.getString(7));


                listaClientes.add(cliente);
            }
            data.close();
            ps.close();
            conexion.close();

        }catch (Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al listar un cliente");
            System.err.println("ERROR: " + ex.getMessage());
        }
        return listaClientes;
    }

    public boolean editar (Cliente cliente){

        String cadena = "jdbc:sqlite:database.db";

        try{

            String insertar = "update clientes set nombre=?, apellido_p=?, apellido_m=?, telefono=?, correo=?, identificacion=? WHERE id_cliente=?;";
            Connection conexion = DriverManager.getConnection(cadena);
            PreparedStatement ps = conexion.prepareStatement(insertar);


            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getApellido_p()) ;
            ps.setString(3, cliente.getApellido_m());
            ps.setString(4, cliente.getNumero());
            ps.setString(5, cliente.getCorreo());
            ps.setString(6, cliente.getIdentificacion());

            ps.setInt(7, cliente.getId_cliente());

            ps.executeUpdate();
            ps.close();
            conexion.close();

            return true;

        }catch (Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al editar un cliente");
            System.err.println("ERROR: " + ex.getMessage());

            return false;
        }
    }

    public boolean eliminar(int id_cliente){

        String cadena = "jdbc:sqlite:database.db";

        try{

            String insertar = "delete from clientes where id_cliente = ?;";

            Connection conexion = DriverManager.getConnection(cadena);
            PreparedStatement ps = conexion.prepareStatement(insertar);

            ps.setInt(1, id_cliente);
            ps.executeUpdate();
            ps.close();

            return true;
        }catch (Exception ex){
            System.err.println(ex);
            System.err.println("ocurrio un erro al editar un cliente");
            System.err.println("ERROR: " + ex.getMessage());

            return false;
        }
    }



}
