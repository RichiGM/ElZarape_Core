package org.utl.dsm.zarape.controller;

import org.utl.dsm.zarape.bd.ConexionMySql;
import org.utl.dsm.zarape.model.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerUsuario {

    // Obtener todos los usuarios
    public List<Object> getAllUsuarios() {
        List<Object> usuarios = new ArrayList<>();
        String query = "SELECT * FROM vw_UsuarioInfo";

        ConexionMySql conexionMySql = new ConexionMySql();
        Connection conn = null;

        try {
            conn = conexionMySql.open();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Mapeo de Usuario
                int idUsuario = rs.getInt("idUsuario");
                String nombreUsuario = rs.getString("nombreUsuario");
                String contrasenia = rs.getString("contrasenia");
                int usuarioActivo = rs.getInt("usuarioActivo");

                Usuario usuario = new Usuario(idUsuario, nombreUsuario, contrasenia, usuarioActivo);

                // Mapeo de Persona
                int idPersona = rs.getInt("idPersona");
                String nombrePersona = rs.getString("nombrePersona");
                String apellidos = rs.getString("apellidos");
                String telefono = rs.getString("telefono");
                int idCiudad = rs.getInt("idCiudad");
                String nombreCiudad = rs.getString("ciudad");

                Persona persona = new Persona(idPersona, nombrePersona, apellidos, telefono, idCiudad, nombreCiudad);

                // Determinar tipo de entidad
                String tipoEntidad = rs.getString("tipoEntidad");

                if ("empleado".equalsIgnoreCase(tipoEntidad)) {
                    // Mapeo de Empleado
                    int idEmpleado = rs.getInt("idEmpleado");
                    int idSucursal = rs.getInt("idSucursal");
                    String nombreSucursal = rs.getString("nombreSucursal");

                    Empleado empleado = new Empleado(idEmpleado, usuario, persona, idSucursal, nombreSucursal);
                    usuarios.add(empleado);

                } else if ("cliente".equalsIgnoreCase(tipoEntidad)) {
                    // Mapeo de Cliente
                    int idCliente = rs.getInt("idCliente");

                    Cliente cliente = new Cliente(idCliente, usuario, persona);
                    usuarios.add(cliente);
                }
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener los usuarios: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return usuarios;
    }

    // Insertar un usuario
    public void insert(Usuario usuario, Persona persona, String tipoEntidad, Integer idSucursal) {
        String query = "{CALL SP_InsertUsuario(?, ?, ?, ?, ?, ?, ?, ?)}";
        ConexionMySql conexionMySql = new ConexionMySql();
        try (Connection conn = conexionMySql.open();
             CallableStatement cs = conn.prepareCall(query)) {

            cs.setString(1, usuario.getNombre());
            cs.setString(2, usuario.getContrasenia());
            cs.setString(3, persona.getNombre());
            cs.setString(4, persona.getApellidos());
            cs.setString(5, persona.getTelefono());
            cs.setInt(6, persona.getIdCiudad());
            cs.setString(7, tipoEntidad);
            if ("empleado".equalsIgnoreCase(tipoEntidad) && idSucursal != null) {
                cs.setInt(8, idSucursal);
            } else {
                cs.setNull(8, Types.INTEGER);
            }

            cs.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar el usuario: " + e.getMessage(), e);
        }
    }

public void update(Usuario usuario, Persona persona, String tipoEntidad, Integer idSucursal) {
    System.out.println("Datos recibidos en el controlador:");
    System.out.println("Usuario: " + usuario);
    System.out.println("Persona: " + persona);
    System.out.println("Tipo Entidad: " + tipoEntidad);
    System.out.println("ID Sucursal: " + idSucursal);

    String query = "{CALL SP_UpdateUsuario(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    ConexionMySql conexionMySql = new ConexionMySql();

    try (Connection conn = conexionMySql.open();
         CallableStatement stmt = conn.prepareCall(query)) {

        stmt.setInt(1, usuario.getIdUsuario());
        stmt.setString(2, usuario.getNombre());
        stmt.setString(3, usuario.getContrasenia());
        stmt.setInt(4, persona.getIdPersona());
        stmt.setString(5, persona.getNombre());
        stmt.setString(6, persona.getApellidos());
        stmt.setString(7, persona.getTelefono());
        stmt.setInt(8, persona.getIdCiudad());
        stmt.setString(9, tipoEntidad);

        if (idSucursal != null) {
            stmt.setInt(10, idSucursal);
        } else {
            stmt.setNull(10, Types.INTEGER);
        }

        System.out.println("Ejecutando el SP con los datos configurados...");
        stmt.executeUpdate();
        System.out.println("SP ejecutado correctamente.");
    } catch (SQLException ex) {
        System.out.println("Error en el SP:");
        ex.printStackTrace();
        throw new RuntimeException("Error al modificar el usuario: " + ex.getMessage(), ex);
    }
}






    // Cambiar estatus de un usuario
    public void cambiarEstatus(int idUsuario) {
        String query = "{CALL SP_DeleteUsuario(?)}";
        ConexionMySql conexionMySql = new ConexionMySql();
        try (Connection conn = conexionMySql.open();
             CallableStatement cs = conn.prepareCall(query)) {

            cs.setInt(1, idUsuario);

            cs.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cambiar el estatus del usuario: " + e.getMessage(), e);
        }
    }
}
