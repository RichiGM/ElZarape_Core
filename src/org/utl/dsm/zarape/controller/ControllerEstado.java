package org.utl.dsm.zarape.controller;

import org.utl.dsm.zarape.bd.ConexionMySql;
import org.utl.dsm.zarape.model.Estado;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerEstado {

    public List<Estado> getAllEstados() {
        List<Estado> estados = new ArrayList<>();
        String query = "SELECT * FROM estado";
        ConexionMySql conexionMySql = new ConexionMySql();

        try ( Connection conn = conexionMySql.open();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Estado estado = new Estado(rs.getInt("idEstado"), rs.getString("nombre"));
                estados.add(estado);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener los estados: " + e.getMessage(), e);
        }
        return estados;
    }

}
