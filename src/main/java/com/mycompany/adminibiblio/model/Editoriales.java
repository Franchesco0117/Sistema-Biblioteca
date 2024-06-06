/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.adminibiblio.model;

import com.mycompany.adminibiblio.util.dataBaseConnexion;
import com.toedter.calendar.JYearChooser;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JComboBox;
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
public class Editoriales extends Entidad {

    private int id;
    private String nombre;
    private String paisOrigen;
    private int anoFundacion;

    // Constructor vacio
    public Editoriales() {
    }

    // Constructor
    public Editoriales(int id, String nombre, String paisOrigen, int anoFundacion) {
        this.id = id;
        this.nombre = nombre;
        this.paisOrigen = paisOrigen;
        this.anoFundacion = anoFundacion;
    }

    public void llenarComboBoxNacionalidades(JComboBox<String> comboBoxNacionalidades) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "SELECT id, pais FROM pais_origen";

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            while (resultSet.next()) {
                int idNacionalidad = resultSet.getInt("id");
                String nombrePais = resultSet.getString("pais");
                comboBoxNacionalidades.addItem(idNacionalidad + " - " + nombrePais);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al llenar ComboBox de Paises: " + e.getMessage());
        }
    }

    private int getSelectedNacionalidadId(JComboBox<String> nacionalidadComboBox) {
        String selectedItem = nacionalidadComboBox.getSelectedItem().toString();
        return Integer.parseInt(selectedItem.split(" - ")[0]);
    }

    public void insertarEditorial(JTextField nombreField, JComboBox<String> paisOrigenComboBox, JYearChooser anoFundacionChooser) {
        String nombre = nombreField.getText();
        int paisOrigenId = getSelectedNacionalidadId(paisOrigenComboBox);
        int anoFundacion = anoFundacionChooser.getYear();

        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "INSERT INTO editoriales (nombre, pais_id, ano_fundacion) VALUES (?, ?, ?);";

        try {
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);

            ps.setString(1, nombre);
            ps.setInt(2, paisOrigenId);
            ps.setInt(3, anoFundacion);

            ps.executeUpdate();
            JOptionPane.showMessageDialog(null, "Editorial registrada con éxito.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar la editorial: " + e.toString());
        }
    }

    public void mostrarEditoriales(JTable tablaEditoriales) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(modelo);

        tablaEditoriales.setRowSorter(ordenarTabla);

        modelo.addColumn("Id");
        modelo.addColumn("Nombre");
        modelo.addColumn("País Origen");
        modelo.addColumn("Año Fundación");

        tablaEditoriales.setModel(modelo);

        String sql = "SELECT e.id, e.nombre, p.pais AS pais_origen, e.ano_fundacion FROM editoriales e JOIN pais_origen p ON e.pais_id = p.id;";
        String[] datos = new String[4];

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                datos[0] = resultSet.getString("id");
                datos[1] = resultSet.getString("nombre");
                datos[2] = resultSet.getString("pais_origen");
                datos[3] = resultSet.getString("ano_fundacion");
                modelo.addRow(datos);
            }

            tablaEditoriales.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudieron mostrar los registros: " + e.toString());
        }
    }

    public void modificarEditorial(JTextField idField, JTextField nombreField, JComboBox<String> paisOrigenComboBox, JYearChooser anoFundacionChooser) {
        try {
            int id = Integer.parseInt(idField.getText());
            String nombre = nombreField.getText();
            int paisOrigenId = getSelectedNacionalidadId(paisOrigenComboBox);
            int anoFundacion = anoFundacionChooser.getYear();

            dataBaseConnexion objetoConexion = new dataBaseConnexion();
            String consulta = "UPDATE editoriales SET nombre = ?, pais_id = ?, ano_fundacion = ? WHERE id = ?;";

            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);

            ps.setString(1, nombre);
            ps.setInt(2, paisOrigenId);
            ps.setInt(3, anoFundacion);
            ps.setInt(4, id);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Modificación exitosa");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró la editorial con el ID especificado");
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fallo en modificar: " + e.getMessage());
        }
    }

    public void eliminarEditorial(JTextField idField) {
        int id = Integer.parseInt(idField.getText());
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "DELETE FROM editoriales WHERE id = ?;";

        try {
            PreparedStatement statement = objetoConexion.estableceConexion().prepareStatement(consulta);
            statement.setInt(1, id);
            statement.execute();
            JOptionPane.showMessageDialog(null, "Editorial eliminada correctamente");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar la editorial: " + e.toString());
        }
    }

    public boolean buscarEditorial(JTextField idField, JTextField nombreField, JYearChooser anoFundacionChooser, JComboBox<String> paisOrigenComboBox, JTable tablaEditoriales) {
        JTextField[] campos = {nombreField};
        int[] columnas = {1};
        JComboBox<?>[] comboBoxes = new JComboBox<?>[]{paisOrigenComboBox};
        int[] comboColumns = {2};
        boolean encontrado = buscarEntidad(idField, campos, tablaEditoriales, columnas, comboBoxes, comboColumns);

        int filaSeleccionada = tablaEditoriales.getSelectedRow();
        if (filaSeleccionada >= 0) {
            int anoFundacion = Integer.parseInt(tablaEditoriales.getValueAt(filaSeleccionada, 3).toString());
            anoFundacionChooser.setYear(anoFundacion);
        }

        return encontrado;
    }

    public void seleccionarEditorial(JTable tablaEditoriales, JTextField idField, JTextField nombreField, JYearChooser anoFundacionChooser, JComboBox<String> paisOrigenComboBox) {
        JTextField[] campos = {idField, nombreField};
        int[] columnas = {0, 1};
        JComboBox<?>[] comboBoxes = new JComboBox<?>[]{paisOrigenComboBox};
        int[] comboColumns = {2};
        seleccionarEntidad(tablaEditoriales, campos, columnas, comboBoxes, comboColumns);

        int filaSeleccionada = tablaEditoriales.getSelectedRow();
        if (filaSeleccionada >= 0) {
            int anoFundacion = Integer.parseInt(tablaEditoriales.getValueAt(filaSeleccionada, 3).toString());
            anoFundacionChooser.setYear(anoFundacion);
        }
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

    public String getPaisOrigen() {
        return paisOrigen;
    }

    public void setPaisOrigen(String paisOrigen) {
        this.paisOrigen = paisOrigen;
    }

    public int getAnoFundacion() {
        return anoFundacion;
    }

    public void setAnoFundacion(int anoFundacion) {
        this.anoFundacion = anoFundacion;
    }
}
