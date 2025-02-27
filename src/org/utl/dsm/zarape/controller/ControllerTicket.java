package org.utl.dsm.zarape.controller;

import org.utl.dsm.zarape.model.Ticket;
import org.utl.dsm.zarape.model.DetalleTicket;
import org.utl.dsm.zarape.bd.ConexionMySql;

import java.sql.*;

public class ControllerTicket {

    // Método para insertar un ticket
    public void insertTicket(Ticket ticket) {
        String query = "{CALL sp_agregar_ticket(?)}";
        try ( Connection conn = new ConexionMySql().open();  CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, ticket.getIdSucursal());
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al insertar el ticket: " + e.getMessage(), e);
        }
    }

    // Método para cambiar el estatus de un ticket
    public void cambiarEstatusTicket(int idTicket, int estatus) {
        String query = "{CALL sp_cambiar_estatus_ticket(?, ?)}";
        try ( Connection conn = new ConexionMySql().open();  CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, idTicket);
            cs.setInt(2, estatus);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar el estatus del ticket: " + e.getMessage(), e);
        }
    }

    // Método para cambiar el estado de pago de un ticket
    public void cambiarPagadoTicket(int idTicket, String pagado) {
        String query = "{CALL sp_cambiar_pagado_ticket(?, ?)}";
        try ( Connection conn = new ConexionMySql().open();  CallableStatement cs = conn.prepareCall(query)) {
            cs.setInt(1, idTicket);
            cs.setString(2, pagado);
            cs.execute();
        } catch (SQLException e) {
            throw new RuntimeException("Error al cambiar el estado de pago: " + e.getMessage(), e);
        }
    }

    // Método para agregar detalle a un ticket
    public void agregarDetalleTicket(DetalleTicket detalle) {
        String query = "{CALL sp_agregar_detalle_ticket(?, ?, ?, ?, ?)}";
        try ( Connection conn = new ConexionMySql().open();  CallableStatement cs = conn.prepareCall(query)) {
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

}
