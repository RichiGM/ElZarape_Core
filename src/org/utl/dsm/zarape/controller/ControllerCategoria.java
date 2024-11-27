package org.utl.dsm.zarape.controller;

import org.utl.dsm.zarape.model.Categoria;
import org.utl.dsm.zarape.bd.ConexionMySql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControllerCategoria {

    public void insert(Categoria categoria) {
        String query = "{CALL SP_InsertCategoria(?, ?)}";

        try ( Connection conn = new ConexionMySql().open();  CallableStatement cs = conn.prepareCall(query)) {

            cs.setString(1, categoria.getDescripcion());
            cs.setString(2, categoria.getTipo());

            cs.execute(); // Ejecuta el procedimiento
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar la categoría: " + e.getMessage(), e);
        }
    }

    // Método para obtener todas las categorías
    public List<Categoria> getAllCategorias() {
        List<Categoria> categorias = new ArrayList<>();
        String query = "SELECT * FROM vw_Categorias";

        ConexionMySql conexionMySql = new ConexionMySql();

        try ( Connection conn = conexionMySql.open();  PreparedStatement ps = conn.prepareStatement(query);  ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria(
                        rs.getInt("idCategoria"),
                        rs.getString("descripcion"),
                        rs.getString("tipo"),
                        rs.getInt("activo")
                );
                categorias.add(categoria);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las categorías: " + e.getMessage(), e);
        }

        return categorias;
    }

    // Método para actualizar una categoría
    public void updateCategoria(Categoria categoria) {
        String query = "{CALL SP_UpdateCategoria(?, ?, ?, ?)}";
        ConexionMySql conexionMySql = new ConexionMySql();

        try ( Connection conn = conexionMySql.open();  CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, categoria.getIdCategoria());
            cs.setString(2, categoria.getDescripcion());
            cs.setString(3, categoria.getTipo());
            cs.setInt(4, categoria.getActivo());
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la categoría: " + e.getMessage(), e);
        }
    }

    // Método para eliminar una categoría
    public void deleteCategoria(int idCategoria) {
        String query = "CALL SP_DeleteCategoria(?)"; // Llama a tu procedimiento almacenado

        try ( Connection conn = new ConexionMySql().open();  PreparedStatement ps = conn.prepareStatement(query)) {

            ps.setInt(1, idCategoria); // Pasa el parámetro idCategoria
            ps.executeUpdate(); // Ejecuta la actualización

        } catch (SQLException e) {
            e.printStackTrace(); // Imprime el stack trace en la consola
            throw new RuntimeException("Error al cambiar el estatus de la categoría: " + e.getMessage(), e);
        }
    }

    public List<Categoria> searchCategoria(String descripcion) {
    List<Categoria> categorias = new ArrayList<>();
    String query = "{CALL SP_SearchCategoria(?)}";

    try (Connection conn = new ConexionMySql().open();
         CallableStatement cs = conn.prepareCall(query)) {

        cs.setString(1, descripcion);

        try (ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria(
                    rs.getInt("idCategoria"),
                    rs.getString("descripcion"),
                    rs.getString("tipo"),
                    rs.getInt("activo")
                );
                categorias.add(categoria);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error al buscar categorías: " + e.getMessage(), e);
    }

    return categorias;
}
 public List<Categoria> getCategoriasByTipo(String tipo) {
    List<Categoria> categorias = new ArrayList<>();
    String query = "SELECT * FROM categoria WHERE tipo = ? AND activo = 1";

    try (Connection conn = new ConexionMySql().open();
         PreparedStatement ps = conn.prepareStatement(query)) {

        ps.setString(1, tipo);

        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Categoria categoria = new Categoria(
                    rs.getInt("idCategoria"),
                    rs.getString("descripcion"),
                    rs.getString("tipo"),
                    rs.getInt("activo")
                );
                categorias.add(categoria);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error al obtener las categorías: " + e.getMessage(), e);
    }

    return categorias;
}

}
