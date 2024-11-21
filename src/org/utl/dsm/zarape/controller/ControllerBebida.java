/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.utl.dsm.zarape.controller;


import org.utl.dsm.zarape.model.Bebida;
import org.utl.dsm.zarape.bd.ConexionMySql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.utl.dsm.zarape.model.Categoria;
import org.utl.dsm.zarape.model.Producto;

/**
 *
 * @author Lap
 */
public class ControllerBebida {
    // Método para insertar una bebida
    public void insert(Bebida bebida) {
        String query = "{CALL SP_InsertProducto(?, ?, ?, ?, ?, ?)}";
        ConexionMySql conexionMySql = new ConexionMySql();

         try ( java.sql.Connection conn = conexionMySql.open();  CallableStatement cs = conn.prepareCall(query)) {

            // Asignar parámetros del procedimiento
            cs.setString(1, bebida.getProducto().getNombre());
            cs.setString(2, bebida.getProducto().getDescripcion());
            cs.setString(3, bebida.getProducto().getFoto());
            cs.setDouble(4, bebida.getProducto().getPrecio());
            cs.setInt(5, bebida.getProducto().getCategoria().getIdCategoria());
            cs.setString(6, "bebida"); // Especificamos que es una bebida

            // Ejecutar el procedimiento
            cs.execute();
            System.out.println("La bebida fue insertada exitosamente.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar la bebida: " + e.getMessage(), e);
        }
    }
    //Metodo para ver las bebidas
    public List<Bebida> getAllBebidas() {
        List<Bebida> bebidas = new ArrayList<>();
        String query = "SELECT * FROM vw_BebidasInfo";

        ConexionMySql conexionMySql = new ConexionMySql();
        Connection conn = null;

        try {
            conn = conexionMySql.open();
            PreparedStatement ps = conn.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Mapeo de Producto
                int idProducto = rs.getInt("idProducto");
                String nombreProducto = rs.getString("nombreProducto");
                String descripcionProducto = rs.getString("descripcionProducto");
                String fotoProducto = rs.getString("fotoProducto");
                double precioProducto = rs.getDouble("precioProducto");
                int idCategoria = rs.getInt("idCategoria");
                String categoriaDescripcion = rs.getString("categoriaDescripcion");
                String categoriaTipo = rs.getString("categoriaTipo");
                int productoActivo = rs.getInt("productoActivo");

                Categoria categoria = new Categoria(idCategoria, categoriaDescripcion, categoriaTipo, 1);
                Producto producto = new Producto(idProducto, nombreProducto, descripcionProducto, fotoProducto, precioProducto, categoria, productoActivo);

                // Mapeo de Bebida
                int idBebida = rs.getInt("idBebida");
                Bebida bebida = new Bebida(idBebida, producto);

                bebidas.add(bebida);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener las bebidas: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return bebidas;
    }
    
    //Metodo para actualizar una bebida
     // Método para actualizar un producto asociado a una bebida
    public void updateBebida(Producto producto) {
        String query = "{CALL SP_UpdateProducto(?, ?, ?, ?, ?, ?)}";
        ConexionMySql conexionMySql = new ConexionMySql();

        try (Connection conn = conexionMySql.open(); CallableStatement cs = conn.prepareCall(query)) {

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
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el producto: " + e.getMessage(), e);
        }
    }
    
     // Método para eliminar un producto (cambiar a inactivo)
    public void deleteProducto(int idProducto) {
        String query = "{CALL SP_DeleteProducto(?)}";
        ConexionMySql conexionMySql = new ConexionMySql();

        try (Connection conn = conexionMySql.open(); CallableStatement cs = conn.prepareCall(query)) {

            // Asignar el parámetro al procedimiento almacenado
            cs.setInt(1, idProducto);

            // Ejecutar el procedimiento
            cs.execute();
            System.out.println("El producto fue marcado como inactivo exitosamente.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al eliminar (marcar como inactivo) el producto: " + e.getMessage(), e);
        }
    }
    
     // Método simplificado para buscar bebidas
    public List<Bebida> buscarBebidas(String nombreProducto, Double precio, Integer idCategoria) {
        String query = "{CALL SP_BuscarBebidas(?, ?, ?)}";
        List<Bebida> bebidas = new ArrayList<>();
        ConexionMySql conexionMySql = new ConexionMySql();

        try (Connection conn = conexionMySql.open(); CallableStatement cs = conn.prepareCall(query)) {

            // Asignar parámetros con null-safe
            cs.setString(1, nombreProducto != null && !nombreProducto.isEmpty() ? nombreProducto : null);
            cs.setObject(2, precio, java.sql.Types.DECIMAL);
            cs.setObject(3, idCategoria, java.sql.Types.INTEGER);

            // Ejecutar y procesar resultados
            ResultSet rs = cs.executeQuery();
            while (rs.next()) {
                // Crear objetos Producto y Bebida directamente
                Producto producto = new Producto(
                        rs.getInt("idProducto"),
                        rs.getString("nombreProducto"),
                        rs.getString("descripcionProducto"),
                        rs.getString("fotoProducto"),
                        rs.getDouble("precioProducto"),
                        new Categoria(
                                rs.getInt("idCategoria"),
                                rs.getString("categoriaDescripcion"),
                                rs.getString("categoriaTipo"),
                                1),
                        rs.getInt("productoActivo")
                );
                Bebida bebida = new Bebida(rs.getInt("idBebida"), producto);
                bebidas.add(bebida);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Error al buscar bebidas: " + e.getMessage(), e);
        }

        return bebidas;
    }

}
