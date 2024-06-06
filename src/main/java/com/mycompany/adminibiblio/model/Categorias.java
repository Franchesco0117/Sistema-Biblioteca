/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.adminibiblio.model;

import com.mycompany.adminibiblio.util.dataBaseConnexion;
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
public class Categorias extends Entidad {
    
    private int id;
    private String nombre;
    
    // Constructor vacío
    public Categorias() {}
    
    // Constructor
    public Categorias(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }
    
    // Método para insertar una nueva categoría
    public void insertarCategoria(JTextField nombre) {
        setNombre(nombre.getText());

        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "INSERT INTO categorias (nombre) VALUES (?);";

        try {
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);
            ps.setString(1, getNombre());
            ps.execute();
            JOptionPane.showMessageDialog(null, "Se insertó correctamente la categoría");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se insertó la categoría: " + e.toString());
        }
    }
    
    // Método para mostrar las categorías
    public void mostrarCategorias(JTable tablaCategorias) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(modelo);

        tablaCategorias.setRowSorter(ordenarTabla);

        modelo.addColumn("Id");
        modelo.addColumn("Nombre");

        tablaCategorias.setModel(modelo);

        String sql = "SELECT * FROM categorias;";
        String[] datos = new String[2];

        try (Statement statement = objetoConexion.estableceConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                datos[0] = resultSet.getString(1);
                datos[1] = resultSet.getString(2);
                modelo.addRow(datos);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo mostrar los registros: " + e.toString());
        }
    }
    
    // Método para modificar una categoría
    public void modificarCategoria(JTextField id, JTextField nombre) {
        try {
            int categoriaId = Integer.parseInt(id.getText().trim());
            String nuevoNombre = nombre.getText().trim();

            dataBaseConnexion objetoConexion = new dataBaseConnexion();
            String consulta = "UPDATE categorias SET nombre = ? WHERE id = ?;";

            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);
            ps.setString(1, nuevoNombre);
            ps.setInt(2, categoriaId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Modificación exitosa");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la categoría con el ID especificado");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en los datos numéricos: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fallo en modificar: " + e.getMessage());
        }
    }
        
    // Método para eliminar una categoría
    public void eliminarCategoria(JTextField idTextField) {
        setId(Integer.parseInt(idTextField.getText()));
        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        String consulta = "DELETE FROM categorias WHERE id = ?;";

        try {
            PreparedStatement statement = objetoConexion.estableceConexion().prepareStatement(consulta);
            statement.setInt(1, getId());
            statement.execute();

            JOptionPane.showMessageDialog(null, "Categoría eliminada correctamente");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar: " + e.toString());
        }
    }
    
    // Método para buscar una categoría
    public boolean buscarCategoria(JTextField idField, JTextField nombre, JTable tablaCategorias) {
        JTextField[] campos = {nombre};
        int[] columnas = {1};
        return buscarEntidad(idField, campos, tablaCategorias, columnas);
    }
    
    // Método para seleccionar una categoría
    public void seleccionarCategoria(JTable tablaCategorias, JTextField id, JTextField nombre) {
        JTextField[] campos = {id, nombre};
        int[] columnas = {0, 1};
        seleccionarEntidad(tablaCategorias, campos, columnas);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    
}
