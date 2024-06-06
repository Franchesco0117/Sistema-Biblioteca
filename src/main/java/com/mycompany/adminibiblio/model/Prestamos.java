/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.adminibiblio.model;

/**
 *
 * @author cmpau
 */
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

public class Prestamos extends Entidad {
    
    private int id;
    private int idLibro;
    private int idUsuario;
    private LocalDate fechaPrestamo;
    private LocalDate fechaDevolucionPrevista;
    private String estado;
    
    // Constructor vacio
    public Prestamos(){}
    
    // Constructor
    public Prestamos(int id, int idLibro, int idUsuario, LocalDate fechaPrestamo, 
            LocalDate fechaDevolucionPrevista, String estado) {
        
        this.id = id;
        this.idLibro = idLibro;
        this.idUsuario = idUsuario;
        this.fechaPrestamo = fechaPrestamo;
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
        this.estado = estado;
    }
    
    public void llenarComboBoxLibros(JComboBox<String> comboBoxLibros) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "SELECT id, titulo FROM libros";

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            while (resultSet.next()) {
                int idLibro = resultSet.getInt("id");
                String titulo = resultSet.getString("titulo");
                comboBoxLibros.addItem(idLibro + " - " + titulo);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al llenar ComboBox de Libros: " + e.getMessage());
        }
    }

    public void llenarComboBoxUsuarios(JComboBox<String> comboBoxUsuarios) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "SELECT id, nombre FROM usuarios";

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            while (resultSet.next()) {
                int idUsuario = resultSet.getInt("id");
                String nombre = resultSet.getString("nombre");
                comboBoxUsuarios.addItem(idUsuario + " - " + nombre);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al llenar ComboBox de Usuarios: " + e.getMessage());
        }
    }

    public void llenarComboBoxEstadoPrestamo(JComboBox<String> comboBoxEstadoPrestamo) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "SELECT id, estado FROM estado_prestamo";

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            while (resultSet.next()) {
                int idEstado = resultSet.getInt("id");
                String estado = resultSet.getString("estado");
                comboBoxEstadoPrestamo.addItem(idEstado + " - " + estado);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al llenar ComboBox de Estado de Préstamo: " + e.getMessage());
        }
    }

    private int getSelectedId(JComboBox<String> comboBox) {
        String selectedItem = comboBox.getSelectedItem().toString();
        return Integer.parseInt(selectedItem.split(" - ")[0]);
    }

    public void insertarPrestamo(JComboBox<String> idLibroComboBox, JComboBox<String> idUsuarioComboBox,
                                 JDateChooser fechaPrestamoChooser, JDateChooser fechaDevolucionPrevistaChooser,
                                 JComboBox<String> estadoComboBox) {

        setIdLibro(getSelectedId(idLibroComboBox));
        setIdUsuario(getSelectedId(idUsuarioComboBox));

        DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try {
            Date fechaPrestamoDate = fechaPrestamoChooser.getDate();
            LocalDate fechaPrestamoLocalDate = fechaPrestamoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            setFechaPrestamo(fechaPrestamoLocalDate);

            Date fechaDevolucionPrevistaDate = fechaDevolucionPrevistaChooser.getDate();
            LocalDate fechaDevolucionPrevistaLocalDate = fechaDevolucionPrevistaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            setFechaDevolucionPrevista(fechaDevolucionPrevistaLocalDate);

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al obtener las fechas: " + e.toString());
            return; // Salir del método si hay un error en las fechas
        }

        setEstado(estadoComboBox.getSelectedItem().toString().split(" - ")[1]);

        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "INSERT INTO prestamos (id_libro, id_usuario, fecha_prestamo, fecha_devolucion, id_estado_prestamo) VALUES (?, ?, ?, ?, ?);";

        try {
            CallableStatement cs = objetoConexion.estableceConexion().prepareCall(consulta);

            cs.setInt(1, getIdLibro());
            cs.setInt(2, getIdUsuario());
            cs.setString(3, getFechaPrestamo().format(dbFormatter));
            cs.setString(4, getFechaDevolucionPrevista().format(dbFormatter));
            cs.setInt(5, getSelectedId(estadoComboBox));

            cs.execute();
            JOptionPane.showMessageDialog(null, "Préstamo registrado con éxito.");

        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error al registrar el préstamo: " + e.toString());
        }
    }

    public void mostrarPrestamos(JTable tablaPrestamos) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(modelo);

        tablaPrestamos.setRowSorter(ordenarTabla);

        modelo.addColumn("Id");
        modelo.addColumn("Libro");
        modelo.addColumn("Usuario");
        modelo.addColumn("Fecha Préstamo");
        modelo.addColumn("Fecha Devolución");
        modelo.addColumn("Estado");

        tablaPrestamos.setModel(modelo);

        String sql = "SELECT p.id, l.titulo AS libro, u.nombre AS usuario, p.fecha_prestamo, p.fecha_devolucion, e.estado " +
                     "FROM prestamos p " +
                     "JOIN libros l ON p.id_libro = l.id " +
                     "JOIN usuarios u ON p.id_usuario = u.id " +
                     "JOIN estado_prestamo e ON p.id_estado_prestamo = e.id;";
        String[] datos = new String[6];

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                datos[0] = resultSet.getString("id");
                datos[1] = resultSet.getString("libro");
                datos[2] = resultSet.getString("usuario");
                datos[3] = resultSet.getString("fecha_prestamo");
                datos[4] = resultSet.getString("fecha_devolucion");
                datos[5] = resultSet.getString("estado");
                modelo.addRow(datos);
            }

            tablaPrestamos.setModel(modelo);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudieron mostrar los registros: " + e.toString());
        }
    }

    public void modificarPrestamo(JTextField idField, JComboBox<String> idLibroComboBox, JComboBox<String> idUsuarioComboBox,
                                  JDateChooser fechaPrestamoChooser, JDateChooser fechaDevolucionPrevistaChooser,
                                  JComboBox<String> estadoComboBox) {
        try {
            int prestamoId = Integer.parseInt(idField.getText().trim());
            int nuevoIdLibro = getSelectedId(idLibroComboBox);
            int nuevoIdUsuario = getSelectedId(idUsuarioComboBox);

            DateTimeFormatter dbFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            Date fechaPrestamoDate = fechaPrestamoChooser.getDate();
            LocalDate fechaPrestamoLocalDate = fechaPrestamoDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String nuevaFechaPrestamo = fechaPrestamoLocalDate.format(dbFormatter);

            Date fechaDevolucionPrevistaDate = fechaDevolucionPrevistaChooser.getDate();
            LocalDate fechaDevolucionPrevistaLocalDate = fechaDevolucionPrevistaDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            String nuevaFechaDevolucionPrevista = fechaDevolucionPrevistaLocalDate.format(dbFormatter);

            int nuevoEstadoId = getSelectedId(estadoComboBox);

            dataBaseConnexion objetoConexion = new dataBaseConnexion();
            String consulta = "UPDATE prestamos SET id_libro = ?, id_usuario = ?, fecha_prestamo = ?, fecha_devolucion = ?, id_estado_prestamo = ? WHERE id = ?;";
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);

            ps.setInt(1, nuevoIdLibro);
            ps.setInt(2, nuevoIdUsuario);
            ps.setString(3, nuevaFechaPrestamo);
            ps.setString(4, nuevaFechaDevolucionPrevista);
            ps.setInt(5, nuevoEstadoId);
            ps.setInt(6, prestamoId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Modificación exitosa");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el préstamo con el ID especificado");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en los datos numéricos: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fallo en modificar: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
        }
    }

    public void eliminarPrestamo(JTextField idField) {
        setId(Integer.parseInt(idField.getText()));
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "DELETE FROM prestamos WHERE id = ?;";

        try {
            CallableStatement statement = objetoConexion.estableceConexion().prepareCall(consulta);
            statement.setInt(1, getId());
            statement.execute();
            JOptionPane.showMessageDialog(null, "Préstamo eliminado correctamente");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar el préstamo: " + e.toString());
        }
    }

    public boolean buscarPrestamo(JTextField idField, JComboBox<String> idLibroComboBox, JComboBox<String> idUsuarioComboBox,
                                  JDateChooser fechaPrestamoChooser, JDateChooser fechaDevolucionPrevistaChooser,
                                  JComboBox<String> estadoComboBox, JTable tablaPrestamos) {
        JTextField[] campos = {};
        int[] columnas = {};
        JComboBox[] comboBoxes = {idLibroComboBox, idUsuarioComboBox, estadoComboBox};
        int[] comboColumns = {1, 2, 5};
        boolean encontrado = buscarEntidad(idField, campos, tablaPrestamos, columnas, comboBoxes, comboColumns);

        int filaSeleccionada = tablaPrestamos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String fechaPrestamoTexto = tablaPrestamos.getValueAt(filaSeleccionada, 3).toString();
            LocalDate fechaPrestamo = LocalDate.parse(fechaPrestamoTexto, formatter);
            Date fechaPrestamoDate = Date.from(fechaPrestamo.atStartOfDay(ZoneId.systemDefault()).toInstant());
            fechaPrestamoChooser.setDate(fechaPrestamoDate);

            String fechaDevolucionTexto = tablaPrestamos.getValueAt(filaSeleccionada, 4).toString();
            LocalDate fechaDevolucion = LocalDate.parse(fechaDevolucionTexto, formatter);
            Date fechaDevolucionDate = Date.from(fechaDevolucion.atStartOfDay(ZoneId.systemDefault()).toInstant());
            fechaDevolucionPrevistaChooser.setDate(fechaDevolucionDate);
        }

        return encontrado;
    }

    public void seleccionarPrestamo(JTable tablaPrestamos, JTextField idField, JComboBox<String> idLibroComboBox,
                                    JComboBox<String> idUsuarioComboBox, JDateChooser fechaPrestamoChooser,
                                    JDateChooser fechaDevolucionPrevistaChooser, JComboBox<String> estadoComboBox) {
        JTextField[] campos = {idField};
        int[] columnas = {0};
        JComboBox[] comboBoxes = {idLibroComboBox, idUsuarioComboBox, estadoComboBox};
        int[] comboColumns = {1, 2, 5};
        seleccionarEntidad(tablaPrestamos, campos, columnas, comboBoxes, comboColumns);

        int filaSeleccionada = tablaPrestamos.getSelectedRow();
        if (filaSeleccionada >= 0) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            String fechaPrestamoTexto = tablaPrestamos.getValueAt(filaSeleccionada, 3).toString();
            LocalDate fechaPrestamo = LocalDate.parse(fechaPrestamoTexto, formatter);
            Date fechaPrestamoDate = Date.from(fechaPrestamo.atStartOfDay(ZoneId.systemDefault()).toInstant());
            fechaPrestamoChooser.setDate(fechaPrestamoDate);

            String fechaDevolucionTexto = tablaPrestamos.getValueAt(filaSeleccionada, 4).toString();
            LocalDate fechaDevolucion = LocalDate.parse(fechaDevolucionTexto, formatter);
            Date fechaDevolucionDate = Date.from(fechaDevolucion.atStartOfDay(ZoneId.systemDefault()).toInstant());
            fechaDevolucionPrevistaChooser.setDate(fechaDevolucionDate);
        }
    }

    // Getters y setters
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

    public LocalDate getFechaPrestamo() {
        return fechaPrestamo;
    }

    public void setFechaPrestamo(LocalDate fechaPrestamo) {
        this.fechaPrestamo = fechaPrestamo;
    }

    public LocalDate getFechaDevolucionPrevista() {
        return fechaDevolucionPrevista;
    }

    public void setFechaDevolucionPrevista(LocalDate fechaDevolucionPrevista) {
        this.fechaDevolucionPrevista = fechaDevolucionPrevista;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}