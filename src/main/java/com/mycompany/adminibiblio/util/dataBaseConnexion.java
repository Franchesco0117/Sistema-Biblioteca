/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.adminibiblio.util;

import java.sql.Connection;
import java.sql.DriverManager;
import javax.swing.JOptionPane;

/**
 *
 * @author cmpau
 */
public class dataBaseConnexion {
    Connection conectar = null;
    
    String usuario = "root";
    String contrasenia = "franciscocastromurillo012";
    String bd = "library_management";
    String ip = "localhost";
    String puerto = "3306";
    
    String cadena = "jdbc:mysql://" + ip + ":" + puerto + "/" + bd;
    
    public Connection estableceConexion() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            conectar = DriverManager.getConnection(cadena, usuario, contrasenia);
            // JOptionPane.showMessageDialog(null, "Conexion realizada con exito.");
        } catch(Exception e) {
            JOptionPane.showMessageDialog(null, "Error al conectarse bd: " + e.toString());
        }
        
        return conectar;
    }
    
}
