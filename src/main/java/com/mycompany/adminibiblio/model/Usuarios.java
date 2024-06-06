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
public class Usuarios extends Entidad {
    
    private int id;
    private String nombre;
    private String primerApellido;
    private String segundoApellido;
    private String direccion;
    private String telefono;
    private String correoElectronico;
    
    // Constructor vacío
    public Usuarios() {}
    
    // Constructor
    public Usuarios(int id, String nombre, String primerApellido, String segundoApellido, String direccion, String telefono, String correoElectronico) {
        this.id = id;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
        this.segundoApellido = segundoApellido;
        this.direccion = direccion;
        this.telefono = telefono;
        this.correoElectronico = correoElectronico;
    }
    
    public void insertarUsuarios(JTextField nombre, JTextField primerApellido, JTextField segundoApellido, JTextField direccion, JTextField telefono, JTextField correoElectronico) {
        
        setNombre(nombre.getText());
        setPrimerApellido(primerApellido.getText());
        setSegundoApellido(segundoApellido.getText());
        setDireccion(direccion.getText());
        setTelefono(telefono.getText());
        setCorreoElectronico(correoElectronico.getText());
        
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        String consulta = "INSERT INTO usuarios (nombre, primer_ap, segundo_ap, direccion, telefono, correo_electronico) VALUES (?, ?, ?, ?, ?, ?);";

        try {
            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);
            
            ps.setString(1, getNombre());
            ps.setString(2, getPrimerApellido());
            ps.setString(3, getSegundoApellido());
            ps.setString(4, getDireccion());
            ps.setString(5, getTelefono());
            ps.setString(6, getCorreoElectronico());
            
            ps.execute();
            JOptionPane.showMessageDialog(null, "Se insertó correctamente el usuario");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se insertó el usuario: " + e.toString());
        }
    }
    
    public void mostrarUsuarios(JTable tablaUsuarios) {
        dataBaseConnexion objetoConexion = new dataBaseConnexion();
        DefaultTableModel modelo = new DefaultTableModel();
        TableRowSorter<TableModel> ordenarTabla = new TableRowSorter<>(modelo);

        tablaUsuarios.setRowSorter(ordenarTabla);

        modelo.addColumn("Id");
        modelo.addColumn("Nombre");
        modelo.addColumn("Primer Apellido");
        modelo.addColumn("Segundo Apellido");
        modelo.addColumn("Direccion");
        modelo.addColumn("Telefono");
        modelo.addColumn("Correo Electronico");

        tablaUsuarios.setModel(modelo);

        String sql = "SELECT * FROM usuarios;";

        String[] datos = new String[7];

        try (Statement statement = objetoConexion.estableceConexion().createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                datos[0] = resultSet.getString(1);
                datos[1] = resultSet.getString(2);
                datos[2] = resultSet.getString(3);
                datos[3] = resultSet.getString(4);
                datos[4] = resultSet.getString(5);
                datos[5] = resultSet.getString(6);
                datos[6] = resultSet.getString(7);

                modelo.addRow(datos);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo mostrar los registros: " + e.toString());
        }
    }
    
    public void modificarUsuarios(JTextField id, JTextField nombre, JTextField primerApellido, JTextField segundoApellido, JTextField direccion,
                                  JTextField telefono, JTextField correoElectronico) {

        try {
            int usuarioId = Integer.parseInt(id.getText().trim());
            String nuevoNombre = nombre.getText().trim();
            String nuevoPrimerApellido = primerApellido.getText().trim();
            String nuevoSegundoApellido = segundoApellido.getText().trim();
            String nuevaDireccion = direccion.getText().trim();
            String nuevoTelefono = telefono.getText().trim();
            String nuevoCorreoElectronico = correoElectronico.getText().trim();

            dataBaseConnexion objetoConexion = new dataBaseConnexion();
            String consulta = "UPDATE usuarios SET nombre = ?, primer_ap = ?, segundo_ap = ?, direccion = ?, telefono = ?, correo_electronico = ? WHERE id = ?;";

            PreparedStatement ps = objetoConexion.estableceConexion().prepareStatement(consulta);

            ps.setString(1, nuevoNombre);
            ps.setString(2, nuevoPrimerApellido);
            ps.setString(3, nuevoSegundoApellido);
            ps.setString(4, nuevaDireccion);
            ps.setString(5, nuevoTelefono);
            ps.setString(6, nuevoCorreoElectronico);
            ps.setInt(7, usuarioId);

            int rowsAffected = ps.executeUpdate();

            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Modificación exitosa");
            } else {
                JOptionPane.showMessageDialog(null, "No se encontró el Usuario con el ID especificado");
            }

        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Error en los datos numéricos: " + e.getMessage());
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Fallo en modificar: " + e.getMessage());
        }
    }
    
    public void eliminarUsuarios(JTextField idTextField) {
        setId(Integer.parseInt(idTextField.getText()));
        dataBaseConnexion objetoConexion = new dataBaseConnexion();

        String consulta = "DELETE FROM usuarios WHERE id = ?;";

        try {
            PreparedStatement statement = objetoConexion.estableceConexion().prepareStatement(consulta);
            statement.setInt(1, getId());
            statement.execute();

            JOptionPane.showMessageDialog(null, "Usuario eliminado correctamente");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "No se pudo eliminar: " + e.toString());
        }
    }
    
    public boolean buscarUsuario(JTextField idField, JTextField nombre, JTextField primerApellido, JTextField segundoApellido, JTextField direccion,
                                 JTextField telefono, JTextField correoElectronico, JTable tablaUsuarios) {
        JTextField[] campos = {nombre, primerApellido, segundoApellido, direccion, telefono, correoElectronico};
        int[] columnas = {1, 2, 3, 4, 5, 6};
        return buscarEntidad(idField, campos, tablaUsuarios, columnas);
    }
    
    public void seleccionarUsuario(JTable tablaUsuarios, JTextField id, JTextField nombre, JTextField primerApellido, JTextField segundoApellido, 
                                   JTextField direccion, JTextField telefono, JTextField correoElectronico) {
        JTextField[] campos = {id, nombre, primerApellido, segundoApellido, direccion, telefono, correoElectronico};
        int[] columnas = {0, 1, 2, 3, 4, 5, 6};
        seleccionarEntidad(tablaUsuarios, campos, columnas);
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

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getCorreoElectronico() {
        return correoElectronico;
    }

    public void setCorreoElectronico(String correoElectronico) {
        this.correoElectronico = correoElectronico;
    }
}
