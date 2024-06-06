/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.mycompany.adminibiblio.controller;

import com.formdev.flatlaf.FlatLightLaf;
import com.mycompany.adminibiblio.view.mainInterface;
import java.awt.Image;
import java.awt.Toolkit;
import javax.swing.UIManager;

/**
 *
 * @author cmpau
 */
public class AdminiBiblio {

    public static void main(String[] args) {
        mainInterface inicio = new mainInterface();
        inicio.setVisible(true);
        
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }
    }
}
