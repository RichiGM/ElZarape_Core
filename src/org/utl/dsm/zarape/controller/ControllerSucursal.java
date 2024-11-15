package org.utl.dsm.zarape.controller;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.sql.SQLException;
import org.utl.dsm.zarape.model.Sucursal;
import org.utl.dsm.zarape.bd.ConexionMySql;


/**
 *
 * @author Ricardo
 */
public class ControllerSucursal {
    public List<Sucursal> getAll() throws SQLException{
        List<Sucursal> sucursales = new ArrayList<>();
        String query = "SELECT * FROM vw_SucursalInfo";
        try {
            ConexionMySql connMysql = new ConexionMySql();
            Connection conn = connMysql.open();
            PreparedStatement pstm = conn.prepareStatement(query);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
    Sucursal sucursal = new Sucursal();
    // Asignación de valores desde el ResultSet
    sucursal.setIdSucursal(rs.getInt("idSucursal"));
    sucursal.setNombreSucursal(rs.getString("nombreSucursal"));
    sucursal.setLatitud(rs.getString("latitud"));
    sucursal.setLongitud(rs.getString("longitud"));
    sucursal.setFoto(rs.getString("foto"));
    sucursal.setUrlWeb(rs.getString("urlWeb"));
    sucursal.setHorarios(rs.getString("horarios"));
    sucursal.setCalle(rs.getString("calle"));
    sucursal.setNumCalle(rs.getString("numCalle"));
    sucursal.setColonia(rs.getString("colonia"));
    sucursal.setIdCiudad(rs.getInt("idCiudad"));
    sucursal.setEstatus(rs.getInt("sucursalActivo")); // Asegúrate de que esto sea correcto
    sucursales.add(sucursal);
}

            rs.close();
            pstm.close();
            connMysql.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sucursales;
    }
    
}