/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import java.awt.Color;
import java.awt.Component;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 *
 * @author Arnaldo_Cantero
 */
public class ColorearJTable extends DefaultTableCellRenderer {

    public Component getTableCellRendererComponent(JTable table, Object valor, boolean isSelected, boolean hasFocus, int fila, int columna) {
        JLabel celda = (JLabel) super.getTableCellRendererComponent(table, valor, isSelected, hasFocus, fila, columna);
        celda.setBackground(Color.WHITE);
        celda.setForeground(Color.BLACK);

        if (columna == 2) {
            if (valor.equals("NO MATRICULADO")) {
                celda.setBackground(Color.RED);
                celda.setForeground(Color.WHITE);
            }
        }

        return celda;
    }
    
    
    
}
