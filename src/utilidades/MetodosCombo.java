/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilidades;

import conexion.Conexion;
import java.awt.Color;
import java.awt.Graphics;
import java.sql.SQLException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JPopupMenu;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

/**
 *
 * @author Lic. Arnaldo Cantero
 */


public class MetodosCombo {

    private int codigo;
    private String descripcion;
    Conexion con = new Conexion();
    static Logger log_historial = Logger.getLogger(MetodosCombo.class.getName());

    public MetodosCombo() { //No borrar
    }

    public MetodosCombo(int codigo, String descripcion) {
        this.codigo = codigo;
        this.descripcion = descripcion;
    }

    public int getCodigo() {
        return codigo;
    }

    public void setCodigo(int id) {
        this.codigo = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return this.descripcion;
    }

    public void setSelectedNombreItem(JComboBox ElCombo, String nombreitem) {
        MetodosCombo item;
        for (int i = 0; i < ElCombo.getItemCount(); i++) {
            item = (MetodosCombo) ElCombo.getItemAt(i);
            if (item.getDescripcion().equalsIgnoreCase(nombreitem)) {
                ElCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    public void setSelectedCodigoItem(JComboBox ElCombo, int codigoitem) {
        MetodosCombo item;
        for (int i = 0; i < ElCombo.getItemCount(); i++) {
            item = (MetodosCombo) ElCombo.getItemAt(i);
            if (item.getCodigo() == codigoitem) {
                ElCombo.setSelectedIndex(i);
                break;
            }
        }
    }

    public void CargarComboBox(JComboBox elCombo, String sentencia, int codItemDefault) {
        System.out.println("Cargar combo (" + elCombo.getName() + "): " + sentencia);
        try {
            elCombo.removeAllItems(); //Vaciamos el combo
            AutoCompleteDecorator.decorate(elCombo);
            con = con.ObtenerRSSentencia(sentencia);

            while (con.rs.next()) {
                elCombo.addItem(new MetodosCombo(con.rs.getInt(1), con.rs.getString(2)));

                //Seleccionado por defecto
                try {
                    if (codItemDefault == con.rs.getInt(1) && codItemDefault >= 0) {
                        elCombo.setSelectedItem(con.rs.getString(2));
                    }
                } catch (NullPointerException e) {
                    log_historial.warn("Error 1012: " + e);
                    e.printStackTrace();
                }
            }

            if (elCombo.getItemCount() > 0 || codItemDefault == -1) {
                elCombo.setSelectedIndex(-1);
            }

            elCombo.setMaximumRowCount(elCombo.getModel().getSize()); //Hace que se despliegue en toda la pantalla vertical el combo
            AddScrollHorizontalCombo(elCombo);
        } catch (NumberFormatException e) {
            log_historial.error("Error al cargar combo: " + e);
            e.printStackTrace();
        } catch (SQLException e) {
            log_historial.error("Error al cargar combo: " + e);
            e.printStackTrace();
        }

        CambiarColorDisabledCombo(elCombo, Color.BLACK);
        con.DesconectarBasedeDatos();
    }

    public void CambiarColorDisabledCombo(JComboBox elCombo, Color elColor) {
        //Cambiar color de texto del combo cuando esta disabled
        elCombo.setRenderer(new DefaultListCellRenderer() {
            public void paint(Graphics g) {
                setForeground(elColor);
                super.paint(g);
            }
        });
    }

    public int ObtenerIDSelectComboBox(JComboBox<MetodosCombo> ElCombo) {
        int codigoitemselect = -1;
        try {
            codigoitemselect = ElCombo.getItemAt(ElCombo.getSelectedIndex()).getCodigo();
        } catch (Exception e) {
            log_historial.error("ObtenerIdCombo: No se selecciono ningun item en el combo: " + e);
            e.printStackTrace();
        }
        return codigoitemselect;
    }

    private void AddScrollHorizontalCombo(JComboBox ElCombo) {
        if (ElCombo.getItemCount() == 0) {
            return;
        }
        Object comp = ElCombo.getUI().getAccessibleChild(ElCombo, 0);
        if (!(comp instanceof JPopupMenu)) {
            return;
        }
        JPopupMenu popup = (JPopupMenu) comp;
        JScrollPane scrollPane = (JScrollPane) popup.getComponent(0);
        scrollPane.setHorizontalScrollBar(new JScrollBar(JScrollBar.HORIZONTAL));
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    }
}
