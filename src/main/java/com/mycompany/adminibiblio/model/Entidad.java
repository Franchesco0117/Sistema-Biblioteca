/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.adminibiblio.model;

import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;

public abstract class Entidad {
    
    public void limpiarCampos(JTextField... campos) {
        for (JTextField campo : campos) {
            campo.setText("");
        }
    }
    
    public boolean buscarEntidad(JTextField idField, JTextField[] campos, JTable tabla, int[] columnas) {
        String idTexto = idField.getText().trim();
        
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un ID.");
            return false;
        }
        
        try {
            int idBuscado = Integer.parseInt(idTexto);
            
            for (int i = 0; i < tabla.getRowCount(); i++) {
                int idTabla = Integer.parseInt(tabla.getValueAt(i, 0).toString());
                if (idTabla == idBuscado) {
                    tabla.setRowSelectionInterval(i, i);
                    idField.setText(tabla.getValueAt(i, 0).toString());
                    for (int j = 0; j < campos.length; j++) {
                        campos[j].setText(tabla.getValueAt(i, columnas[j]).toString());
                    }
                    return true;
                }
            }
            
            JOptionPane.showMessageDialog(null, "No se encontró una entidad con el ID especificado.");
            return false;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un ID válido.");
            return false;
        }
    }
    
    public void seleccionarEntidad(JTable tabla, JTextField[] campos, int[] columnas) {
        try {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                for (int i = 0; i < campos.length; i++) {
                    campos[i].setText(tabla.getValueAt(fila, columnas[i]).toString());
                }
            } else {
                JOptionPane.showMessageDialog(null, "Fila no seleccionada");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de selección: " + e.toString());
        }
    }

    public boolean buscarEntidad(JTextField idField, JTextField[] campos, JTable tabla, int[] columnas, JComboBox<?>[] comboBoxes, int[] comboColumns) {
        String idTexto = idField.getText().trim();
        
        if (idTexto.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un ID.");
            return false;
        }
        
        try {
            int idBuscado = Integer.parseInt(idTexto);
            
            for (int i = 0; i < tabla.getRowCount(); i++) {
                int idTabla = Integer.parseInt(tabla.getValueAt(i, 0).toString());
                if (idTabla == idBuscado) {
                    tabla.setRowSelectionInterval(i, i);
                    idField.setText(tabla.getValueAt(i, 0).toString());
                    for (int j = 0; j < campos.length; j++) {
                        campos[j].setText(tabla.getValueAt(i, columnas[j]).toString());
                    }
                    for (int j = 0; j < comboBoxes.length; j++) {
                        String value = tabla.getValueAt(i, comboColumns[j]).toString();
                        comboBoxes[j].setSelectedItem(value);
                    }
                    return true;
                }
            }
            
            JOptionPane.showMessageDialog(null, "No se encontró una entidad con el ID especificado.");
            return false;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(null, "Por favor, ingrese un ID válido.");
            return false;
        }
    }
    
    public void seleccionarEntidad(JTable tabla, JTextField[] campos, int[] columnas, JComboBox<?>[] comboBoxes, int[] comboColumns) {
        try {
            int fila = tabla.getSelectedRow();
            if (fila >= 0) {
                for (int i = 0; i < campos.length; i++) {
                    campos[i].setText(tabla.getValueAt(fila, columnas[i]).toString());
                }
                for (int i = 0; i < comboBoxes.length; i++) {
                    String value = tabla.getValueAt(fila, comboColumns[i]).toString();
                    comboBoxes[i].setSelectedItem(value);
                }
            } else {
                JOptionPane.showMessageDialog(null, "Fila no seleccionada");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error de selección: " + e.toString());
        }
    }
}
