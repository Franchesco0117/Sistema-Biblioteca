/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.adminibiblio.model;

import com.mycompany.adminibiblio.util.dataBaseConnexion;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author cmpau
 */

public class Reseñas extends Entidad {

    private int id;
    private int idLibro;
    private int idUsuario;
    private int puntuacion;

    // Constructor vacío
    public Reseñas() {
    }

    // Constructor
    public Reseñas(int id, int idLibro, int idUsuario, int puntuacion) {
        this.id = id;
        this.idLibro = idLibro;
        this.idUsuario = idUsuario;
        this.puntuacion = puntuacion;
    }

    // Método para insertar una nueva reseña
    public void insertarResenia(JTextField idLibro, JTextField idUsuario, JTextField puntuacion) {
        setIdLibro(Integer.parseInt(idLibro.getText().trim()));
        setIdUsuario(Integer.parseInt(idUsuario.getText().trim()));
        setPuntuacion(Integer.parseInt(puntuacion.getText().trim()));

        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "INSERT INTO resenias (id_libro, id_usuario, puntuacion) VALUES (?, ?, ?);";

        try {
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);
            ps.setInt(1, getIdLibro());
            ps.setInt(2, getIdUsuario());
            ps.setInt(3, getPuntuacion());

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Reseña registrada con éxito.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar la reseña: " + e.toString());
        }
    }

    // Método para mostrar las reseñas
    public void mostrarResenias(JTable tablaResenias) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(modelo);

        tablaResenias.setRowSorter(ordenarTabla);

        modelo.addColumn("Id");
        modelo.addColumn("Id Libro");
        modelo.addColumn("Id Usuario");
        modelo.addColumn("Puntuacion");

        tablaResenias.setModel(modelo);

        String sql = "SELECT * FROM resenias;";
        String[] datos = new String[4];

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                datos[0] = resultSet.getString(1);
                datos[1] = resultSet.getString(2);
                datos[2] = resultSet.getString(3);
                datos[3] = resultSet.getString(4);
                modelo.addRow(datos);
            }

            tablaResenias.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudieron mostrar los registros: " + e.toString());
        }
    }

    // Método para modificar una reseña
    public void modificarResenia(JTextField id, JTextField idLibro, JTextField idUsuario, JTextField puntuacion) {
        try {
            int reseniaId = Integer.parseInt(id.getText().trim());
            int nuevoIdLibro = Integer.parseInt(idLibro.getText().trim());
            int nuevoIdUsuario = Integer.parseInt(idUsuario.getText().trim());
            int nuevaPuntuacion = Integer.parseInt(puntuacion.getText().trim());

            dataBaseConnexion objetoConexion = new dataBaseConnexion();
            String consulta = "UPDATE resenias SET id_libro = ?, id_usuario = ?, puntuacion = ? WHERE id = ?;";
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);

            ps.setInt(1, nuevoIdLibro);
            ps.setInt(2, nuevoIdUsuario);
            ps.setInt(3, nuevaPuntuacion);
            ps.setInt(4, reseniaId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Modificación exitosa");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la reseña con el ID especificado");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en los datos numéricos: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fallo en modificar: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
        }
    }

    // Método para eliminar una reseña
    public void eliminarResenia(JTextField idTextField) {
        setId(Integer.parseInt(idTextField.getText().trim()));
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "DELETE FROM resenias WHERE id = ?;";

        try {
            CallableStatement statement = objetoConexion.estableceConexion().prepareCall(consulta);
            statement.setInt(1, getId());
            statement.execute();
            JOptionPane.showMessageDialog(null, "Reseña eliminada correctamente");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar la reseña: " + e.toString());
        }
    }

    public boolean buscarResenia(JTextField idField, JTextField idLibro, JTextField idUsuario, JTextField puntuacion, JTable tablaResenias) {
        JTextField[] campos = {idLibro, idUsuario, puntuacion};
        int[] columnas = {1, 2, 3};
        return buscarEntidad(idField, campos, tablaResenias, columnas);
    }

    public void seleccionarResenia(JTable tablaResenias, JTextField id, JTextField idLibro, JTextField idUsuario, JTextField puntuacion) {
        JTextField[] campos = {id, idLibro, idUsuario, puntuacion};
        int[] columnas = {0, 1, 2, 3};
        seleccionarEntidad(tablaResenias, campos, columnas);
    }

    // Métodos getter y setter
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLibro() {
        return idLibro;
    }

    public void setIdLibro(int idLibro) {
        this.idLibro = idLibro;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public void setPuntuacion(int puntuacion) {
        this.puntuacion = puntuacion;
    }
}
