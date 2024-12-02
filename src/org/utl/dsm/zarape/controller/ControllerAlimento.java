package org.utl.dsm.zarape.controller;

import org.utl.dsm.zarape.model.Alimento;
import org.utl.dsm.zarape.bd.ConexionMySql;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import org.utl.dsm.zarape.model.Categoria;
import org.utl.dsm.zarape.model.Producto;

public class ControllerAlimento {

    // Método para insertar un alimento
    public void insert(Alimento alimento) {
        String query = "{CALL SP_InsertProducto(?, ?, ?, ?, ?, ?)}";
        ConexionMySql conexionMySql = new ConexionMySql();

        try ( Connection conn = conexionMySql.open();  CallableStatement cs = conn.prepareCall(query)) {
            // Asignar parámetros del procedimiento
            cs.setString(1, alimento.getProducto().getNombre());
            cs.setString(2, alimento.getProducto().getDescripcion());
            cs.setString(3, alimento.getProducto().getFoto());
            cs.setDouble(4, alimento.getProducto().getPrecio());
            cs.setInt(5, alimento.getProducto().getCategoria().getIdCategoria());
            cs.setString(6, "alimento"); // Especificamos que es un alimento

            // Ejecutar el procedimiento
            cs.execute();
            System.out.println("El alimento fue insertado exitosamente.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al insertar el alimento: " + e.getMessage(), e);
        }
    }

    // Método para ver todos los alimentos
    public List<Alimento> getAllAlimentos() {
        List<Alimento> alimentos = new ArrayList<>();
        String query = "SELECT * FROM vw_AlimentosInfo";

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

                // Mapeo de Alimento
                int idAlimento = rs.getInt("idAlimento");
                Alimento alimento = new Alimento(idAlimento, producto);

                alimentos.add(alimento);
            }

            rs.close();
            ps.close();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al obtener los alimentos: " + e.getMessage(), e);
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return alimentos;
    }

    // Método para actualizar un producto asociado a un alimento
    public void updateAlimento(Producto producto) {
        String query = "{CALL SP_UpdateProducto(?, ?, ?, ?, ?, ?)}";
        ConexionMySql conexionMySql = new ConexionMySql();

        try ( Connection conn = conexionMySql.open();  CallableStatement cs = conn.prepareCall(query)) {
            // Asignar parámetros al procedimiento almacenado
            cs.setInt(1, producto.getIdProducto());
            cs.setString(2, producto.getNombre());
            cs.setString(3, producto.getDescripcion());
            cs.setString(4, producto.getFoto());
            cs.setDouble(5, producto.getPrecio());
            cs.setInt(6, producto.getCategoria().getIdCategoria());

            // Ejecutar el procedimiento
            cs.execute();
            System.out.println("El producto asociado al alimento fue actualizado exitosamente.");

        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al actualizar el producto: " + e.getMessage(), e);
        }
    }

    // Método para eliminar un producto (cambiar a inactivo)
    public void deleteProducto(int idProducto) {
        String query = "{CALL SP_DeleteProducto(?)}"; // Llama al procedimiento almacenado
        ConexionMySql conexionMySql = new ConexionMySql();

        try ( Connection conn = conexionMySql.open();  CallableStatement cs = conn.prepareCall(query)) {
            // Asignar el parámetro al procedimiento almacenado
            cs.setInt(1, idProducto);

            // Ejecutar el procedimiento
            cs.execute();
            System.out.println("El producto fue marcado como inactivo/activo exitosamente.");
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error al cambiar el estatus del producto: " + e.getMessage(), e);
        }
    }

    // Método simplificado para buscar alimentos
    public List<Alimento> searchAlimentos(String filtro) {
    String query = "{CALL SP_SearchAlimentos(?)}"; // Solo un parámetro de filtro
    List<Alimento> alimentos = new ArrayList<>();

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

                // Mapeo del alimento
                Alimento alimento = new Alimento(rs.getInt("idAlimento"), producto);
                alimentos.add(alimento);
            }
        }

    } catch (SQLException e) {
        throw new RuntimeException("Error al buscar bebidas: " + e.getMessage(), e);
    }

    return alimentos;
}
}
