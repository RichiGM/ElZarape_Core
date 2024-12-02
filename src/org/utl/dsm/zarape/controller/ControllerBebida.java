package org.utl.dsm.zarape.controller;

import org.utl.dsm.zarape.model.Bebida;
import org.utl.dsm.zarape.bd.ConexionMySql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.utl.dsm.zarape.model.Categoria;
import org.utl.dsm.zarape.model.Producto;

/**
 * Controlador para gestionar las bebidas y sus productos asociados.
 */
public class ControllerBebida {

    // Método para insertar una bebida
    public void insert(Bebida bebida) {
        String query = "{CALL SP_InsertProducto(?, ?, ?, ?, ?, ?)}";
        try (Connection conn = new ConexionMySql().open();
             CallableStatement cs = conn.prepareCall(query)) {

            // Asignar parámetros del procedimiento
            cs.setString(1, bebida.getProducto().getNombre());
            cs.setString(2, bebida.getProducto().getDescripcion());
            cs.setString(3, bebida.getProducto().getFoto());
            cs.setDouble(4, bebida.getProducto().getPrecio());
            cs.setInt(5, bebida.getProducto().getCategoria().getIdCategoria());
            cs.setString(6, "bebida");

            // Ejecutar el procedimiento
            cs.execute();
            System.out.println("La bebida fue insertada exitosamente.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar la bebida: " + e.getMessage(), e);
        }
    }

    // Método para obtener todas las bebidas
    public List<Bebida> getAllBebidas() {
        String query = "SELECT * FROM vw_BebidasInfo"; // Consulta directa a la vista
        List<Bebida> bebidas = new ArrayList<>();

        try (Connection conn = new ConexionMySql().open();
             PreparedStatement ps = conn.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                // Mapeo de la categoría
                Categoria categoria = new Categoria(
                        rs.getInt("idCategoria"),
                        rs.getString("categoriaDescripcion"),
                        rs.getString("categoriaTipo"),
                        1
                );

                // Mapeo del producto
                Producto producto = new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getString("descripcionProducto"),
                        rs.getString("fotoProducto"),
                        rs.getDouble("precioProducto"),
                        categoria,
                        rs.getInt("productoActivo")
                );

                // Mapeo de la bebida
                Bebida bebida = new Bebida(rs.getInt("idBebida"), producto);
                bebidas.add(bebida);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al obtener las bebidas: " + e.getMessage(), e);
        }

        return bebidas;
    }

    // Método para actualizar una bebida
    public void updateBebida(Producto producto) {
        String query = "{CALL SP_UpdateProducto(?, ?, ?, ?, ?, ?)}";

        try (Connection conn = new ConexionMySql().open();
             CallableStatement cs = conn.prepareCall(query)) {

            // Asignar parámetros al procedimiento almacenado
            cs.setInt(1, producto.getIdProducto());
            cs.setString(2, producto.getNombre());
            cs.setString(3, producto.getDescripcion());
            cs.setString(4, producto.getFoto());
            cs.setDouble(5, producto.getPrecio());
            cs.setInt(6, producto.getCategoria().getIdCategoria());

            // Ejecutar el procedimiento
            cs.execute();
            System.out.println("El producto asociado a la bebida fue actualizado exitosamente.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar la bebida: " + e.getMessage(), e);
        }
    }

    // Método para eliminar (marcar como inactivo) una bebida
    public void deleteProducto(int idProducto) {
        String query = "{CALL SP_DeleteProducto(?)}";

        try (Connection conn = new ConexionMySql().open();
             CallableStatement cs = conn.prepareCall(query)) {

            // Asignar el parámetro al procedimiento almacenado
            cs.setInt(1, idProducto);

            // Ejecutar el procedimiento
            cs.execute();
            System.out.println("El producto fue marcado como inactivo exitosamente.");

        } catch (SQLException e) {
            throw new RuntimeException("Error al eliminar (marcar como inactivo) la bebida: " + e.getMessage(), e);
        }
    }

    // Método para buscar bebidas
public List<Bebida> searchBebidas(String filtro) {
    String query = "{CALL SP_SearchBebidas(?)}"; // Solo un parámetro de filtro
    List<Bebida> bebidas = new ArrayList<>();

    try (Connection conn = new ConexionMySql().open();
         CallableStatement cs = conn.prepareCall(query)) {

        // Establecer el filtro
       cs.setString(1, filtro != null && !filtro.isEmpty() ? filtro : "");

        // Ejecutar y procesar resultados
        try (ResultSet rs = cs.executeQuery()) {
            while (rs.next()) {
                // Mapeo de la categoría
                Categoria categoria = new Categoria(
                        rs.getInt("idCategoria"),
                        rs.getString("categoriaDescripcion"),
                        rs.getString("categoriaTipo"),
                        1 // Activo, puedes ajustar según tu lógica
                );

                // Mapeo del producto
                Producto producto = new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getString("descripcionProducto"),
                        rs.getString("fotoProducto"),
                        rs.getDouble("precioProducto"),
                        categoria,
                        rs.getInt("productoActivo")
                );

                // Mapeo de la bebida
                Bebida bebida = new Bebida(rs.getInt("idBebida"), producto);
                bebidas.add(bebida);
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error al buscar bebidas: " + e.getMessage(), e);
    }

    return bebidas;
}
    
    // Cambiar estatus de una bebida
public void cambiarEstatus(int idProducto) {
    String query = "{CALL SP_DeleteProducto(?)}";
    try (Connection conn = new ConexionMySql().open();
         CallableStatement cs = conn.prepareCall(query)) {

        cs.setInt(1, idProducto);
        cs.execute();
        System.out.println("Estatus del producto actualizado correctamente.");
    } catch (SQLException e) {
        throw new RuntimeException("Error al cambiar el estatus del producto: " + e.getMessage(), e);
    }
}


}
