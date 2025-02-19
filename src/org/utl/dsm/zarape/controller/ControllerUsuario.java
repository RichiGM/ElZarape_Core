package org.utl.dsm.zarape.controller;

import org.utl.dsm.zarape.bd.ConexionMySql;
import org.utl.dsm.zarape.model.*;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import org.apache.commons.codec.digest.DigestUtils;

public class ControllerUsuario {

    // Obtener todos los usuarios
    public List<Object> getAllUsuarios() {
    List<Object> usuarios = new ArrayList<>();
    String query = "SELECT * FROM vw_UsuarioInfo";

    ConexionMySql conexionMySql = new ConexionMySql();
    Connection conn = null;

    try {
        conn = conexionMySql.open();
        System.out.println("Conexión establecida con éxito.");
        
        PreparedStatement ps = conn.prepareStatement(query);
        ResultSet rs = ps.executeQuery();
        System.out.println("Consulta ejecutada correctamente.");

        while (rs.next()) {
            System.out.println("Procesando registro...");
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
        System.err.println("Error durante la ejecución de la consulta: " + e.getMessage());
        throw new RuntimeException("Error al obtener los usuarios: " + e.getMessage(), e);
    } finally {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Conexión cerrada correctamente.");
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
        try ( Connection conn = conexionMySql.open();  CallableStatement cs = conn.prepareCall(query)) {

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

    // Modificar usuario (sin modificar contraseña)
public void updateSinContrasenia(Usuario usuario, Persona persona, String tipoEntidad, Integer idSucursal) {
    System.out.println("Datos recibidos en el controlador (Sin Contraseña):");
    System.out.println("Usuario: " + usuario);
    System.out.println("Persona: " + persona);
    System.out.println("Tipo Entidad: " + tipoEntidad);
    System.out.println("ID Sucursal: " + idSucursal);

    String query = "{CALL SP_UpdateUsuarioSinContrasenia(?, ?, ?, ?, ?, ?, ?, ?, ?)}";
    ConexionMySql conexionMySql = new ConexionMySql();

    try (Connection conn = conexionMySql.open();
         CallableStatement stmt = conn.prepareCall(query)) {

        stmt.setInt(1, usuario.getIdUsuario());
        stmt.setString(2, usuario.getNombre());
        stmt.setInt(3, persona.getIdPersona());
        stmt.setString(4, persona.getNombre());
        stmt.setString(5, persona.getApellidos());
        stmt.setString(6, persona.getTelefono());
        stmt.setInt(7, persona.getIdCiudad());
        stmt.setString(8, tipoEntidad);

        if (idSucursal != null) {
            stmt.setInt(9, idSucursal);
        } else {
            stmt.setNull(9, Types.INTEGER);
        }

        System.out.println("Ejecutando el SP sin modificar contraseña...");
        stmt.executeUpdate();
        System.out.println("SP ejecutado correctamente.");
    } catch (SQLException ex) {
        System.out.println("Error en el SP (Sin Contraseña):");
        ex.printStackTrace();
        throw new RuntimeException("Error al modificar el usuario sin contraseña: " + ex.getMessage(), ex);
    }
}

// Modificar solo la contraseña del usuario
public void updateContrasenia(int idUsuario, String nuevaContrasenia) {
    System.out.println("Datos recibidos en el controlador (Solo Contraseña):");
    System.out.println("ID Usuario: " + idUsuario);
    System.out.println("Nueva Contraseña (Hash): " + nuevaContrasenia);

    String query = "{CALL SP_UpdateContrasenia(?, ?)}";
    ConexionMySql conexionMySql = new ConexionMySql();

    try (Connection conn = conexionMySql.open();
         CallableStatement stmt = conn.prepareCall(query)) {

        stmt.setInt(1, idUsuario);
        stmt.setString(2, nuevaContrasenia);

        System.out.println("Ejecutando el SP para modificar solo la contraseña...");
        stmt.executeUpdate();
        System.out.println("SP ejecutado correctamente.");
    } catch (SQLException ex) {
        System.out.println("Error en el SP (Solo Contraseña):");
        ex.printStackTrace();
        throw new RuntimeException("Error al modificar la contraseña: " + ex.getMessage(), ex);
    }
}


    // Cambiar estatus de un usuario
    public void cambiarEstatus(int idUsuario) {
        String query = "{CALL SP_DeleteUsuario(?)}";
        ConexionMySql conexionMySql = new ConexionMySql();
        try ( Connection conn = conexionMySql.open();  CallableStatement cs = conn.prepareCall(query)) {

            cs.setInt(1, idUsuario);

            cs.execute();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cambiar el estatus del usuario: " + e.getMessage(), e);
        }
    }

    public boolean validateUser(String username, String password) {
        String query = "SELECT COUNT(*) FROM usuario WHERE nombre = ? AND contrasenia = ? AND activo = 1";
        ConexionMySql conexionMySql = new ConexionMySql();

        try ( Connection conn = conexionMySql.open();  PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0; // Si el resultado es mayor a 0, el usuario existe
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al validar el usuario: " + e.getMessage());
        }

        return false;

    }

    public List<Object> searchUsuarios(String nombreUsuario) {
        List<Object> usuarios = new ArrayList<>();
        String query = "{CALL SP_SearchUsuarios(?)}";

        ConexionMySql conexionMySql = new ConexionMySql();
        Connection conn = null;

        try {
            conn = conexionMySql.open();
            CallableStatement cs = conn.prepareCall(query);
            cs.setString(1, nombreUsuario);
            ResultSet rs = cs.executeQuery();

            while (rs.next()) {
                // Reutiliza el código del método `getAllUsuarios` para mapear los resultados
                int idUsuario = rs.getInt("idUsuario");
                String nombreUsuarioResult = rs.getString("nombreUsuario");
                String contrasenia = rs.getString("contrasenia");
                int usuarioActivo = rs.getInt("usuarioActivo");

                Usuario usuario = new Usuario(idUsuario, nombreUsuarioResult, contrasenia, usuarioActivo);

                int idPersona = rs.getInt("idPersona");
                String nombrePersona = rs.getString("nombrePersona");
                String apellidos = rs.getString("apellidos");
                String telefono = rs.getString("telefono");
                int idCiudad = rs.getInt("idCiudad");
                String nombreCiudad = rs.getString("ciudad");

                Persona persona = new Persona(idPersona, nombrePersona, apellidos, telefono, idCiudad, nombreCiudad);

                String tipoEntidad = rs.getString("tipoEntidad");

                if ("empleado".equalsIgnoreCase(tipoEntidad)) {
                    int idEmpleado = rs.getInt("idEmpleado");
                    int idSucursal = rs.getInt("idSucursal");
                    String nombreSucursal = rs.getString("nombreSucursal");

                    Empleado empleado = new Empleado(idEmpleado, usuario, persona, idSucursal, nombreSucursal);
                    usuarios.add(empleado);

                } else if ("cliente".equalsIgnoreCase(tipoEntidad)) {
                    int idCliente = rs.getInt("idCliente");

                    Cliente cliente = new Cliente(idCliente, usuario, persona);
                    usuarios.add(cliente);
                }
            }

            rs.close();
            cs.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar los usuarios: " + e.getMessage(), e);
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
    
   public String checkUsers(String nombreU) throws Exception {
    // Consulta SQL
    String sql = "SELECT * FROM usuario WHERE nombre = '" + nombreU + "';";
    System.out.println("Ejecutando consulta SQL: " + sql); // Log 1: Ver la consulta ejecutada
    

    ConexionMySql connMySQL = new ConexionMySql();
    Connection conn = connMySQL.open();
    PreparedStatement pstmt = conn.prepareStatement(sql);
    ResultSet rs = pstmt.executeQuery();

    String name = null;
    String tok = null; // Token almacenado en la BD
    String tokenizer = null; // Token generado
    Date myDate = new Date(); // Fecha actual
    String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(myDate);
    String sql2 = "";

    while (rs.next()) {                  
        name = rs.getString("nombre");
        tok = rs.getString("lastToken"); // Obtener el lastToken desde la BD

        // Log 2: Mostrar valores obtenidos
        System.out.println("Nombre usuario obtenido: " + name);
        System.out.println("Token obtenido de BD: " + tok);

        // Verificar si el token no es null antes de hacer trim()
        if (tok != null) {
            tok = tok.trim();
        } else {
            tok = "";  // Evitar NullPointerException
        }

        if (!tok.isEmpty()) { // Si ya tiene un hash, solo actualiza la fecha
            tokenizer = tok;
            sql2 = "UPDATE usuario SET dateLastToken = '" + fecha + "' WHERE nombre = '" + name + "';";
            System.out.println("Token ya existe, solo actualizando fecha.");
        } else { // Si no tiene token, generarlo
            String token = "ZARAPE" + "." + name + "." + fecha;
            tokenizer = DigestUtils.md5Hex(token); // Generar token MD5
            sql2 = "UPDATE usuario SET lastToken= '" + tokenizer + "', dateLastToken = '" + fecha + "' WHERE nombre = '" + name + "';";
            System.out.println("Generando nuevo token: " + tokenizer);
        } 

        // Log 3: Verificar la consulta de actualización
        System.out.println("Ejecutando SQL de actualización: " + sql2);

        // Ejecutar la actualización
        Connection connect = connMySQL.open();                    
        PreparedStatement ps = connect.prepareStatement(sql2);                     
        ps.executeUpdate(); 

        // Log 4: Retorno del token
        System.out.println("Token retornado: " + tokenizer);
        return tokenizer;
    } 

    System.out.println("No se encontró el usuario: " + nombreU);
    return name;
}
   
   public void logoutUser (String nombreUsuario) {
    String query = "UPDATE usuario SET lastToken = NULL WHERE nombre = ?";
    ConexionMySql conexionMySql = new ConexionMySql();
    
    try (Connection conn = conexionMySql.open(); PreparedStatement ps = conn.prepareStatement(query)) {
        ps.setString(1, nombreUsuario);
        ps.executeUpdate();
    } catch (SQLException e) {
        e.printStackTrace();
        throw new RuntimeException("Error al hacer logout del usuario: " + e.getMessage(), e);
    }
}

}

