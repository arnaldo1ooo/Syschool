/*
 * 
 * Carga una consulta realizada a un combobox
 * 
 */
package utilidades;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import javax.swing.JComboBox;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import conexion.Conexion;
import java.awt.Desktop;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.Map;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JInternalFrame;
import javax.swing.RowFilter;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.view.JasperViewer;
import org.apache.log4j.Logger;

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class Metodos {

    Conexion con = new Conexion();
    MetodosTXT metodostxt = new MetodosTXT();
    public int CantRegistros = 0;
    static Logger log_historial = Logger.getLogger(Metodos.class.getName());

    public void AnchuraColumna(JTable LaTabla) {
        LaTabla.setAutoResizeMode(JTable.AUTO_RESIZE_OFF); //Desactiva el autoresize
        TableColumnModel ModeloColumna = LaTabla.getColumnModel();
        int anchoacumulado = 0;
        int anchoadicional = 20;
        int cantidadcolumns = LaTabla.getColumnCount();
        int cantidadfilas = LaTabla.getRowCount();
        String nomheader; //Header = Cabecera
        TableColumn columnactual;
        Component componente;
        TableCellRenderer renderer;
        int anchoheaderenpixel;

        //Obtener tamano de String en pixeles
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB); //Elegir el tipo de fuente que usa
        Graphics2D graphics2d = img.createGraphics();
        Font font = new Font("Tahoma", Font.PLAIN, 12); //Poner la fuente tipo y tamano que se usa en la tabla
        FontMetrics fontmetrics = graphics2d.getFontMetrics(font);
        //System.out.println(fm.stringWidth("Este es un ejemplo"));
        graphics2d.dispose();

        for (int numdecolumna = 0; numdecolumna < cantidadcolumns; numdecolumna++) {
            int anchomax = 50; // Min width 
            columnactual = ModeloColumna.getColumn(numdecolumna);
            for (int fila = 0; fila < cantidadfilas; fila++) {
                renderer = LaTabla.getCellRenderer(fila, numdecolumna);
                componente = LaTabla.prepareRenderer(renderer, fila, numdecolumna);
                nomheader = columnactual.getHeaderValue().toString(); //Header es cabecera de la columna
                anchoheaderenpixel = fontmetrics.stringWidth(nomheader);
                anchomax = Math.max(componente.getPreferredSize().width + anchoadicional, anchomax);
                if (anchomax <= anchoheaderenpixel || cantidadfilas == 0) { //Si el ancho del registtro mas largo de la columna es menor a la cabecera 
                    anchomax = anchoheaderenpixel;
                }
            }
            if (cantidadfilas == 0) { //Si no hay ningun registro
                nomheader = columnactual.getHeaderValue().toString();
                anchoheaderenpixel = fontmetrics.stringWidth(nomheader);
                anchomax = anchoheaderenpixel + anchoadicional;
            }
            if (numdecolumna < (cantidadcolumns - 1)) { //Si no es la ultima columna
                columnactual.setPreferredWidth(anchomax); //Asigna a la columna el ancho del registro mas largo de la columna 
                anchoacumulado = anchoacumulado + anchomax; //Acumula el ancho de las columnas excepto el ultimo
            } else { //Ultima columna
                int anchototal = (int) LaTabla.getParent().getSize().getWidth(); //Tamano total del scroll que contiene a la tabla
                if ((anchoacumulado + anchomax) <= anchototal) {//Si la suma de la anchura de todas las columnas es menor o igual al ancho del scroll
                    int resta = anchototal - anchoacumulado; //Resta entre el ancho total del scroll y el ancho sumado de las columnas anteriores
                    columnactual.setPreferredWidth(resta);
                } else { //Si es mayor asigna el ancho del registro mas largo de la columna
                    columnactual.setPreferredWidth(anchomax);
                }
            }
        }
    }

    public void CambiarColorAlternadoTabla(JTable LaTabla, final Color colorback1, final Color colorback2) {
        LaTabla.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                c.setBackground(row % 2 == 0 ? colorback1 : colorback2);
                return c;
            }
        }
        );
    }

    public void FiltroJTable(String cadenaABuscar, int columnaABuscar, JTable ElJTable) {
        //System.out.println("Metodo FiltroJTable:  cadena: " + cadenaABuscar + ", columna: " + columnaABuscar);
        TableRowSorter modelFiltrado = new TableRowSorter<>(ElJTable.getModel());
        modelFiltrado.setRowFilter(RowFilter.regexFilter("(?i)" + cadenaABuscar, columnaABuscar));
        ElJTable.setRowSorter(modelFiltrado);
        //ElJTable.repaint();
    }

    public void ConsultaFiltroTablaBD(JTable LaTabla, String titlesJtabla[], String campoconsulta[], String nombresp, String filtro, JComboBox cbCampoBuscar) {
        String sentencia;
        DefaultTableModel modelotabla = new DefaultTableModel(null, titlesJtabla);

        if (cbCampoBuscar.getItemCount() == 0) {//Si combo esta vacio
            for (int i = 0; i < titlesJtabla.length; i++) {
                cbCampoBuscar.addItem(titlesJtabla[i]);
            }
            cbCampoBuscar.addItem("Todos");
            cbCampoBuscar.setSelectedIndex(1);
        }

        if (cbCampoBuscar.getSelectedItem() == "Todos") {
            String todoscamposconsulta = campoconsulta[0]; //Cargar el combo campobuscar
            //Cargamos todos los titulos en un String separado por comas
            for (int i = 1; i < campoconsulta.length; i++) {
                todoscamposconsulta = todoscamposconsulta + ", " + campoconsulta[i];
            }
            sentencia = "CALL " + nombresp + " ('" + todoscamposconsulta + "', '" + filtro + "');";
        } else {
            sentencia = "CALL " + nombresp + " ('" + campoconsulta[cbCampoBuscar.getSelectedIndex()] + "', '" + filtro + "');";
        }

        cbCampoBuscar.setEnabled(true);

        System.out.println("sentencia filtro tabla BD: " + sentencia);

        Connection conexion;
        Statement st;
        ResultSet rs;
        try {
            conexion = (Connection) Conexion.ConectarBasedeDatos();
            st = conexion.createStatement();
            rs = st.executeQuery(sentencia);
            ResultSetMetaData mdrs = rs.getMetaData();
            int numColumns = mdrs.getColumnCount();
            Object[] registro = new Object[numColumns]; //el numero es la cantidad de columnas del rs
            CantRegistros = 0;
            while (rs.next()) {
                for (int j = 0; j < numColumns; j++) {
                    registro[j] = (rs.getString(j + 1));
                }
                modelotabla.addRow(registro);//agrega el registro a la tabla
                CantRegistros = CantRegistros + 1;
            }
            LaTabla.setModel(modelotabla);//asigna a la tabla el modelo creado

            conexion.close();
            st.close();
            rs.close();
        } catch (SQLException e) {
            log_historial.error("Error 1005: " + e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e);
        }
    }

    public void CentrarventanaJInternalFrame(JInternalFrame LaVentana) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - LaVentana.getWidth()) / 2);
        int y = 0; //(int) ((dimension.getHeight() - LaVentana.getHeight()) / 2);
        LaVentana.setLocation(x, y);
    }

    public void CentrarVentanaJDialog(JDialog LaVentana) {
        Dimension dimension = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (int) ((dimension.getWidth() - LaVentana.getWidth()) / 2);
        int y = (int) ((dimension.getHeight() - LaVentana.getHeight()) / 2);
        LaVentana.setLocation(x, y);
    }

    public String ObtenerCotizacion(String de, String a) {
        String valor = "";
        try {
            DecimalFormat df = new DecimalFormat("#.###");
            Conexion con = new Conexion();
            con = con.ObtenerRSSentencia("SELECT coti_valor FROM cambio WHERE cam_de = '" + de + "' AND cam_a = '" + a + "'");
            if (con.rs.next() == true) {
                valor = df.format(Double.parseDouble(con.rs.getString(1)));
                valor = valor.replace(".", ",");
            }
            con.DesconectarBasedeDatos();
        } catch (SQLException e) {
            log_historial.error("Error 1006: " + e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error al intentar obtener cambio " + e);
        }
        return valor;
    }

    public double SumarColumnaDouble(JTable LaTabla, int LaColumna) {
        double valor;
        double totalDouble = 0;

        try {
            if (LaTabla.getRowCount() > 0) {
                for (int i = 0; i < LaTabla.getRowCount(); i++) {
                    valor = metodostxt.DoubleAFormatoAmericano(LaTabla.getValueAt(i, LaColumna) + "");
                    totalDouble = totalDouble + valor;
                }
            }
        } catch (NumberFormatException e) {
            log_historial.error("Error 1007: " + e);
            e.printStackTrace();
        }
        return totalDouble;
    }

    public Icon AjustarIconoAButton(Icon icono, int largo) {
        ImageIcon imageicono = (ImageIcon) icono;
        Image img = imageicono.getImage();
        Image resizedImage = img.getScaledInstance(largo, largo, java.awt.Image.SCALE_SMOOTH);
        return new ImageIcon(resizedImage);
    }

    public void GenerarReporteJTABLE(String rutajasper, Map parametros, TableModel elTableModel) {
        try {
            InputStream isRutajasper = Metodos.class.getResourceAsStream(rutajasper);
            if (isRutajasper == null) {
                JOptionPane.showMessageDialog(null, "Archivo jasper no encontrado: " + rutajasper, "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                //Carga el archivo jasper
                JasperReport jrReporte = (JasperReport) JRLoader.loadObject(isRutajasper);
                //Carga el modelo de la tabla (Los titulos de la tabla deben coincidir con los fields del jasper)
                JasperPrint jprint;
                if (elTableModel != null) {
                    JRTableModelDataSource jrTableModel = new JRTableModelDataSource(elTableModel);
                    jprint = JasperFillManager.fillReport(jrReporte, parametros, jrTableModel);
                } else {  //Si el tablemodel viene null
                    jprint = JasperFillManager.fillReport(jrReporte, parametros, new JREmptyDataSource());
                }

                //JasperPrintManager.printPages(jprint, 1, 4, true); //Imprimir paginas especificas
                //JasperPrintManager.printReport(jprint, true); //Mandar directamente al dialogo de impresora, si es false imprime directo
                //Ver vista previa del reporte
                JasperViewer jViewer = new JasperViewer(jprint, false);//false para que al cerrar reporte no se cierre el sistema
                //jViewer.setTitle("Reporte de productos"); //Titulo de la ventana
                jViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
                jViewer.setZoomRatio((float) 0.8); //1 es Zoom al 100%
                jViewer.setExtendedState(JasperViewer.MAXIMIZED_BOTH); //Maximizado
                jViewer.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
                jViewer.requestFocus();
                jViewer.setVisible(true);

                //Exportar a pdf
                //JasperExportManager.exportReportToPdfFile(jprint, "C:/ss.pdf"); 
            }
        } catch (JRException e) {
            log_historial.error("Error 1008: " + e);
            e.printStackTrace();
        }
    }

    public void GenerarReporteSQL(String rutajasper, Map parametros, String consulta) {
        try {
            InputStream isRutajasper = Metodos.class.getResourceAsStream(rutajasper);
            if (isRutajasper == null) {
                JOptionPane.showMessageDialog(null, "Archivo jasper no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                //Carga el archivo jasper
                JasperReport jrReporte_productos = (JasperReport) JRLoader.loadObject(isRutajasper);

                //LOS TITULOS DE LA Consulta DEBEN COINCIDIR CON LOS FIELDS DEL JASPER
                Conexion con = new Conexion();
                con = con.ObtenerRSSentencia(consulta);
                JRResultSetDataSource rsLista = new JRResultSetDataSource(con.rs); //Para sql
                JasperPrint jprint = JasperFillManager.fillReport(jrReporte_productos, parametros, rsLista);

                JasperViewer jViewer = new JasperViewer(jprint, false);//false para que al cerrar reporte no se cierre el sistema
                //jViewer.setTitle("Reporte de productos"); //Titulo de la ventana
                jViewer.setDefaultCloseOperation(JasperViewer.DISPOSE_ON_CLOSE);
                jViewer.setZoomRatio((float) 0.8); //1 es Zoom al 100%
                jViewer.setExtendedState(JasperViewer.MAXIMIZED_BOTH); //Maximizado
                jViewer.setModalExclusionType(Dialog.ModalExclusionType.APPLICATION_EXCLUDE);
                jViewer.requestFocus();
                jViewer.setVisible(true);

                con.DesconectarBasedeDatos();
            }
        } catch (JRException e) {
            log_historial.error("Error 1009: " + e);
            e.printStackTrace();
        }
    }

    public String PermisoRol(String codUsuario, String modulo) {
        con = con.ObtenerRSSentencia("CALL SP_UsuarioRolConsulta('" + codUsuario + "','" + modulo + "')");

        String permisos = "";
        String roldenominacion;
        String aliasusuario;
        try {

            while (con.rs.next()) {
                roldenominacion = con.rs.getString("rol_denominacion");
                aliasusuario = con.rs.getString("usu_alias");
                switch (roldenominacion) {
                    case "ALTA":
                        permisos = permisos.concat("A");
                        break;

                    case "MODIFICAR":
                        permisos = permisos.concat("M");
                        break;

                    case "BAJA":
                        permisos = permisos.concat("B");
                        break;

                    default:
                        System.out.println("No coincide ninguno");
                        break;
                }
                System.out.println("El usuario " + aliasusuario + " en el modulo " + modulo + " tiene permiso para " + roldenominacion
                );

            }
        } catch (SQLException e) {
            log_historial.error("Error 1010: " + e);
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
        return permisos;
    }

    public String MayusPrimeraLetra(String laCadena) {
        if (laCadena == null || laCadena.isEmpty()) {
            return "";
        } else {
            return Character.toUpperCase(laCadena.charAt(0)) + laCadena.substring(1, laCadena.length()).toLowerCase();
        }
    }

    public String MayusCadaPrimeraLetra(String laCadena) {
        if (laCadena == null || laCadena.isEmpty()) {
            return "";
        } else {
            char[] caracteres = laCadena.toCharArray();
            caracteres[0] = Character.toUpperCase(caracteres[0]);

            // el -2 es para evitar una excepción al caernos del arreglo
            for (int i = 0; i < laCadena.length() - 2; i++) {
                // Es 'palabra'
                if (caracteres[i] == ' ' || caracteres[i] == '.' || caracteres[i] == ',') { //Separadores puede ser espacio . o ,
                    caracteres[i + 1] = Character.toUpperCase(caracteres[i + 1]); // Reemplazamos
                }
            }
            return new String(caracteres);
        }
    }

    public String SiStringEsNull(String laCadena) {
        if (laCadena.equals("null")) {
            laCadena = "";
        }
        return laCadena;
    }

    public void AbrirFichero(String ruta) {
        File elFile = new File(ruta);
        Desktop ficheroAEjecutar = Desktop.getDesktop();
        try {
            ficheroAEjecutar.open(elFile);
        } catch (IOException e) {
            log_historial.error("Error 1011: " + e);
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void OcultarColumna(JTable LaTabla, int NumColumna) {
        if (LaTabla.getColumnCount() >= NumColumna) {
            LaTabla.getColumnModel().getColumn(NumColumna).setMaxWidth(0);
            LaTabla.getColumnModel().getColumn(NumColumna).setMinWidth(0);
            LaTabla.getColumnModel().getColumn(NumColumna).setPreferredWidth(0);
        }
    }
}
