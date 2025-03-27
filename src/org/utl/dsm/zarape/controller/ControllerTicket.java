package org.utl.dsm.zarape.controller;

import org.utl.dsm.zarape.model.Ticket;
import org.utl.dsm.zarape.model.DetalleTicket;
import org.utl.dsm.zarape.model.Comanda;
import org.utl.dsm.zarape.bd.ConexionMySql;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ControllerTicket {

    // Método para insertar un ticket
    public int insertTicket(Ticket ticket) {
        String query = "{CALL sp_agregar_ticket(?, ?)}";
        int ticketId = -1; // Valor por defecto en caso de error
        try (Connection conn = new ConexionMySql().open(); CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, ticket.getIdSucursal());
            cs.registerOutParameter(2, java.sql.Types.INTEGER); // Registrar el parámetro de salida
            cs.execute();
            ticketId = cs.getInt(2); // Obtener el ID del ticket insertado
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar el ticket: " + e.getMessage(), e);
        }
        return ticketId; // Retornar el ID del ticket insertado
    }

    // Método para cambiar el estatus de un ticket
    public void cambiarEstatusTicket(int idTicket) {
        String query = "{CALL sp_cambiar_estatus_ticket(?)}";
        try (Connection conn = new ConexionMySql().open(); CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, idTicket);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar el estatus del ticket: " + e.getMessage(), e);
        }
    }

    // Método para cambiar el estado de pago de un ticket
    public void cambiarPagadoTicket(int idTicket) {
        String query = "{CALL sp_cambiar_pagado_ticket(?)}";
        try (Connection conn = new ConexionMySql().open(); CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, idTicket);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar el estado de pago: " + e.getMessage(), e);
        }
    }

    // Método para agregar detalle a un ticket
    public void agregarDetalleTicket(DetalleTicket detalle) {
        String query = "{CALL sp_agregar_detalle_ticket(?, ?, ?, ?, ?)}";
        try (Connection conn = new ConexionMySql().open(); CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, detalle.getIdTicket());
            cs.setInt(2, detalle.getCantidad());
            cs.setDouble(3, detalle.getPrecio());
            cs.setObject(4, detalle.getIdCombo() != 0 ? detalle.getIdCombo() : null);
            cs.setObject(5, detalle.getIdProducto() != 0 ? detalle.getIdProducto() : null);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al agregar el detalle: " + e.getMessage(), e);
        }
    }

    // Método para obtener todas las comandas activas
    // Método para obtener todas las comandas activas
public List<Comanda> getAllQueue() {
    String query = "SELECT c.idComanda, c.idTicket, c.estatus, t.fecha, dt.cantidad, dt.precio, dt.idCombo, dt.idProducto, p.nombre AS nombreProducto " +
                  "FROM comanda c " +
                  "JOIN ticket t ON c.idTicket = t.idTicket " +
                  "LEFT JOIN detalle_ticket dt ON t.idTicket = dt.idTicket " +
                  "LEFT JOIN producto p ON dt.idProducto = p.idProducto " +
                  "WHERE c.estatus != 0 AND c.estatus != 3 " + // Excluimos "Cancelado" y "Entregado"
                  "AND t.pagado = 'S' " + // Solo tickets pagados
                  "ORDER BY t.fecha ASC";
    List<Comanda> comandas = new ArrayList<>();
    Map<Integer, Comanda> comandaMap = new HashMap<>();

    try (Connection conn = new ConexionMySql().open(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(query)) {
        while (rs.next()) {
            int idComanda = rs.getInt("idComanda");
            Comanda comanda = comandaMap.get(idComanda);

            if (comanda == null) {
                int estatusInt = rs.getInt("estatus");
                String estatusStr;
                switch (estatusInt) {
                    case 1:
                        estatusStr = "En proceso";
                        break;
                    case 2:
                        estatusStr = "Terminado";
                        break;
                    case 3:
                        estatusStr = "Entregado";
                        break;
                    case 0:
                        estatusStr = "Cancelado";
                        break;
                    default:
                        estatusStr = "Desconocido";
                }
                comanda = new Comanda(
                    idComanda,
                    rs.getInt("idTicket"),
                    estatusStr,
                    rs.getString("fecha")
                );
                comandaMap.put(idComanda, comanda);
                comandas.add(comanda);
            }

            int idProducto = rs.getInt("idProducto");
            if (idProducto != 0) {
                DetalleTicket detalle = new DetalleTicket(
                    rs.getInt("idTicket"),
                    rs.getInt("cantidad"),
                    rs.getDouble("precio"),
                    rs.getInt("idCombo"),
                    idProducto
                );
                detalle.getProducto().setNombre(rs.getString("nombreProducto"));
                comanda.getDetalles().add(detalle);
            }
        }
    } catch (SQLException e) {
        throw new RuntimeException("Error al obtener la cola de pedidos: " + e.getMessage(), e);
    }
    return comandas;
}

    // Método para actualizar el estatus de una comanda
    public void updateEstatus(int idComanda, int estatus) {
        String query = "UPDATE comanda SET estatus = ? WHERE idComanda = ?";
        try (Connection conn = new ConexionMySql().open(); PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, estatus);
            pstmt.setInt(2, idComanda);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error al actualizar el estatus: " + e.getMessage(), e);
        }
    }
}