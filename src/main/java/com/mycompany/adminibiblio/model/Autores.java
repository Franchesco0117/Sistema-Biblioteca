/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.adminibiblio.model;

import com.mycompany.adminibiblio.util.dataBaseConnexion;
import com.toedter.calendar.JDateChooser;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
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
public class Autores extends Entidad {

    private int id;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private LocalDate fechaNacimiento;
    private String nacionalidad;

    // Constructor vacío
    public Autores() {}

    // Constructor
    public Autores(int id, String nombre, String primerApellido, String segundoApellido, LocalDate fechaNacimiento,
                   String nacionalidad) {
        this.id = id;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.fechaNacimiento = fechaNacimiento;
        this.nacionalidad = nacionalidad;
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

    public void insertarAutor(JTextField nombre, JTextField primerApellido, JTextField segundoApellido, JDateChooser fechaNacimientoChooser,
                              JComboBox<String> nacionalidadComboBox) {

        setNombre(nombre.getText());
        setPrimerApellido(primerApellido.getText());
        setSegundoApellido(segundoApellido.getText());
        setNacionalidad(nacionalidadComboBox.getSelectedItem().toString());

        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            Date fechaNacimientoDate = fechaNacimientoChooser.getDate();
            LocalDate fechaNacimientoLocalDate = fechaNacimientoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            setFechaNacimiento(fechaNacimientoLocalDate);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener la fecha de nacimiento: " + e.toString());
            return; // Salir del método si hay un error en la fecha
        }

        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "INSERT INTO autores (nombre, primer_ap, segundo_ap, fecha_nacimiento, pais_id) VALUES (?, ?, ?, ?, ?);";

        try {
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);

            ps.setString(1, getNombre());
            ps.setString(2, getPrimerApellido());
            ps.setString(3, getSegundoApellido());
            ps.setString(4, getFechaNacimiento().format(dbFormatter));
            ps.setInt(5, getSelectedNacionalidadId(nacionalidadComboBox));

            ps.execute();
            JOptionPane.showMessageDialog(null, "Autor registrado con éxito.");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al registrar el autor: " + e.toString());
        }
    }

    public void mostrarAutores(JTable tablaAutores) {

        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(modelo);

        tablaAutores.setRowSorter(ordenarTabla);

        modelo.addColumn("Id");
        modelo.addColumn("Nombre");
        modelo.addColumn("Primer Apellido");
        modelo.addColumn("Segundo Apellido");
        modelo.addColumn("Fecha Nacimiento");
        modelo.addColumn("Nacionalidad");

        tablaAutores.setModel(modelo);

        String sql = "SELECT a.id, a.nombre, a.primer_ap, a.segundo_ap, a.fecha_nacimiento, p.pais AS nacionalidad FROM autores a JOIN pais_origen p ON a.pais_id = p.id;";
        String[] datos = new String[6];

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                datos[0] = resultSet.getString("id");
                datos[1] = resultSet.getString("nombre");
                datos[2] = resultSet.getString("primer_ap");
                datos[3] = resultSet.getString("segundo_ap");
                datos[4] = resultSet.getString("fecha_nacimiento");
                datos[5] = resultSet.getString("nacionalidad");
                modelo.addRow(datos);
            }

            tablaAutores.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudieron mostrar los registros: " + e.toString());
        }
    }

    public void modificarAutor(JTextField id, JTextField nombre, JTextField primerApellido, JTextField segundoApellido, JDateChooser fechaNacimientoChooser,
                               JComboBox<String> nacionalidadComboBox) {
        try {
            int autorId = Integer.parseInt(id.getText().trim());
            String nuevoNombre = nombre.getText().trim();
            String nuevoPrimerApellido = primerApellido.getText().trim();
            String nuevoSegundoApellido = segundoApellido.getText().trim();
            int nuevaNacionalidadId = getSelectedNacionalidadId(nacionalidadComboBox);

            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            Date fechaNacimientoDate = fechaNacimientoChooser.getDate();
            LocalDate fechaNacimientoLocalDate = fechaNacimientoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String fechaNacimientoDb = fechaNacimientoLocalDate.format(dbFormatter);

            dataBaseConnexion objetoConexion = new dataBaseConnexion();
            String consulta = "UPDATE autores SET nombre = ?, primer_ap = ?, segundo_ap = ?, fecha_nacimiento = ?, pais_id = ? WHERE id = ?;";
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);

            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevoPrimerApellido);
            ps.setString(3, nuevoSegundoApellido);
            ps.setString(4, fechaNacimientoDb);
            ps.setInt(5, nuevaNacionalidadId);
            ps.setInt(6, autorId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Modificación exitosa");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el autor con el ID especificado");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en los datos numéricos: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fallo en modificar: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
        }
    }

    public void eliminarAutor(JTextField idTextField) {
        int autorId = Integer.parseInt(idTextField.getText());
        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        try {
            // Obtener los libros asociados con el autor
            String obtenerLibrosQuery = "SELECT id_libro FROM autores_libros WHERE id_autor = ?;";
            PreparedStatement obtenerLibrosStmt = objetoConexion.estableceConexion().prepareStatement(obtenerLibrosQuery);
            obtenerLibrosStmt.setInt(1, autorId);
            ResultSet rs = obtenerLibrosStmt.executeQuery();

            // Eliminar los libros asociados
            String eliminarLibroQuery = "DELETE FROM libros WHERE id = ?;";
            PreparedStatement eliminarLibroStmt = objetoConexion.estableceConexion().prepareStatement(eliminarLibroQuery);

            while (rs.next()) {
                int libroId = rs.getInt("id_libro");
                eliminarLibroStmt.setInt(1, libroId);
                eliminarLibroStmt.executeUpdate();
            }

            // Eliminar la relación en autores_libros
            String eliminarRelacionQuery = "DELETE FROM autores_libros WHERE id_autor = ?;";
            PreparedStatement eliminarRelacionStmt = objetoConexion.estableceConexion().prepareStatement(eliminarRelacionQuery);
            eliminarRelacionStmt.setInt(1, autorId);
            eliminarRelacionStmt.executeUpdate();

            // Eliminar el autor
            String eliminarAutorQuery = "DELETE FROM autores WHERE id = ?;";
            PreparedStatement eliminarAutorStmt = objetoConexion.estableceConexion().prepareStatement(eliminarAutorQuery);
            eliminarAutorStmt.setInt(1, autorId);
            eliminarAutorStmt.executeUpdate();

            JOptionPane.showMessageDialog(null, "Autor y libros asociados eliminados correctamente");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar el autor y los libros asociados: " + e.toString());
        }
    }

    public boolean buscarAutor(JTextField idField, JTextField nombre, JTextField primerApellido, JTextField segundoApellido, JDateChooser fechaNacimientoChooser,
                               JComboBox<String> nacionalidadComboBox, JTable tablaAutores) {
        JTextField[] campos = {nombre, primerApellido, segundoApellido};
        int[] columnas = {1, 2, 3};
        JComboBox<?>[] comboBoxes = new JComboBox<?>[]{nacionalidadComboBox};
        int[] comboColumns = {5};
        return buscarEntidad(idField, campos, tablaAutores, columnas, comboBoxes, comboColumns);
    }

    public void seleccionarAutor(JTable tablaAutores, JTextField id, JTextField nombre, JTextField primerApellido, JTextField segundoApellido,
                                 JDateChooser fechaNacimientoChooser, JComboBox<String> nacionalidadComboBox) {
        JTextField[] campos = {id, nombre, primerApellido, segundoApellido};
        int[] columnas = {0, 1, 2, 3};
        JComboBox<?>[] comboBoxes = new JComboBox<?>[]{nacionalidadComboBox};
        int[] comboColumns = {5};
        seleccionarEntidad(tablaAutores, campos, columnas, comboBoxes, comboColumns);

        int filaSeleccionada = tablaAutores.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            String fechaTexto = tablaAutores.getValueAt(filaSeleccionada, 4).toString();
            LocalDate fecha = LocalDate.parse(fechaTexto, formatter);
            Date fechaDate = Date.from(fecha.atStartOfDay(ZoneId.systemDefault()).toInstant());
            fechaNacimientoChooser.setDate(fechaDate);
        }
    }


    // Getters y setters
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

    public String getPrimerApellido() {
        return primerApellido;
    }

    public void setPrimerApellido(String primerApellido) {
        this.primerApellido = primerApellido;
    }

    public String getSegundoApellido() {
        return segundoApellido;
    }

    public void setSegundoApellido(String segundoApellido) {
        this.segundoApellido = segundoApellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }
}