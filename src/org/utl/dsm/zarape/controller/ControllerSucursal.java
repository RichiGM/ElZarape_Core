package org.utl.dsm.zarape.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.utl.dsm.zarape.bd.ConexionMySql;
import org.utl.dsm.zarape.model.Ciudad;
import org.utl.dsm.zarape.model.Estado;
import org.utl.dsm.zarape.model.Sucursal;

public class ControllerSucursal {

    // Obtener todas las sucursales
    public List<Sucursal> getAllSucursales() {
        List<Sucursal> sucursales = new ArrayList<>();
        String query = "SELECT * FROM vw_SucursalInfo";

        ConexionMySql conexionMySql = new ConexionMySql();

        try ( Connection conn = conexionMySql.open();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setIdSucursal(rs.getInt("idSucursal"));
                sucursal.setNombre(rs.getString("nombreSucursal"));
                sucursal.setLatitud(rs.getString("latitud"));
                sucursal.setLongitud(rs.getString("longitud"));
                sucursal.setFoto(rs.getString("foto"));
                sucursal.setUrlWeb(rs.getString("urlWeb"));
                sucursal.setHorarios(rs.getString("horarios"));
                sucursal.setCalle(rs.getString("calle"));
                sucursal.setNumCalle(rs.getString("numCalle"));
                sucursal.setColonia(rs.getString("colonia"));

                // Manejo de Ciudad
                if (rs.getInt("idCiudad") > 0) {
                    Ciudad ciudad = new Ciudad();
                    ciudad.setIdCiudad(rs.getInt("idCiudad"));
                    ciudad.setNombre(rs.getString("ciudad"));
                    ciudad.setIdEstado(rs.getInt("idEstado"));
                    sucursal.setCiudad(ciudad);
                } else {
                    sucursal.setCiudad(null);
                }

                // Manejo de Estado
                if (rs.getInt("idEstado") > 0) {
                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("idEstado"));
                    estado.setNombre(rs.getString("estado"));
                    sucursal.setEstado(estado);
                } else {
                    sucursal.setEstado(null);
                }

                sucursal.setActivo(rs.getInt("sucursalActivo"));
                sucursales.add(sucursal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener las sucursales: " + e.getMessage(), e);
        }
        return sucursales;
    }

    public void insertSucursal(Sucursal sucursal) {
        String query = "CALL SP_InsertSucursal(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try ( Connection conn = new ConexionMySql().open();  PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setString(1, sucursal.getNombre());
            ps.setString(2, sucursal.getLatitud());
            ps.setString(3, sucursal.getLongitud());
            ps.setString(4, sucursal.getFoto()); // Foto en Base64
            ps.setString(5, sucursal.getUrlWeb());
            ps.setString(6, sucursal.getHorarios());
            ps.setString(7, sucursal.getCalle());
            ps.setString(8, sucursal.getNumCalle());
            ps.setString(9, sucursal.getColonia());
            ps.setInt(10, sucursal.getCiudad().getIdCiudad()); // Valida que idCiudad no sea null

            System.out.println("Datos enviados al SP_InsertSucursal: " + sucursal);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el error en los logs
            throw new RuntimeException("Error al insertar la sucursal: " + e.getMessage(), e);
        }
    }

    // Modificar una sucursal existente
    public void updateSucursal(Sucursal sucursal) {
        String query = "CALL SP_UpdateSucursal(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try ( Connection conn = new ConexionMySql().open();  PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, sucursal.getIdSucursal());
            ps.setString(2, sucursal.getNombre());
            ps.setString(3, sucursal.getLatitud());
            ps.setString(4, sucursal.getLongitud());
            ps.setString(5, sucursal.getFoto());
            ps.setString(6, sucursal.getUrlWeb());
            ps.setString(7, sucursal.getHorarios());
            ps.setString(8, sucursal.getCalle());
            ps.setString(9, sucursal.getNumCalle());
            ps.setString(10, sucursal.getColonia());
            ps.setInt(11, sucursal.getCiudad().getIdCiudad()); // Extraer idCiudad del objeto Ciudad

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al modificar la sucursal: " + e.getMessage(), e);
        }
    }

    // Cambiar estatus (eliminar/inactivar) una sucursal
    public void deleteSucursal(int idSucursal) {
        String query = "CALL SP_DeleteSucursal(?)";

        try ( Connection conn = new ConexionMySql().open();  PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idSucursal); // Pasar el ID de la sucursal
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cambiar el estatus de la sucursal: " + e.getMessage(), e);
        }
    }

    public List<Estado> getAllEstados() {
        List<Estado> estados = new ArrayList<>();
        String query = "SELECT * FROM estado";

        try ( Connection conn = new ConexionMySql().open();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estado estado = new Estado();
                estado.setIdEstado(rs.getInt("idEstado"));
                estado.setNombre(rs.getString("nombre"));
                estados.add(estado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener los estados: " + e.getMessage(), e);
        }

        return estados;
    }

    public List<Ciudad> getCiudadesByEstado(int idEstado) {
        List<Ciudad> ciudades = new ArrayList<>();
        String query = "SELECT * FROM ciudad WHERE idEstado = ?";

        try ( Connection conn = new ConexionMySql().open();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idEstado);
            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ciudad ciudad = new Ciudad();
                    ciudad.setIdCiudad(rs.getInt("idCiudad"));
                    ciudad.setNombre(rs.getString("nombre"));
                    ciudad.setIdEstado(rs.getInt("idEstado"));
                    ciudades.add(ciudad);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener las ciudades: " + e.getMessage(), e);
        }

        return ciudades;
    }

    public List<Sucursal> searchSucursales(String filtro) {
        List<Sucursal> sucursales = new ArrayList<>();
        String query = "CALL SP_SearchSucursales(?)";

        try ( Connection conn = new ConexionMySql().open();  PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, filtro);

            try ( ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Sucursal sucursal = new Sucursal();
                    sucursal.setIdSucursal(rs.getInt("idSucursal"));
                    sucursal.setNombre(rs.getString("nombreSucursal"));
                    sucursal.setLatitud(rs.getString("latitud"));
                    sucursal.setLongitud(rs.getString("longitud"));
                    sucursal.setFoto(rs.getString("foto"));
                    sucursal.setUrlWeb(rs.getString("urlWeb"));
                    sucursal.setHorarios(rs.getString("horarios"));
                    sucursal.setCalle(rs.getString("calle"));
                    sucursal.setNumCalle(rs.getString("numCalle"));
                    sucursal.setColonia(rs.getString("colonia"));

                    // Ciudad
                    Ciudad ciudad = new Ciudad();
                    ciudad.setIdCiudad(rs.getInt("idCiudad"));
                    ciudad.setNombre(rs.getString("ciudad"));
                    sucursal.setCiudad(ciudad);

                    // Estado
                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("idEstado"));
                    estado.setNombre(rs.getString("estado"));
                    sucursal.setEstado(estado);

                    // Estatus activo/inactivo
                    sucursal.setActivo(rs.getInt("sucursalActivo"));

                    sucursales.add(sucursal);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al buscar sucursales: " + e.getMessage(), e);
        }
        return sucursales;
    }
    
    public List<Sucursal> getAllSucursalesCliente() {
        List<Sucursal> sucursales = new ArrayList<>();
        String query = "SELECT * FROM vw_SucursalInfo WHERE sucursalActivo = 1";

        ConexionMySql conexionMySql = new ConexionMySql();

        try ( Connection conn = conexionMySql.open();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sucursal sucursal = new Sucursal();
                sucursal.setIdSucursal(rs.getInt("idSucursal"));
                sucursal.setNombre(rs.getString("nombreSucursal"));
                sucursal.setLatitud(rs.getString("latitud"));
                sucursal.setLongitud(rs.getString("longitud"));
                sucursal.setFoto(rs.getString("foto"));
                sucursal.setUrlWeb(rs.getString("urlWeb"));
                sucursal.setHorarios(rs.getString("horarios"));
                sucursal.setCalle(rs.getString("calle"));
                sucursal.setNumCalle(rs.getString("numCalle"));
                sucursal.setColonia(rs.getString("colonia"));

                // Manejo de Ciudad
                if (rs.getInt("idCiudad") > 0) {
                    Ciudad ciudad = new Ciudad();
                    ciudad.setIdCiudad(rs.getInt("idCiudad"));
                    ciudad.setNombre(rs.getString("ciudad"));
                    ciudad.setIdEstado(rs.getInt("idEstado"));
                    sucursal.setCiudad(ciudad);
                } else {
                    sucursal.setCiudad(null);
                }

                // Manejo de Estado
                if (rs.getInt("idEstado") > 0) {
                    Estado estado = new Estado();
                    estado.setIdEstado(rs.getInt("idEstado"));
                    estado.setNombre(rs.getString("estado"));
                    sucursal.setEstado(estado);
                } else {
                    sucursal.setEstado(null);
                }

                sucursal.setActivo(rs.getInt("sucursalActivo"));
                sucursales.add(sucursal);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener las sucursales: " + e.getMessage(), e);
        }
        return sucursales;
    }
}
