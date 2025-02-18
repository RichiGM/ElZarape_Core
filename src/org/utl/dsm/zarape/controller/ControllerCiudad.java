/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.dsm.zarape.controller;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.utl.dsm.zarape.bd.ConexionMySql;
import org.utl.dsm.zarape.model.Ciudad;

/**
 *
 * @author Ricardo
 */
public class ControllerCiudad {

        public List<Ciudad> getCiudadesPorEstado(int idEstado) {
    List<Ciudad> ciudades = new ArrayList<>();
    String query = "SELECT * FROM ciudad WHERE idEstado = ?";

    try (Connection conn = new ConexionMySql().open();
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setInt(1, idEstado);
        try (ResultSet rs = ps.executeQuery()) {
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
        
         public List<Ciudad> getAllCiudadesConEstados() {
    List<Ciudad> ciudades = new ArrayList<>();
    String query = """
            SELECT c.idCiudad, c.nombre AS ciudadNombre, e.idEstado, e.nombre AS estadoNombre
            FROM ciudad c
            INNER JOIN estado e ON c.idEstado = e.idEstado
        """;

    try (Connection conn = new ConexionMySql().open();
         PreparedStatement ps = conn.prepareStatement(query);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            Ciudad ciudad = new Ciudad();
            ciudad.setIdCiudad(rs.getInt("idCiudad"));
            ciudad.setNombre(rs.getString("ciudadNombre")); // Alias correcto
            ciudad.setIdEstado(rs.getInt("idEstado"));
            ciudad.setNombreEstado(rs.getString("estadoNombre")); // Alias correcto

            ciudades.add(ciudad);
        }
    } catch (SQLException e) {
        System.err.println("Error en getAllCiudadesConEstados:");
        e.printStackTrace(); // Logs para identificar el error exacto
        throw new RuntimeException("Error al obtener las ciudades con estados: " + e.getMessage(), e);
    }

    return ciudades;
}


    


           
    }
