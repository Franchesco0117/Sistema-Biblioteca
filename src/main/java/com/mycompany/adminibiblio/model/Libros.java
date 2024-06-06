/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.adminibiblio.model;

import com.mycompany.adminibiblio.util.dataBaseConnexion;
import com.toedter.calendar.JYearChooser;

import javax.swing.JOptionPane;
import javax.swing.JTextField;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

/**
 *
 * @author cmpau
 */
public class Libros extends Entidad {

    private int id;
    private String titulo;
    private int idAutor;
    private int idEditorial;
    private int anioPublicacion;
    private String isbn;
    private int idCategoria;

    // Constructor vacio
    public Libros() {
    }

    // Constructor
    public Libros(int id, String titulo, int idAutor, int idEditorial, int anioPublicacion, String isbn, int idCategoria) {
        this.id = id;
        this.titulo = titulo;
        this.idAutor = idAutor;
        this.idEditorial = idEditorial;
        this.anioPublicacion = anioPublicacion;
        this.isbn = isbn;
        this.idCategoria = idCategoria;
    }

    public void llenarComboBoxAutores(JComboBox<String> comboBoxAutores) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "SELECT id, nombre FROM autores";

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            while (resultSet.next()) {
                int idAutor = resultSet.getInt("id");
                String nombreAutor = resultSet.getString("nombre");
                comboBoxAutores.addItem(idAutor + " - " + nombreAutor);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al llenar ComboBox de Autores: " + e.getMessage());
        }
    }

    public void llenarComboBoxEditoriales(JComboBox<String> comboBoxEditoriales) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "SELECT id, nombre FROM editoriales";

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            while (resultSet.next()) {
                int idEditorial = resultSet.getInt("id");
                String nombreEditorial = resultSet.getString("nombre");
                comboBoxEditoriales.addItem(idEditorial + " - " + nombreEditorial);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al llenar ComboBox de Editoriales: " + e.getMessage());
        }
    }

    public void llenarComboBoxCategorias(JComboBox<String> comboBoxCategorias) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "SELECT id, nombre FROM categorias";

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(consulta);

            while (resultSet.next()) {
                int idCategoria = resultSet.getInt("id");
                String nombreCategoria = resultSet.getString("nombre");
                comboBoxCategorias.addItem(idCategoria + " - " + nombreCategoria);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al llenar ComboBox de Categorias: " + e.getMessage());
        }
    }

    public void insertarLibros(JTextField titulo, JComboBox<String> comboBoxAutores, JComboBox<String> comboBoxEditoriales,
            JYearChooser anioPublicacionChooser, JTextField isbn, JComboBox<String> comboBoxCategorias) {

        setTitulo(titulo.getText());
        setAnioPublicacion(anioPublicacionChooser.getYear());
        setIsbn(isbn.getText());

        // Obtener idAutor, idEditorial y idCategoria desde los ComboBox
        String selectedAutor = comboBoxAutores.getSelectedItem().toString();
        String selectedEditorial = comboBoxEditoriales.getSelectedItem().toString();
        String selectedCategoria = comboBoxCategorias.getSelectedItem().toString();

        int idAutor = Integer.parseInt(selectedAutor.substring(0, selectedAutor.indexOf(" - ")));
        int idEditorial = Integer.parseInt(selectedEditorial.substring(0, selectedEditorial.indexOf(" - ")));
        int idCategoria = Integer.parseInt(selectedCategoria.substring(0, selectedCategoria.indexOf(" - ")));

        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        String consulta = "INSERT INTO libros (titulo, id_editorial, anio_publicacion, isbn, categoria_id) VALUES (?, ?, ?, ?, ?)";
        String consultaAutor = "INSERT INTO autores_libros (id_autor, id_libro) VALUES (?, ?)";

        try {
            // Establece la conexión a la base de datos y prepara las declaraciones
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta, Statement.RETURN_GENERATED_KEYS);

            // Asigna los valores a los parámetros de la consulta
            ps.setString(1, getTitulo());
            ps.setInt(2, idEditorial);
            ps.setInt(3, getAnioPublicacion());
            ps.setString(4, getIsbn());
            ps.setInt(5, idCategoria);

            // Ejecuta la inserción del libro
            ps.executeUpdate();

            // Obtén el ID generado para el nuevo libro
            ResultSet generatedKeys = ps.getGeneratedKeys();
            int libroId = 0;
            if (generatedKeys.next()) {
                libroId = generatedKeys.getInt(1);
            }

            // Inserta la relación autor-libro
            PreparedStatement psAutor = objetoConexion.estableceConexion().prepareStatement(consultaAutor);
            psAutor.setInt(1, idAutor);
            psAutor.setInt(2, libroId);
            psAutor.executeUpdate();

            JOptionPane.showMessageDialog(null, "Se insertó correctamente el libro");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en los datos numéricos: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se insertó el libro: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
        }
    }

    public void mostrarLibros(JTable tablaLibros) {

        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(modelo);

        tablaLibros.setRowSorter(ordenarTabla);

        modelo.addColumn("Id");
        modelo.addColumn("Titulo");
        modelo.addColumn("Autor");
        modelo.addColumn("Editorial");
        modelo.addColumn("Año de Publicacion");
        modelo.addColumn("ISBN");
        modelo.addColumn("Categoria");

        tablaLibros.setModel(modelo);

        String sql = "SELECT l.id, l.titulo, a.nombre AS autor, e.nombre AS editorial, l.anio_publicacion, l.isbn, c.nombre AS categoria "
                + "FROM libros l "
                + "LEFT JOIN autores_libros al ON l.id = al.id_libro "
                + "LEFT JOIN autores a ON al.id_autor = a.id "
                + "LEFT JOIN editoriales e ON l.id_editorial = e.id "
                + "LEFT JOIN categorias c ON l.categoria_id = c.id;";

        try {
            Statement statement = objetoConexion.estableceConexion().createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                String id = resultSet.getString("id");
                String titulo = resultSet.getString("titulo");
                String autor = resultSet.getString("autor");
                String editorial = resultSet.getString("editorial");
                String anioPublicacion = resultSet.getString("anio_publicacion");
                String isbn = resultSet.getString("isbn");
                String categoria = resultSet.getString("categoria");

                Object[] datos = {id, titulo, autor, editorial, anioPublicacion, isbn, categoria};
                modelo.addRow(datos);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo mostrar los registros: " + e.getMessage());
        }
    }

    public void actualizarLibros(int id, JTextField titulo, JComboBox<String> comboBoxAutores, JComboBox<String> comboBoxEditoriales,
            JYearChooser anioPublicacionChooser, JTextField isbn, JComboBox<String> comboBoxCategorias) {

        setTitulo(titulo.getText());
        setAnioPublicacion(anioPublicacionChooser.getYear());
        setIsbn(isbn.getText());

        // Obtener idAutor, idEditorial y idCategoria desde los ComboBox
        String selectedAutor = comboBoxAutores.getSelectedItem().toString();
        String selectedEditorial = comboBoxEditoriales.getSelectedItem().toString();
        String selectedCategoria = comboBoxCategorias.getSelectedItem().toString();

        int idAutor = Integer.parseInt(selectedAutor.substring(0, selectedAutor.indexOf(" - ")));
        int idEditorial = Integer.parseInt(selectedEditorial.substring(0, selectedEditorial.indexOf(" - ")));
        int idCategoria = Integer.parseInt(selectedCategoria.substring(0, selectedCategoria.indexOf(" - ")));

        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        String consulta = "UPDATE libros SET titulo = ?, id_editorial = ?, anio_publicacion = ?, isbn = ?, categoria_id = ? WHERE id = ?";
        String consultaAutor = "UPDATE autores_libros SET id_autor = ? WHERE id_libro = ?";

        try {
            // Establece la conexión a la base de datos y prepara las declaraciones
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);

            // Asigna los valores a los parámetros de la consulta
            ps.setString(1, getTitulo());
            ps.setInt(2, idEditorial);
            ps.setInt(3, getAnioPublicacion());
            ps.setString(4, getIsbn());
            ps.setInt(5, idCategoria);
            ps.setInt(6, id);

            // Ejecuta la actualización del libro
            ps.executeUpdate();

            // Actualiza la relación autor-libro
            PreparedStatement psAutor = objetoConexion.estableceConexion().prepareStatement(consultaAutor);
            psAutor.setInt(1, idAutor);
            psAutor.setInt(2, id);
            psAutor.executeUpdate();

            JOptionPane.showMessageDialog(null, "Se actualizó correctamente el libro");

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en los datos numéricos: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se actualizó el libro: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
        }
    }

    public void eliminarLibros(JTextField idTextField) {
        try {
            setId(Integer.parseInt(idTextField.getText()));
            dataBaseConnexion objetoConexion = new dataBaseConnexion();

            String consultaAutor = "DELETE FROM autores_libros WHERE id_libro = ?";
            String consultaLibro = "DELETE FROM libros WHERE id = ?";

            Connection conexion = objetoConexion.estableceConexion();
            conexion.setAutoCommit(false); // Inicia la transacción

            try {
                // Eliminar las relaciones en autores_libros primero
                PreparedStatement psAutor = conexion.prepareStatement(consultaAutor);
                psAutor.setInt(1, getId());
                psAutor.executeUpdate();
                psAutor.close();

                // Eliminar el libro
                PreparedStatement psLibro = conexion.prepareStatement(consultaLibro);
                psLibro.setInt(1, getId());
                psLibro.executeUpdate();
                psLibro.close();

                conexion.commit(); // Confirma la transacción
                JOptionPane.showMessageDialog(null, "Libro eliminado correctamente");

            } catch (SQLException e) {
                conexion.rollback(); // Reversa la transacción en caso de error
                JOptionPane.showMessageDialog(null, "No se pudo eliminar: " + e.getMessage());
            } finally {
                conexion.setAutoCommit(true); // Restablece el modo de confirmación automática
                conexion.close(); // Cierra la conexión
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en los datos numéricos: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar: " + e.getMessage());
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error inesperado: " + e.getMessage());
        }
    }

    public boolean buscarLibro(JTextField idField, JTextField titulo, JComboBox<String> comboBoxAutores, JComboBox<String> comboBoxEditoriales,
            JYearChooser anioPublicacionChooser, JTextField isbn, JComboBox<String> comboBoxCategorias, JTable tablaLibros) {
        JTextField[] campos = {titulo, isbn};
        int[] columnas = {1, 5};
        JComboBox<?>[] comboBoxes = new JComboBox<?>[]{comboBoxAutores, comboBoxEditoriales, comboBoxCategorias};
        int[] comboColumns = {2, 3, 6};

        boolean encontrado = buscarEntidad(idField, campos, tablaLibros, columnas, comboBoxes, comboColumns);

        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada >= 0) {
            int anioPublicacion = Integer.parseInt(tablaLibros.getValueAt(filaSeleccionada, 4).toString());
            anioPublicacionChooser.setYear(anioPublicacion);
        }

        return encontrado;
    }

    public void seleccionarLibro(JTable tablaLibros, JTextField id, JTextField titulo, JComboBox<String> comboBoxAutores,
            JComboBox<String> comboBoxEditoriales, JYearChooser anioPublicacionChooser, JTextField isbn, JComboBox<String> comboBoxCategorias) {
        JTextField[] campos = {id, titulo, isbn};
        int[] columnas = {0, 1, 5};
        JComboBox<?>[] comboBoxes = new JComboBox<?>[]{comboBoxAutores, comboBoxEditoriales, comboBoxCategorias};
        int[] comboColumns = {2, 3, 6};
        seleccionarEntidad(tablaLibros, campos, columnas, comboBoxes, comboColumns);

        int filaSeleccionada = tablaLibros.getSelectedRow();
        if (filaSeleccionada >= 0) {
            int anioPublicacion = Integer.parseInt(tablaLibros.getValueAt(filaSeleccionada, 4).toString());
            anioPublicacionChooser.setYear(anioPublicacion);
        }
    }

    // Getters y setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public int getIdAutor() {
        return idAutor;
    }

    public void setIdAutor(int idAutor) {
        this.idAutor = idAutor;
    }

    public int getIdEditorial() {
        return idEditorial;
    }

    public void setIdEditorial(int idEditorial) {
        this.idEditorial = idEditorial;
    }

    public int getAnioPublicacion() {
        return anioPublicacion;
    }

    public void setAnioPublicacion(int anioPublicacion) {
        this.anioPublicacion = anioPublicacion;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }
}
