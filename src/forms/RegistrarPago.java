package forms;

import dao.DAO;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static login.Login.codUsuario;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.data.JRTableModelDataSource;
import service.ConceptoPagoService;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;
import utilidades.ColorearCelda;

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class RegistrarPago extends javax.swing.JDialog {

    private DAO con = new DAO();
    private Metodos metodos = new Metodos();
    private MetodosTXT metodostxt = new MetodosTXT();
    private MetodosCombo metodoscombo = new MetodosCombo();
    private DefaultTableModel modelTableConceptoAPagar;
    private DefaultTableModel modelTablePoderantes;
    private DefaultTableModel modelTableConceptos;
    private DefaultTableModel modelTableApoderados;
    private Calendar c2 = new GregorianCalendar();
    private int anhoActual = c2.get(Calendar.YEAR);
    private ConceptoPagoService conceptoPagoService = new ConceptoPagoService();

    public RegistrarPago(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        modelTableConceptoAPagar = (DefaultTableModel) tbConceptoAPagar.getModel();
        modelTablePoderantes = (DefaultTableModel) tbPoderantes.getModel();
        modelTableApoderados = (DefaultTableModel) tbApoderado.getModel();

        Date finPeriodoEscolar = ObtenerFechaFinPeriodo(anhoActual);

        dcFechaPago.setDate(new Date()); //Fecha actual
        if (dcFechaPago.getDate().before(finPeriodoEscolar)) { //Si la fecha de pago es despues del fin del periodo escolar
            lblPeriodoActual.setText(anhoActual + "");
        } else {
            lblPeriodoActual.setText(anhoActual + 1 + "");
        }

        //Llamar Metodos
        GenerarNumpago();
        CargarComboBoxes();
        Limpiar();
        TablaAllConcepto();
        TablaAllApoderado();

        metodos.CargarTitlesaCombo(cbCampoBuscarApoderado, tbApoderado);
        txtCedulaApoderado.setText("");

        //Permiso Roles de usuario
        String permisos = metodos.PermisoRol(codUsuario, "PAGO");
        btnGuardar.setVisible(permisos.contains("A"));

        OrdenTabulador();
    }

    private Date ObtenerFechaFinPeriodo(int anho) { //Obtener de bd fin fecha periodo
        int dia = 1, mes = 1;
        String[] diames;
        String finPeriodoEscolarString;
        Date finPeriodoEscolar = new Date();
        try {
            con = con.ObtenerRSSentencia("SELECT conf_valor FROM configuracion WHERE conf_codigo = '3'");
            if (con.getResultSet().next()) {
                diames = con.getResultSet().getString("conf_valor").split("/");
                dia = Integer.parseInt(diames[0]);
                mes = Integer.parseInt(diames[1]);
            } else {
                System.out.println("No se encontro configuracion Fin fecha Periodo");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            finPeriodoEscolarString = dia + "/" + mes + "/" + anho;
            finPeriodoEscolar = sdf.parse(finPeriodoEscolarString);
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();

        return finPeriodoEscolar;
    }

//--------------------------METODOS----------------------------//
    private void CargarComboBoxes() { //Carga los combobox con las consultas
        metodoscombo.CargarComboConsulta(cbApoderado, "SELECT apo_codigo, CONCAT(apo_nombre,' ', apo_apellido) AS nomape "
                + "FROM apoderado ORDER BY apo_nombre", -1);
    }

    private void TablaAllConcepto() {//Realiza la consulta de los productos que tenemos en la base de datos
        modelTableConceptos = (DefaultTableModel) tbAllConceptos.getModel();
        modelTableConceptos.setRowCount(0); //Vacia tabla
        try {
            String sentencia = "SELECT con_codigo, con_descripcion, con_tipoimporte, con_importe, con_numpagos, con_tipopago FROM concepto ORDER BY con_codigo";
            con = con.ObtenerRSSentencia(sentencia);
            int codigo, numpagos;
            String descripcion, tipoimporte, tipopago;
            double importe;
            while (con.getResultSet().next()) {
                codigo = con.getResultSet().getInt("con_codigo");
                descripcion = con.getResultSet().getString("con_descripcion");
                tipoimporte = con.getResultSet().getString("con_tipoimporte");
                importe = con.getResultSet().getDouble("con_importe");
                numpagos = con.getResultSet().getInt("con_numpagos");
                tipopago = con.getResultSet().getString("con_tipopago");

                modelTableConceptos.addRow(new Object[]{codigo, descripcion, tipoimporte, importe, numpagos, tipopago});
            }
            tbAllConceptos.setModel(modelTableConceptos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
        metodos.AnchuraColumna(tbAllConceptos);
    }

    public void RegistroNuevo() {
        try {
            if (ComprobarCampos() == true) {
                String numpago = lblNumPago.getText();
                int alumno = metodoscombo.ObtenerIDSelectCombo(cbApoderado);
                DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
                String fechapago = formatoFecha.format(dcFechaPago.getDate());
                double importe = metodostxt.StringAFormatoAmericano(txtImporteRecibido.getText());
                int periodo = Integer.parseInt(lblPeriodoActual.getText());

                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de registrar este nuevo pago?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    try {
                        //Registrar nuevo pago
                        String sentencia = "CALL SP_PagoAlta('" + numpago + "','" + alumno + "','" + fechapago + "','" + importe + "','" + periodo + "')";
                        con.EjecutarABM(sentencia, false);

                        //Registra los conceptos del pago 
                        int idultimopago = 0;
                        int idconcepto;
                        int numcuotas;
                        String meses;
                        double monto;
                        int cantAlumnos;

                        //Obtener el id de pago
                        con = con.ObtenerRSSentencia("SELECT MAX(pag_codigo) AS ultimoid FROM pago");
                        while (con.getResultSet().next()) {
                            idultimopago = con.getResultSet().getInt("ultimoid");
                        }
                        con.DesconectarBasedeDatos();

                        int cantfila = tbConceptoAPagar.getRowCount();
                        for (int fila = 0; fila < cantfila; fila++) {
                            idconcepto = Integer.parseInt(tbConceptoAPagar.getValueAt(fila, 0) + "");
                            numcuotas = Integer.parseInt(tbConceptoAPagar.getValueAt(fila, 3) + "");
                            meses = tbConceptoAPagar.getValueAt(fila, 4) + "";
                            monto = metodostxt.StringAFormatoAmericano(tbConceptoAPagar.getValueAt(fila, 5) + "");
                            cantAlumnos = Integer.parseInt(tbConceptoAPagar.getValueAt(fila, 7) + "");

                            sentencia = "INSERT INTO pago_concepto VALUES (pagcon_codigo, '" + idultimopago + "', '" + idconcepto 
                                    + "', '" + numcuotas + "', '" + meses + "', '" + monto + "', '" + cantAlumnos + "')";
                            
                            con.EjecutarABM(sentencia, false);
                        }
                        Toolkit.getDefaultToolkit().beep(); //BEEP
                        JOptionPane.showMessageDialog(this, "Se agregó correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);

                        ImprimirReciboPago();
                        Limpiar();
                        GenerarNumpago();
                    } catch (HeadlessException ex) {
                        JOptionPane.showMessageDialog(this, "Ocurrió un Error " + ex.getMessage());
                        Logger.getLogger(RegistrarPago.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (HeadlessException ex) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Completar los campos obligarios marcados con * ", "Advertencia", JOptionPane.WARNING_MESSAGE);
            System.out.println("Completar los campos obligarios marcados con * " + ex);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void ImprimirReciboPago() {
        //Imprimir recibo
        int confirmado2 = JOptionPane.showConfirmDialog(this, "¿Quieres imprimir el recibo de pago?", "Confirmación", JOptionPane.YES_OPTION);
        if (JOptionPane.YES_OPTION == confirmado2) {
            //Se suma los subtotales
            double total = 0.0;
            double subtotal;
            for (int i = 0; i < tbConceptoAPagar.getRowCount(); i++) {
                subtotal = metodostxt.StringAFormatoAmericano(tbConceptoAPagar.getValueAt(i, 6) + "");
                total = total + subtotal;
            }
            String totalString = metodostxt.DoubleAFormatSudamerica(total);
            InputStream logo = this.getClass().getResourceAsStream("/reportes/images/logo_ace.jpg");
            InputStream logo2 = this.getClass().getResourceAsStream("/reportes/images/logo_ace.jpg");
            //Parametros
            Map parametros = new HashMap();
            parametros.clear();
            parametros.put("NUMPAGO", lblNumPago.getText());
            parametros.put("NUMPAGO2", lblNumPago.getText());
            parametros.put("LOGO", logo);
            parametros.put("LOGO2", logo2);
            parametros.put("APODERADO", cbApoderado.getSelectedItem() + "");
            parametros.put("APODERADO2", cbApoderado.getSelectedItem() + "");

            /*try {//Buscar nombre y apellido de usuario registrado
                con = con.ObtenerRSSentencia("SELECT CONCAT(usu_nombre,' ', usu_apellido) AS nomapeusu FROM usuario WHERE usu_alias = '" + Alias + "'");
                if (con.rs.next()) {

                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.DesconectarBasedeDatos();
            parametros.put("USUARIO", "ACES (Cooperadora Escolar de Padres)");
            parametros.put("USUARIO2", "ACES (Cooperadora Escolar de Padres)");*/
            parametros.put("NIVEL_BASICO", lblPoderantesBasico.getText());
            parametros.put("NIVEL_BASICO2", lblPoderantesBasico.getText());
            parametros.put("NIVEL_MEDIO", lblPoderantesMedio.getText());
            parametros.put("NIVEL_MEDIO2", lblPoderantesMedio.getText());
            parametros.put("CEDULA", txtCedulaApoderado.getText());
            parametros.put("CEDULA2", txtCedulaApoderado.getText());
            parametros.put("TOTAL", totalString);
            parametros.put("TOTAL2", totalString);
            JRDataSource datasource = new JRTableModelDataSource(tbConceptoAPagar.getModel());
            parametros.put("DATASOURCE", datasource);
            JRDataSource datasource2 = new JRTableModelDataSource(tbConceptoAPagar.getModel());
            parametros.put("DATASOURCE2", datasource2);
            //Enviar directorio del subreporte
            String directoriosub = this.getClass().getResource("/reportes/recibo_pago/reporte_recibo_pago.jasper").toString();
            directoriosub = directoriosub.replaceAll("reporte_recibo_pago.jasper", "");
            parametros.put("SUBREPORT_DIR", directoriosub); //Direccion del subreporte

            String tipohoja = "";
            try {
                con = con.ObtenerRSSentencia("SELECT conf_codigo, conf_descripcion, conf_valor FROM configuracion WHERE conf_descripcion = 'TIPOHOJA'");
                while (con.getResultSet().next()) {
                    switch (con.getResultSet().getString("conf_descripcion")) {
                        case "TIPOHOJA":
                            tipohoja = con.getResultSet().getString("conf_valor");
                            break;
                        default:
                            JOptionPane.showMessageDialog(this, "No coincide con switch al buscar tipo de hoja", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.DesconectarBasedeDatos();

            System.out.println("tipohoja " + tipohoja);
            String rutajasper = "/reportes/recibo_pago/reporte_recibo_principal_" + tipohoja.toLowerCase() + ".jasper";

            metodos.GenerarReporteJTABLE(rutajasper, parametros, null);
        }
    }

    private void Limpiar() {
        cbApoderado.setSelectedIndex(-1);
        txtCedulaApoderado.setText("");
        dcFechaPago.setDate(new Date());
        modelTableConceptoAPagar.setRowCount(0);
        btnAgregar.setEnabled(false);
        btnEliminar.setEnabled(false);
        txtImporteRecibido.setEnabled(false);
        txtImporteRecibido.setText("");
        txtImporteRecibido.setForeground(Color.BLACK);
        txtVuelto.setText("0");
        txtTotalAPagar.setText("0");
        tbAllConceptos.clearSelection();
        for (int f = 0; f < tbAllConceptos.getRowCount(); f++) { //Vaciar num cuotas pagados
            tbAllConceptos.setValueAt("", f, 6);
        }

        modelTablePoderantes = (DefaultTableModel) tbPoderantes.getModel();
        modelTablePoderantes.setRowCount(0);
    }

    private boolean ComprobarCampos() {
        if (cbApoderado.getSelectedIndex() == -1) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Seleccione el apoderado", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbApoderado.requestFocus();
            return false;
        }

        if (dcFechaPago.getDate() == null) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Complete la fecha de pago", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dcFechaPago.requestFocus();
            return false;
        }

        int cantidadpagos = tbConceptoAPagar.getModel().getRowCount();
        if (cantidadpagos <= 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "No se cargó ningún pago", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (txtImporteRecibido.getText().equals("")) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Ingrese el importe", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtImporteRecibido.requestFocus();
            return false;
        }

        double importe = metodostxt.StringAFormatoAmericano(txtImporteRecibido.getText());
        double totalventa = metodostxt.StringAFormatoAmericano(txtTotalAPagar.getText());
        if (totalventa > importe || txtImporteRecibido.getText().equals("")) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "El importe debe ser mayor al total del pago", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtImporteRecibido.requestFocus();
            return false;
        }

        return true;
    }

    private void TablaAllApoderado() {//Realiza la consulta de los productos que tenemos en la base de datos
        modelTableApoderados = (DefaultTableModel) tbApoderado.getModel();
        modelTableApoderados.setRowCount(0);
        try {
            String sentencia = "CALL SP_ApoderadoConsulta()";
            con = con.ObtenerRSSentencia(sentencia);
            int codigo;
            String cedula, nombre, apellido, sexo, direccion, telefono, email, obs;
            while (con.getResultSet().next()) {
                codigo = con.getResultSet().getInt("apo_codigo");
                cedula = con.getResultSet().getString("apo_cedula");
                if (cedula == null) {
                    cedula = "0";
                }
                nombre = con.getResultSet().getString("apo_nombre");
                apellido = con.getResultSet().getString("apo_apellido");
                sexo = con.getResultSet().getString("apo_sexo");
                direccion = con.getResultSet().getString("apo_direccion");
                telefono = con.getResultSet().getString("apo_telefono");
                email = con.getResultSet().getString("apo_email");
                obs = con.getResultSet().getString("apo_obs");

                modelTableApoderados.addRow(new Object[]{codigo, cedula, nombre, apellido, sexo, direccion, telefono, email, obs});
            }
            tbApoderado.setModel(modelTableApoderados);
            metodos.AnchuraColumna(tbApoderado);
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();

        if (tbApoderado.getModel().getRowCount() == 1) {
            lbCantRegistrosApoderado.setText(tbApoderado.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistrosApoderado.setText(tbApoderado.getModel().getRowCount() + " Registros encontrados");
        }
    }

    private void GenerarNumpago() {
        try {
            con = con.ObtenerRSSentencia("SELECT MAX(pag_numpago) AS numultimopago FROM pago");
            String numultimapago = null;
            while (con.getResultSet().next()) {
                numultimapago = con.getResultSet().getString("numultimopago");
            }

            if (numultimapago == null) {
                numultimapago = String.format("%8s", String.valueOf(1)).replace(' ', '0');
            } else {
                numultimapago = String.format("%8s", String.valueOf((Integer.parseInt(numultimapago) + 1))).replace(' ', '0');
            }
            lblNumPago.setText(numultimapago);

        } catch (SQLException e) {
            Logger.getLogger(RegistrarPago.class
                    .getName()).log(Level.SEVERE, null, e);
        }
        con.DesconectarBasedeDatos();
    }

//--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AgregarPago = new javax.swing.JDialog();
        panel3 = new org.edisoncor.gui.panel.Panel();
        lbl4 = new javax.swing.JLabel();
        txtNumCuotasAPagar = new javax.swing.JTextField();
        pnCuotasPagadas = new org.edisoncor.gui.panel.Panel();
        lbl5 = new javax.swing.JLabel();
        lblNumCuotasPagados = new javax.swing.JLabel();
        lblCedula6 = new javax.swing.JLabel();
        lblNumTotalCuotas = new javax.swing.JLabel();
        pnMesesPagados = new org.edisoncor.gui.panel.Panel();
        lblEne2 = new javax.swing.JLabel();
        lblFeb2 = new javax.swing.JLabel();
        lblMar2 = new javax.swing.JLabel();
        lblAbr2 = new javax.swing.JLabel();
        lblMay2 = new javax.swing.JLabel();
        lblJun2 = new javax.swing.JLabel();
        lblJul2 = new javax.swing.JLabel();
        lblAgo2 = new javax.swing.JLabel();
        lblSep2 = new javax.swing.JLabel();
        lblOct2 = new javax.swing.JLabel();
        lblNov2 = new javax.swing.JLabel();
        lblDic2 = new javax.swing.JLabel();
        lblCancelado = new javax.swing.JLabel();
        btnAgregar2 = new org.edisoncor.gui.button.ButtonSeven();
        buttonSeven2 = new org.edisoncor.gui.button.ButtonSeven();
        pnCuotasAPagar = new org.edisoncor.gui.panel.Panel();
        lbl3 = new javax.swing.JLabel();
        pnMesesAPagar = new org.edisoncor.gui.panel.Panel();
        lblEne = new javax.swing.JLabel();
        lblFeb = new javax.swing.JLabel();
        lblMar = new javax.swing.JLabel();
        lblAbr = new javax.swing.JLabel();
        lblMay = new javax.swing.JLabel();
        lblJun = new javax.swing.JLabel();
        lblJul = new javax.swing.JLabel();
        lblAgo = new javax.swing.JLabel();
        lblSep = new javax.swing.JLabel();
        lblOct = new javax.swing.JLabel();
        lblNov = new javax.swing.JLabel();
        lblDic = new javax.swing.JLabel();
        lbl10 = new javax.swing.JLabel();
        txtSubtotal = new javax.swing.JTextField();
        lbl8 = new javax.swing.JLabel();
        lbl7 = new javax.swing.JLabel();
        txtConcepto = new javax.swing.JTextField();
        lblImporte = new javax.swing.JLabel();
        txtImporte = new javax.swing.JTextField();
        lblCedula10 = new javax.swing.JLabel();
        panel4 = new org.edisoncor.gui.panel.Panel();
        lblPoderantesBasico3 = new javax.swing.JLabel();
        lblPoderantesBasico2 = new javax.swing.JLabel();
        lblPoderantesBasico = new javax.swing.JLabel();
        lblPoderantesMedio = new javax.swing.JLabel();
        lblPoderantesMedio2 = new javax.swing.JLabel();
        lbl6 = new javax.swing.JLabel();
        txtCantAlumnosPaganCuota = new javax.swing.JTextField();
        BuscadorApoderado = new javax.swing.JDialog();
        panel6 = new org.edisoncor.gui.panel.Panel();
        jLabel12 = new javax.swing.JLabel();
        txtBuscarApoderado = new javax.swing.JTextField();
        lblBuscarCampoApoderado = new javax.swing.JLabel();
        cbCampoBuscarApoderado = new javax.swing.JComboBox();
        scApoderado = new javax.swing.JScrollPane();
        tbApoderado = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lbCantRegistrosApoderado = new javax.swing.JLabel();
        jpPrincipal = new javax.swing.JPanel();
        jpBotones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jpDatosVenta = new javax.swing.JPanel();
        lblApoderado = new javax.swing.JLabel();
        cbApoderado = new javax.swing.JComboBox<>();
        btnBuscarApoderado = new javax.swing.JButton();
        lblFechaPago = new javax.swing.JLabel();
        dcFechaPago = new com.toedter.calendar.JDateChooser();
        txtCedulaApoderado = new javax.swing.JTextField();
        lblCedulaApoderado = new javax.swing.JLabel();
        scAllConcepto1 = new javax.swing.JScrollPane();
        tbPoderantes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lblPoderantes = new javax.swing.JLabel();
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        labelMetric1 = new org.edisoncor.gui.label.LabelMetric();
        lblNumPago = new org.edisoncor.gui.label.LabelMetric();
        jPanel3 = new javax.swing.JPanel();
        lblTituloTotalCompra1 = new javax.swing.JLabel();
        txtImporteRecibido = new javax.swing.JTextField();
        txtVuelto = new javax.swing.JTextField();
        lblTituloTotalCompra2 = new javax.swing.JLabel();
        lblTotalMoneda = new javax.swing.JLabel();
        txtTotalAPagar = new javax.swing.JTextField();
        lblTituloTotalCompra = new javax.swing.JLabel();
        lblTotalMoneda1 = new javax.swing.JLabel();
        lblTotalMoneda2 = new javax.swing.JLabel();
        panel1 = new org.edisoncor.gui.panel.Panel();
        jLabel10 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        jLabel11 = new javax.swing.JLabel();
        scConceptoAPagar = new javax.swing.JScrollPane();
        tbConceptoAPagar = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        btnAgregar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        scAllConcepto = new javax.swing.JScrollPane();
        tbAllConceptos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }

        };
        panel7 = new org.edisoncor.gui.panel.Panel();
        jLabel1 = new javax.swing.JLabel();
        lblPeriodoActual = new javax.swing.JLabel();

        AgregarPago.setTitle("Agregar pago");
        AgregarPago.setLocation(new java.awt.Point(0, 0));
        AgregarPago.setModal(true);
        AgregarPago.setResizable(false);
        AgregarPago.setSize(new java.awt.Dimension(700, 382));
        AgregarPago.setType(java.awt.Window.Type.POPUP);

        panel3.setPreferredSize(new java.awt.Dimension(700, 382));
        panel3.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                panel3KeyReleased(evt);
            }
        });

        lbl4.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lbl4.setForeground(new java.awt.Color(255, 255, 255));
        lbl4.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl4.setText("N° de cuotas a pagar:");

        txtNumCuotasAPagar.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtNumCuotasAPagar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtNumCuotasAPagar.setToolTipText("");
        txtNumCuotasAPagar.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNumCuotasAPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtNumCuotasAPagarActionPerformed(evt);
            }
        });
        txtNumCuotasAPagar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNumCuotasAPagarKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNumCuotasAPagarKeyTyped(evt);
            }
        });

        pnCuotasPagadas.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl5.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lbl5.setForeground(new java.awt.Color(255, 255, 255));
        lbl5.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl5.setText("N° de cuotas ya pagados:");

        lblNumCuotasPagados.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblNumCuotasPagados.setForeground(new java.awt.Color(255, 255, 255));
        lblNumCuotasPagados.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNumCuotasPagados.setText("0");

        lblCedula6.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCedula6.setForeground(new java.awt.Color(255, 255, 255));
        lblCedula6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCedula6.setText("de");

        lblNumTotalCuotas.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblNumTotalCuotas.setForeground(new java.awt.Color(255, 255, 255));
        lblNumTotalCuotas.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNumTotalCuotas.setText("0");

        lblEne2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblEne2.setForeground(new java.awt.Color(255, 255, 255));
        lblEne2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblEne2.setText("Ene");

        lblFeb2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblFeb2.setForeground(new java.awt.Color(255, 255, 255));
        lblFeb2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblFeb2.setText("Feb");

        lblMar2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblMar2.setForeground(new java.awt.Color(255, 255, 255));
        lblMar2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblMar2.setText("Mar");

        lblAbr2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblAbr2.setForeground(new java.awt.Color(255, 255, 255));
        lblAbr2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblAbr2.setText("Abr");

        lblMay2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblMay2.setForeground(new java.awt.Color(255, 255, 255));
        lblMay2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblMay2.setText("May");

        lblJun2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblJun2.setForeground(new java.awt.Color(255, 255, 255));
        lblJun2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblJun2.setText("Jun");

        lblJul2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblJul2.setForeground(new java.awt.Color(255, 255, 255));
        lblJul2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblJul2.setText("Jul");

        lblAgo2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblAgo2.setForeground(new java.awt.Color(255, 255, 255));
        lblAgo2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblAgo2.setText("Ago");

        lblSep2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblSep2.setForeground(new java.awt.Color(255, 255, 255));
        lblSep2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblSep2.setText("Sep");

        lblOct2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblOct2.setForeground(new java.awt.Color(255, 255, 255));
        lblOct2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblOct2.setText("Oct");

        lblNov2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblNov2.setForeground(new java.awt.Color(255, 255, 255));
        lblNov2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblNov2.setText("Nov");

        lblDic2.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblDic2.setForeground(new java.awt.Color(255, 255, 255));
        lblDic2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblDic2.setText("Dic");

        javax.swing.GroupLayout pnMesesPagadosLayout = new javax.swing.GroupLayout(pnMesesPagados);
        pnMesesPagados.setLayout(pnMesesPagadosLayout);
        pnMesesPagadosLayout.setHorizontalGroup(
            pnMesesPagadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnMesesPagadosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblEne2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFeb2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMar2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAbr2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMay2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblJun2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblJul2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAgo2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSep2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblOct2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNov2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDic2)
                .addGap(36, 36, 36))
        );
        pnMesesPagadosLayout.setVerticalGroup(
            pnMesesPagadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnMesesPagadosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(pnMesesPagadosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblEne2)
                    .addComponent(lblFeb2)
                    .addComponent(lblMar2)
                    .addComponent(lblAbr2)
                    .addComponent(lblMay2)
                    .addComponent(lblJun2)
                    .addComponent(lblJul2)
                    .addComponent(lblAgo2)
                    .addComponent(lblSep2)
                    .addComponent(lblOct2)
                    .addComponent(lblNov2)
                    .addComponent(lblDic2))
                .addContainerGap())
        );

        lblCancelado.setFont(new java.awt.Font("SansSerif", 1, 16)); // NOI18N
        lblCancelado.setForeground(new java.awt.Color(0, 255, 51));
        lblCancelado.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCancelado.setText("CANCELADO");

        javax.swing.GroupLayout pnCuotasPagadasLayout = new javax.swing.GroupLayout(pnCuotasPagadas);
        pnCuotasPagadas.setLayout(pnCuotasPagadasLayout);
        pnCuotasPagadasLayout.setHorizontalGroup(
            pnCuotasPagadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnMesesPagados, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnCuotasPagadasLayout.createSequentialGroup()
                .addGap(41, 41, 41)
                .addComponent(lbl5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNumCuotasPagados)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCedula6)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNumTotalCuotas, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblCancelado)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnCuotasPagadasLayout.setVerticalGroup(
            pnCuotasPagadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnCuotasPagadasLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnCuotasPagadasLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl5)
                    .addComponent(lblNumCuotasPagados)
                    .addComponent(lblCedula6)
                    .addComponent(lblNumTotalCuotas)
                    .addComponent(lblCancelado))
                .addGap(2, 2, 2)
                .addComponent(pnMesesPagados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnAgregar2.setBackground(new java.awt.Color(0, 153, 153));
        btnAgregar2.setText("Agregar");
        btnAgregar2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregar2ActionPerformed(evt);
            }
        });

        buttonSeven2.setBackground(new java.awt.Color(204, 0, 51));
        buttonSeven2.setText("Cancelar");
        buttonSeven2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSeven2ActionPerformed(evt);
            }
        });

        pnCuotasAPagar.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lbl3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lbl3.setForeground(new java.awt.Color(255, 255, 255));
        lbl3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl3.setText("Cuotas a pagar");

        lblEne.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblEne.setForeground(new java.awt.Color(255, 255, 255));
        lblEne.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblEne.setText("Ene");

        lblFeb.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblFeb.setForeground(new java.awt.Color(255, 255, 255));
        lblFeb.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblFeb.setText("Feb");

        lblMar.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblMar.setForeground(new java.awt.Color(255, 255, 255));
        lblMar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblMar.setText("Mar");

        lblAbr.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblAbr.setForeground(new java.awt.Color(255, 255, 255));
        lblAbr.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblAbr.setText("Abr");

        lblMay.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblMay.setForeground(new java.awt.Color(255, 255, 255));
        lblMay.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblMay.setText("May");

        lblJun.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblJun.setForeground(new java.awt.Color(255, 255, 255));
        lblJun.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblJun.setText("Jun");

        lblJul.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblJul.setForeground(new java.awt.Color(255, 255, 255));
        lblJul.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblJul.setText("Jul");

        lblAgo.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblAgo.setForeground(new java.awt.Color(255, 255, 255));
        lblAgo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblAgo.setText("Ago");

        lblSep.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblSep.setForeground(new java.awt.Color(255, 255, 255));
        lblSep.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblSep.setText("Sep");

        lblOct.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblOct.setForeground(new java.awt.Color(255, 255, 255));
        lblOct.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblOct.setText("Oct");

        lblNov.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblNov.setForeground(new java.awt.Color(255, 255, 255));
        lblNov.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblNov.setText("Nov");

        lblDic.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        lblDic.setForeground(new java.awt.Color(255, 255, 255));
        lblDic.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"))); // NOI18N
        lblDic.setText("Dic");

        javax.swing.GroupLayout pnMesesAPagarLayout = new javax.swing.GroupLayout(pnMesesAPagar);
        pnMesesAPagar.setLayout(pnMesesAPagarLayout);
        pnMesesAPagarLayout.setHorizontalGroup(
            pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnMesesAPagarLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblEne)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFeb)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAbr)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblMay)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblJun)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblJul)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblAgo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblSep)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblOct)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNov)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblDic)
                .addGap(37, 37, 37))
        );
        pnMesesAPagarLayout.setVerticalGroup(
            pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMesesAPagarLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblSep)
                        .addComponent(lblOct)
                        .addComponent(lblNov)
                        .addComponent(lblDic))
                    .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblMay)
                        .addComponent(lblJun)
                        .addComponent(lblJul)
                        .addComponent(lblAgo))
                    .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblMar)
                        .addComponent(lblAbr))
                    .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblEne)
                        .addComponent(lblFeb)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnCuotasAPagarLayout = new javax.swing.GroupLayout(pnCuotasAPagar);
        pnCuotasAPagar.setLayout(pnCuotasAPagarLayout);
        pnCuotasAPagarLayout.setHorizontalGroup(
            pnCuotasAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnMesesAPagar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnCuotasAPagarLayout.createSequentialGroup()
                .addGap(39, 39, 39)
                .addComponent(lbl3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnCuotasAPagarLayout.setVerticalGroup(
            pnCuotasAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnCuotasAPagarLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lbl3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(pnMesesAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(125, 125, 125))
        );

        lbl10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lbl10.setForeground(new java.awt.Color(255, 255, 255));
        lbl10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl10.setText("Subtotal:");

        txtSubtotal.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtSubtotal.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSubtotal.setText("0");
        txtSubtotal.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSubtotal.setEnabled(false);
        txtSubtotal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSubtotalKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSubtotalKeyTyped(evt);
            }
        });

        lbl8.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lbl8.setForeground(new java.awt.Color(255, 255, 255));
        lbl8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbl8.setText("Guaranies");

        lbl7.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lbl7.setForeground(new java.awt.Color(255, 255, 255));
        lbl7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl7.setText("Concepto:");

        txtConcepto.setEditable(false);
        txtConcepto.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtConcepto.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtConcepto.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtConcepto.setEnabled(false);
        txtConcepto.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtConceptoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtConceptoKeyTyped(evt);
            }
        });

        lblImporte.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblImporte.setForeground(new java.awt.Color(255, 255, 255));
        lblImporte.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblImporte.setText("Importe:");

        txtImporte.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtImporte.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtImporte.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtImporte.setEnabled(false);
        txtImporte.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtImporteKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtImporteKeyTyped(evt);
            }
        });

        lblCedula10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCedula10.setForeground(new java.awt.Color(255, 255, 255));
        lblCedula10.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCedula10.setText("Guaranies");

        panel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblPoderantesBasico3.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblPoderantesBasico3.setForeground(new java.awt.Color(255, 255, 255));
        lblPoderantesBasico3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPoderantesBasico3.setText("Alumnos a cargo:");

        lblPoderantesBasico2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        lblPoderantesBasico2.setForeground(new java.awt.Color(204, 204, 0));
        lblPoderantesBasico2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPoderantesBasico2.setText("En nivel básico:");

        lblPoderantesBasico.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        lblPoderantesBasico.setForeground(new java.awt.Color(255, 255, 255));
        lblPoderantesBasico.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPoderantesBasico.setText("NO");

        lblPoderantesMedio.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        lblPoderantesMedio.setForeground(new java.awt.Color(255, 255, 255));
        lblPoderantesMedio.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPoderantesMedio.setText("0");

        lblPoderantesMedio2.setFont(new java.awt.Font("SansSerif", 1, 11)); // NOI18N
        lblPoderantesMedio2.setForeground(new java.awt.Color(204, 204, 0));
        lblPoderantesMedio2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblPoderantesMedio2.setText("En nivel medio:");

        javax.swing.GroupLayout panel4Layout = new javax.swing.GroupLayout(panel4);
        panel4.setLayout(panel4Layout);
        panel4Layout.setHorizontalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(panel4Layout.createSequentialGroup()
                            .addComponent(lblPoderantesMedio2, javax.swing.GroupLayout.PREFERRED_SIZE, 84, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblPoderantesMedio, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGroup(panel4Layout.createSequentialGroup()
                            .addComponent(lblPoderantesBasico2)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                            .addComponent(lblPoderantesBasico, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(lblPoderantesBasico3))
                .addGap(111, 111, 111))
        );
        panel4Layout.setVerticalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblPoderantesBasico3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPoderantesBasico2)
                    .addComponent(lblPoderantesBasico))
                .addGap(2, 2, 2)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPoderantesMedio2)
                    .addComponent(lblPoderantesMedio))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        lbl6.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lbl6.setForeground(new java.awt.Color(255, 255, 255));
        lbl6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbl6.setText("Alumnos que pagan cuota:");

        txtCantAlumnosPaganCuota.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtCantAlumnosPaganCuota.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCantAlumnosPaganCuota.setText("0");
        txtCantAlumnosPaganCuota.setToolTipText("");
        txtCantAlumnosPaganCuota.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCantAlumnosPaganCuota.setEnabled(false);
        txtCantAlumnosPaganCuota.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCantAlumnosPaganCuotaActionPerformed(evt);
            }
        });
        txtCantAlumnosPaganCuota.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCantAlumnosPaganCuotaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCantAlumnosPaganCuotaKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel3Layout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addComponent(lbl7, javax.swing.GroupLayout.PREFERRED_SIZE, 77, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(txtConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 195, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(28, 28, 28)
                        .addComponent(lblImporte)
                        .addGap(2, 2, 2)
                        .addComponent(txtImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(lblCedula10)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel3Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(pnCuotasPagadas, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnCuotasAPagar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(panel3Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(panel3Layout.createSequentialGroup()
                                        .addComponent(lbl4)
                                        .addGap(2, 2, 2)
                                        .addComponent(txtNumCuotasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panel3Layout.createSequentialGroup()
                                        .addComponent(lbl6)
                                        .addGap(2, 2, 2)
                                        .addComponent(txtCantAlumnosPaganCuota, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(18, 18, 18)
                                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panel3Layout.createSequentialGroup()
                                        .addComponent(btnAgregar2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(18, 18, 18)
                                        .addComponent(buttonSeven2, javax.swing.GroupLayout.PREFERRED_SIZE, 87, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(panel3Layout.createSequentialGroup()
                                        .addComponent(lbl10)
                                        .addGap(2, 2, 2)
                                        .addComponent(txtSubtotal, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lbl8)
                                        .addGap(28, 28, 28)
                                        .addComponent(panel4, javax.swing.GroupLayout.PREFERRED_SIZE, 186, javax.swing.GroupLayout.PREFERRED_SIZE)))))))
                .addGap(43, 43, 43))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel3Layout.createSequentialGroup()
                .addContainerGap(18, Short.MAX_VALUE)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbl7)
                    .addComponent(txtConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblImporte)
                    .addComponent(txtImporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCedula10))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnCuotasAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 71, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(26, 26, 26)
                .addComponent(pnCuotasPagadas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl4)
                            .addComponent(txtNumCuotasAPagar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl10)
                            .addComponent(txtSubtotal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lbl8))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lbl6)
                            .addComponent(txtCantAlumnosPaganCuota, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel3Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(panel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(3, 3, 3)
                .addGroup(panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAgregar2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(buttonSeven2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(41, 41, 41))
        );

        javax.swing.GroupLayout AgregarPagoLayout = new javax.swing.GroupLayout(AgregarPago.getContentPane());
        AgregarPago.getContentPane().setLayout(AgregarPagoLayout);
        AgregarPagoLayout.setHorizontalGroup(
            AgregarPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel3, 715, 715, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        AgregarPagoLayout.setVerticalGroup(
            AgregarPagoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel3, 386, 386, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        BuscadorApoderado.setTitle("Buscador de apoderados");
        BuscadorApoderado.setModal(true);
        BuscadorApoderado.setSize(new java.awt.Dimension(760, 310));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoBuscar.png"))); // NOI18N
        jLabel12.setText("  BUSCAR ");

        txtBuscarApoderado.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        txtBuscarApoderado.setForeground(new java.awt.Color(0, 153, 153));
        txtBuscarApoderado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBuscarApoderado.setCaretColor(new java.awt.Color(0, 204, 204));
        txtBuscarApoderado.setDisabledTextColor(new java.awt.Color(0, 204, 204));
        txtBuscarApoderado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarApoderadoKeyReleased(evt);
            }
        });

        lblBuscarCampoApoderado.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampoApoderado.setForeground(new java.awt.Color(255, 255, 255));
        lblBuscarCampoApoderado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampoApoderado.setText("Buscar por:");

        scApoderado.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tbApoderado.setAutoCreateRowSorter(true);
        tbApoderado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbApoderado.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbApoderado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "N° de cedula", "Nombre", "Apellido", "Sexo", "Direccion", "Telefono", "Email", "Observacion"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbApoderado.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbApoderado.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbApoderado.setGridColor(new java.awt.Color(0, 153, 204));
        tbApoderado.setOpaque(false);
        tbApoderado.setRowHeight(20);
        tbApoderado.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbApoderado.getTableHeader().setReorderingAllowed(false);
        tbApoderado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbApoderadoMousePressed(evt);
            }
        });
        tbApoderado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbApoderadoKeyReleased(evt);
            }
        });
        scApoderado.setViewportView(tbApoderado);

        lbCantRegistrosApoderado.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lbCantRegistrosApoderado.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistrosApoderado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistrosApoderado.setText("0 Registros encontrados");
        lbCantRegistrosApoderado.setPreferredSize(new java.awt.Dimension(57, 25));

        javax.swing.GroupLayout panel6Layout = new javax.swing.GroupLayout(panel6);
        panel6.setLayout(panel6Layout);
        panel6Layout.setHorizontalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel6Layout.createSequentialGroup()
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(lbCantRegistrosApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel6Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(panel6Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(53, 53, 53)
                                .addComponent(lblBuscarCampoApoderado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbCampoBuscarApoderado, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(scApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 709, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(14, 14, 14))
        );
        panel6Layout.setVerticalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbCampoBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscarCampoApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(scApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(lbCantRegistrosApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout BuscadorApoderadoLayout = new javax.swing.GroupLayout(BuscadorApoderado.getContentPane());
        BuscadorApoderado.getContentPane().setLayout(BuscadorApoderadoLayout);
        BuscadorApoderadoLayout.setHorizontalGroup(
            BuscadorApoderadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        BuscadorApoderadoLayout.setVerticalGroup(
            BuscadorApoderadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ventana Registrar Pagos");
        setBackground(new java.awt.Color(45, 62, 80));
        setPreferredSize(new java.awt.Dimension(1260, 575));
        setResizable(false);
        setSize(new java.awt.Dimension(1260, 575));

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1260, 575));

        jpBotones.setBackground(new java.awt.Color(233, 255, 255));
        jpBotones.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()));
        jpBotones.setPreferredSize(new java.awt.Dimension(100, 50));

        btnGuardar.setBackground(new java.awt.Color(0, 153, 255));
        btnGuardar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoGuardar.png"))); // NOI18N
        btnGuardar.setText("Registrar pago");
        btnGuardar.setToolTipText("Inserta el nuevo registro");
        btnGuardar.setPreferredSize(new java.awt.Dimension(128, 36));
        btnGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuardarActionPerformed(evt);
            }
        });
        btnGuardar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                btnGuardarKeyPressed(evt);
            }
        });

        btnCancelar.setBackground(new java.awt.Color(255, 101, 101));
        btnCancelar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoCancelar.png"))); // NOI18N
        btnCancelar.setText("Limpiar campos");
        btnCancelar.setToolTipText("Cancela la acción");
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpBotonesLayout = new javax.swing.GroupLayout(jpBotones);
        jpBotones.setLayout(jpBotonesLayout);
        jpBotonesLayout.setHorizontalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancelar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jpDatosVenta.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosVenta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblApoderado.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblApoderado.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblApoderado.setText("Apoderado (Responsable)");
        lblApoderado.setToolTipText("");
        lblApoderado.setFocusable(false);

        cbApoderado.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        cbApoderado.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbApoderadoItemStateChanged(evt);
            }
        });

        btnBuscarApoderado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnBuscarApoderado.setToolTipText("Buscar cliente");
        btnBuscarApoderado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarApoderadoActionPerformed(evt);
            }
        });

        lblFechaPago.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaPago.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaPago.setText("Fecha de pago");
        lblFechaPago.setToolTipText("");
        lblFechaPago.setFocusable(false);

        dcFechaPago.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        dcFechaPago.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFechaPago.setMinSelectableDate(new java.util.Date(631162800000L));

        txtCedulaApoderado.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        txtCedulaApoderado.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtCedulaApoderado.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCedulaApoderado.setEnabled(false);
        txtCedulaApoderado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCedulaApoderadoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCedulaApoderadoKeyTyped(evt);
            }
        });

        lblCedulaApoderado.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCedulaApoderado.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCedulaApoderado.setText("N° de cédula");
        lblCedulaApoderado.setFocusable(false);

        tbPoderantes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbPoderantes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbPoderantes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre y Apellido", "N° de cédula", "Nivel", "CodNivel"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbPoderantes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbPoderantes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbPoderantes.setEnabled(false);
        tbPoderantes.setGridColor(new java.awt.Color(0, 153, 204));
        tbPoderantes.setOpaque(false);
        tbPoderantes.setRowHeight(20);
        tbPoderantes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbPoderantes.getTableHeader().setResizingAllowed(false);
        tbPoderantes.getTableHeader().setReorderingAllowed(false);
        tbPoderantes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbPoderantesMousePressed(evt);
            }
        });
        scAllConcepto1.setViewportView(tbPoderantes);

        lblPoderantes.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblPoderantes.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPoderantes.setText("Poderantes (Alumnos a cargo)");
        lblPoderantes.setFocusable(false);

        javax.swing.GroupLayout jpDatosVentaLayout = new javax.swing.GroupLayout(jpDatosVenta);
        jpDatosVenta.setLayout(jpDatosVentaLayout);
        jpDatosVentaLayout.setHorizontalGroup(
            jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosVentaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(jpDatosVentaLayout.createSequentialGroup()
                        .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 275, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpDatosVentaLayout.createSequentialGroup()
                        .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCedulaApoderado)
                            .addComponent(txtCedulaApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(30, 30, 30)
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPoderantes)
                    .addComponent(scAllConcepto1, javax.swing.GroupLayout.PREFERRED_SIZE, 696, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpDatosVentaLayout.setVerticalGroup(
            jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosVentaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosVentaLayout.createSequentialGroup()
                        .addComponent(lblPoderantes)
                        .addGap(2, 2, 2)
                        .addComponent(scAllConcepto1, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpDatosVentaLayout.createSequentialGroup()
                        .addComponent(lblApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(btnBuscarApoderado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cbApoderado))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblCedulaApoderado)
                            .addComponent(lblFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(txtCedulaApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel2.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel2.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REGISTRAR PAGOS");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFocusable(false);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        labelMetric1.setText("N° de pago:");
        labelMetric1.setDistanciaDeSombra(2);
        labelMetric1.setFocusable(false);

        lblNumPago.setText("00000000");
        lblNumPago.setFocusable(false);
        lblNumPago.setFont(new java.awt.Font("Arial", 1, 20)); // NOI18N

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelMetric1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblNumPago, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(50, 50, 50))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNumPago, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(labelMetric1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel3.setBackground(new java.awt.Color(233, 255, 255));
        jPanel3.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblTituloTotalCompra1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblTituloTotalCompra1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTituloTotalCompra1.setText("TOTAL EFECTIVO");
        lblTituloTotalCompra1.setFocusable(false);

        txtImporteRecibido.setFont(new java.awt.Font("sansserif", 1, 22)); // NOI18N
        txtImporteRecibido.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtImporteRecibido.setEnabled(false);
        txtImporteRecibido.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtImporteRecibidoActionPerformed(evt);
            }
        });
        txtImporteRecibido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtImporteRecibidoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtImporteRecibidoKeyTyped(evt);
            }
        });

        txtVuelto.setEditable(false);
        txtVuelto.setBackground(new java.awt.Color(0, 153, 153));
        txtVuelto.setFont(new java.awt.Font("sansserif", 1, 22)); // NOI18N
        txtVuelto.setForeground(new java.awt.Color(255, 255, 255));
        txtVuelto.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtVuelto.setText("0");
        txtVuelto.setDisabledTextColor(new java.awt.Color(0, 51, 153));
        txtVuelto.setFocusable(false);
        txtVuelto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtVueltoActionPerformed(evt);
            }
        });

        lblTituloTotalCompra2.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblTituloTotalCompra2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTituloTotalCompra2.setText("VUELTO");
        lblTituloTotalCompra2.setFocusable(false);

        lblTotalMoneda.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblTotalMoneda.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalMoneda.setText("Guaranies");
        lblTotalMoneda.setFocusable(false);
        lblTotalMoneda.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        txtTotalAPagar.setEditable(false);
        txtTotalAPagar.setBackground(new java.awt.Color(0, 0, 0));
        txtTotalAPagar.setFont(new java.awt.Font("sansserif", 1, 22)); // NOI18N
        txtTotalAPagar.setForeground(new java.awt.Color(0, 154, 0));
        txtTotalAPagar.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtTotalAPagar.setText("0");
        txtTotalAPagar.setFocusable(false);
        txtTotalAPagar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtTotalAPagarActionPerformed(evt);
            }
        });

        lblTituloTotalCompra.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblTituloTotalCompra.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTituloTotalCompra.setText("TOTAL A PAGAR");
        lblTituloTotalCompra.setFocusable(false);

        lblTotalMoneda1.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblTotalMoneda1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalMoneda1.setText("Guaranies");
        lblTotalMoneda1.setFocusable(false);
        lblTotalMoneda1.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        lblTotalMoneda2.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblTotalMoneda2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotalMoneda2.setText("Guaranies");
        lblTotalMoneda2.setFocusable(false);
        lblTotalMoneda2.setHorizontalTextPosition(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtImporteRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(lblTotalMoneda1, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblTituloTotalCompra1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloTotalCompra2)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(3, 3, 3)
                        .addComponent(lblTotalMoneda2, javax.swing.GroupLayout.PREFERRED_SIZE, 91, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTituloTotalCompra)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(txtTotalAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblTotalMoneda)))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblTituloTotalCompra1)
                    .addComponent(lblTituloTotalCompra2)
                    .addComponent(lblTituloTotalCompra))
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtImporteRecibido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalMoneda1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVuelto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalMoneda2, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTotalAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotalMoneda, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel1.setColorPrimario(new java.awt.Color(233, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(233, 255, 255));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel10.setText("Buscar por concepto");
        jLabel10.setFocusable(false);

        txtBuscar.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        txtBuscar.setForeground(new java.awt.Color(0, 153, 153));
        txtBuscar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBuscar.setCaretColor(new java.awt.Color(0, 204, 204));
        txtBuscar.setDisabledTextColor(new java.awt.Color(0, 204, 204));
        txtBuscar.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarKeyReleased(evt);
            }
        });

        jLabel11.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jLabel11.setText("Pagos a realizarse");
        jLabel11.setFocusable(false);

        tbConceptoAPagar.setAutoCreateRowSorter(true);
        tbConceptoAPagar.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbConceptoAPagar.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        tbConceptoAPagar.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Concepto", "Cuotas pagadas", "Cuotas a pagar", "Mes", "Importe", "Subtotal", "Alumnos que pagan cuota"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbConceptoAPagar.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbConceptoAPagar.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbConceptoAPagar.setGridColor(new java.awt.Color(0, 153, 204));
        tbConceptoAPagar.setOpaque(false);
        tbConceptoAPagar.setRowHeight(20);
        tbConceptoAPagar.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbConceptoAPagar.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbConceptoAPagar.getTableHeader().setReorderingAllowed(false);
        tbConceptoAPagar.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbConceptoAPagarMousePressed(evt);
            }
        });
        scConceptoAPagar.setViewportView(tbConceptoAPagar);

        btnAgregar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnAgregar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoNuevo.png"))); // NOI18N
        btnAgregar.setText("Agregar");
        btnAgregar.setEnabled(false);
        btnAgregar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarActionPerformed(evt);
            }
        });

        btnEliminar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoEliminar.png"))); // NOI18N
        btnEliminar.setText("Quitar");
        btnEliminar.setEnabled(false);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        tbAllConceptos.setAutoCreateRowSorter(true);
        tbAllConceptos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbAllConceptos.setFont(new java.awt.Font("Tahoma", 0, 11)); // NOI18N
        tbAllConceptos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Concepto", "Tipo de importe", "Importe", "N° de pagos", "Tipo de pago", "Cuotas pagadas"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Double.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbAllConceptos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbAllConceptos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbAllConceptos.setGridColor(new java.awt.Color(0, 153, 204));
        tbAllConceptos.setOpaque(false);
        tbAllConceptos.setRowHeight(20);
        tbAllConceptos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbAllConceptos.getTableHeader().setReorderingAllowed(false);
        tbAllConceptos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbAllConceptosMousePressed(evt);
            }
        });
        scAllConcepto.setViewportView(tbAllConceptos);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 154, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar))
                    .addComponent(scAllConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 547, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 97, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scConceptoAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 531, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(32, 32, 32))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10))
                .addGap(2, 2, 2)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(btnAgregar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scAllConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(scConceptoAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel7.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel7.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel7.setColorSecundario(new java.awt.Color(0, 140, 140));

        jLabel1.setFont(new java.awt.Font("Franklin Gothic Medium Cond", 1, 20)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("PERIODO ");
        jLabel1.setFocusable(false);

        lblPeriodoActual.setFont(new java.awt.Font("Nirmala UI", 1, 38)); // NOI18N
        lblPeriodoActual.setForeground(new java.awt.Color(255, 255, 255));
        lblPeriodoActual.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblPeriodoActual.setText("0000");
        lblPeriodoActual.setFocusable(false);

        javax.swing.GroupLayout panel7Layout = new javax.swing.GroupLayout(panel7);
        panel7.setLayout(panel7Layout);
        panel7Layout.setHorizontalGroup(
            panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel7Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addGroup(panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPeriodoActual, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(19, 19, 19))
        );
        panel7Layout.setVerticalGroup(
            panel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel7Layout.createSequentialGroup()
                .addGap(35, 35, 35)
                .addComponent(jLabel1)
                .addGap(2, 2, 2)
                .addComponent(lblPeriodoActual, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(35, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 413, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(400, 400, 400))
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jpDatosVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(9, 9, 9)
                        .addComponent(panel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(panel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 1205, Short.MAX_VALUE)
                        .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpDatosVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(50, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        getAccessibleContext().setAccessibleName("RegistrarPago");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


    private void btnAgregarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarActionPerformed
        if (ComprobarAgregar() == true) {
            BuscarCantBasicoyMedio();
            CargarDatosaAgregarPagos();

            //Tipo de importe
            String tipoimporte = tbAllConceptos.getValueAt(tbAllConceptos.getSelectedRow(), 2) + "";
            switch (tipoimporte) {
                case "FIJO" ->
                    txtImporte.setEnabled(false);

                case "VARIABLE" ->
                    txtImporte.setEnabled(true);
                default ->
                    System.out.println("No existe en el sistema el tipo de importe seleccionado " + tipoimporte);
            }

            //Tipo de pago
            String tipopago = tbAllConceptos.getValueAt(tbAllConceptos.getSelectedRow(), 5) + "";
            switch (tipopago) {
                case "UNICO" -> {
                    txtNumCuotasAPagar.setText("1");
                    txtNumCuotasAPagar.setEnabled(false);
                }
                case "MENSUAL" -> {
                    txtNumCuotasAPagar.setText("0");
                    txtNumCuotasAPagar.setEnabled(true);
                }
                default ->
                    System.out.println("No existe en el sistema el tipo de pago seleccionado" + tipopago);
            }

            //Si todas las cuotas ya fueron pagadas
            if (lblNumCuotasPagados.getText().equals(lblNumTotalCuotas.getText())) {
                txtNumCuotasAPagar.setEnabled(false);
                btnAgregar2.setEnabled(false);
                lblCancelado.setVisible(true);
                txtImporte.setEnabled(false);
                txtNumCuotasAPagar.setText("0");
                txtNumCuotasAPagar.setEnabled(false);
            } else {//Si aun no se cancelo las cuotas
                btnAgregar2.setEnabled(true);
                lblCancelado.setVisible(false);
            }

            IconMesesAPagar();
            AgregarPago.pack();
            AgregarPago.setLocationRelativeTo(this); //Centrar
            AgregarPago.setVisible(true);
        }
    }//GEN-LAST:event_btnAgregarActionPerformed

    private void BuscarCantBasicoyMedio() {
        //Verificar cantidad de poderantes del basico y medio
        String cantbasico = "NO";
        int cantmedio = 0;
        int codnivel;
        for (int i = 0; i < tbPoderantes.getRowCount(); i++) {
            codnivel = Integer.parseInt(tbPoderantes.getValueAt(i, 3) + "");
            con = con.ObtenerRSSentencia("SELECT niv_tipo FROM nivel WHERE niv_codigo='" + codnivel + "'");
            try {
                if (con.getResultSet().next()) {
                    if (con.getResultSet().getString("niv_tipo").equals("BÁSICO") && cantbasico.equals("NO")) {
                        cantbasico = "SI";
                    } else {
                        if (con.getResultSet().getString("niv_tipo").equals("MEDIO")) {
                            cantmedio = cantmedio + 1;
                        }
                    }
                }
                lblPoderantesBasico.setText(cantbasico + "");
                lblPoderantesMedio.setText(cantmedio + "");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.DesconectarBasedeDatos();
        }
    }

    private void CargarDatosaAgregarPagos() {
        //Cargar valores del concepto a ventana agregar pago
        txtConcepto.setText(tbAllConceptos.getValueAt(tbAllConceptos.getSelectedRow(), 1) + "");

        double importe = Double.parseDouble(tbAllConceptos.getValueAt(tbAllConceptos.getSelectedRow(), 3) + "");
        if (importe == 0) {
            txtImporte.setText("");
        } else {
            txtImporte.setText(metodostxt.DoubleAFormatSudamerica(importe));
        }

        lblNumTotalCuotas.setText(tbAllConceptos.getValueAt(tbAllConceptos.getSelectedRow(), 4) + "");
        txtSubtotal.setText("0");
        numactual = 0;

        try {
            int codconcepto = Integer.parseInt(tbAllConceptos.getValueAt(tbAllConceptos.getSelectedRow(), 0) + "");
            //Obtener numero de cuotas pagados
            int codapoderado = metodoscombo.ObtenerIDSelectCombo(cbApoderado);
            con = con.ObtenerRSSentencia("SELECT SUM(pagcon_numcuotas) AS sumanumcuotas "
                    + "FROM pago, pago_concepto "
                    + "WHERE pagcon_concepto = '" + codconcepto + "' AND pag_apoderado = '" + codapoderado + "' "
                    + "AND pag_periodo = '" + lblPeriodoActual.getText() + "' AND pag_codigo = pagcon_pago");
            while (con.getResultSet().next()) {
                lblNumCuotasPagados.setText(con.getResultSet().getInt("sumanumcuotas") + "");
            }
            //Obtener meses a pagar
            con = con.ObtenerRSSentencia("SELECT con_ene, con_feb, con_mar, con_abr, con_may, con_jun, con_jul, "
                    + "con_ago, con_sep, con_oct, con_nov, con_dic FROM concepto WHERE con_codigo = '" + codconcepto + "'");
            //Recorrer los meses
            int numcuotaspagados = Integer.parseInt(lblNumCuotasPagados.getText());
            Component[] compMesesAPagar = pnMesesAPagar.getComponents();
            Component[] compMesesPagados = pnMesesPagados.getComponents();
            ImageIcon iconoX = new ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"));
            ImageIcon iconoOK = new ImageIcon(getClass().getResource("/iconos/Iconos20x20/1.png"));
            JLabel labelactual;
            if (con.getResultSet().next()) {
                for (int i = 0; i < compMesesAPagar.length; i++) {
                    //Meses a pagar
                    if (compMesesAPagar[i] instanceof JLabel) { //Si es Jlabel
                        labelactual = ((JLabel) compMesesAPagar[i]);
                        labelactual.setIcon(new ImageIcon(getClass().getResource("/iconos/Iconos20x20/" + con.getResultSet().getInt(i + 1) + ".png")));
                        labelactual.setEnabled(con.getResultSet().getBoolean(i + 1));
                    }
                    //Meses pagados
                    if (compMesesPagados[i] instanceof JLabel) { //Si es Jlabel
                        labelactual = ((JLabel) compMesesPagados[i]);
                        labelactual.setEnabled(con.getResultSet().getBoolean(i + 1));
                        labelactual.setIcon(iconoX);
                        if (numcuotaspagados > 0) {
                            if (labelactual.isEnabled() && labelactual.getIcon().toString().equals(iconoX.toString())) {
                                labelactual.setIcon(iconoOK);
                                numcuotaspagados = numcuotaspagados - 1;
                            }
                        }
                    }
                }
            }
        } catch (SQLException e) {
            System.out.println("error rs ");
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    private boolean ComprobarAgregar() {
        //Verificar si se selecciono un apoderado
        if (cbApoderado.getSelectedIndex() == -1) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Seleccione el apoderado", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbApoderado.requestFocus();
            return false;
        }
        //SI EL APODERADO NO TIENE ALUMNOS A SU CARGO
        if (tbPoderantes.getRowCount() == 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "El Apoderado seleccionado no tiene ningún alumno a su cargo", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        //Si no se seleccionó ningun concepto
        if (tbAllConceptos.getSelectedRowCount() == 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Seleccione el concepto a agregar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtBuscar.requestFocus();
            return false;
        }
        //Ver si el concepto a agregar ya fue agregado
        String codconceptoselect = tbAllConceptos.getValueAt(tbAllConceptos.getSelectedRow(), 0) + "";
        String codagregado;
        for (int fila = 0; fila < tbConceptoAPagar.getRowCount(); fila++) {
            codagregado = tbConceptoAPagar.getValueAt(fila, 0) + "";
            if (codconceptoselect.equals(codagregado)) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Este pago ya fue agregado", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        //Comprobar si alumnos estan matriculados
        String nivel;
        for (int i = 0; i < tbPoderantes.getRowCount(); i++) {
            nivel = tbPoderantes.getValueAt(i, 2) + "";
            if (nivel.equals("NO MATRICULADO")) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Existe uno o más Poderantes (Alumnos a cargo) no matriculados, no se puede realizar el pago", "Advertencia", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }

        return true;
    }

    private void SumarSubtotal() {
        //Suma la colmna subtotal
        double sumarsubtotal = metodos.SumarColumnaDouble(tbConceptoAPagar, 6);
        sumarsubtotal = metodostxt.DoubleATresDecimales(sumarsubtotal);
        String sumarsubtotalString = metodostxt.DoubleAFormatSudamerica(sumarsubtotal);
        txtTotalAPagar.setText(sumarsubtotalString); //El 5 es la columna 5, comienza de 0
    }

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        tbAllConceptos.clearSelection();
        if (tbConceptoAPagar.getSelectedRowCount() > 0) {
            modelTableConceptoAPagar.removeRow(tbConceptoAPagar.getSelectedRow());
            SumarSubtotal();

            if (tbConceptoAPagar.getRowCount() <= 0) {
                txtImporteRecibido.setEnabled(false);
                txtImporteRecibido.setText("");
                txtVuelto.setText("");
                btnEliminar.setEnabled(false);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Seleccione el concepto a eliminar", "Advertencia", JOptionPane.WARNING_MESSAGE);
            btnEliminar.setEnabled(false);
            tbConceptoAPagar.requestFocus();
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnBuscarApoderadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarApoderadoActionPerformed
        TablaAllApoderado();
        txtBuscarApoderado.setText("");
        BuscadorApoderado.setLocationRelativeTo(this);
        BuscadorApoderado.setVisible(true);
    }//GEN-LAST:event_btnBuscarApoderadoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        RegistroNuevo();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed


    }//GEN-LAST:event_btnGuardarKeyPressed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        int confirmado = JOptionPane.showConfirmDialog(this, "¿Seguro que desea borrar todos los datos del pago actual?", "Confirmación", JOptionPane.YES_OPTION);
        if (JOptionPane.YES_OPTION == confirmado) {
            Limpiar();
        }
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtVueltoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtVueltoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtVueltoActionPerformed

    private void txtImporteRecibidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteRecibidoKeyTyped
        if (evt.getKeyCode() != (char) KeyEvent.VK_LEFT && evt.getKeyCode() != (char) KeyEvent.VK_RIGHT) { //Ignorar derecha e izquierda
            metodostxt.TxtCantidadCaracteresKeyTyped(txtImporteRecibido, 11);
            metodostxt.SoloNumeroDecimalKeyTyped(evt, txtImporteRecibido);
        }
    }//GEN-LAST:event_txtImporteRecibidoKeyTyped

    private void CalcularVuelto() {
        double importe = metodostxt.StringAFormatoAmericano(txtImporteRecibido.getText());
        double totalAPagar = metodostxt.StringAFormatoAmericano(txtTotalAPagar.getText());
        if (totalAPagar > importe) {
            txtImporteRecibido.setForeground(Color.RED);
            txtVuelto.setText("0");
        } else {
            txtImporteRecibido.setText(metodostxt.StringAFormatSudamericaKeyRelease(txtImporteRecibido.getText()));
            double vuelto = importe - totalAPagar;
            vuelto = metodostxt.DoubleATresDecimales(vuelto);
            txtVuelto.setText(metodostxt.DoubleAFormatSudamerica(vuelto));
            txtImporteRecibido.setForeground(new Color(0, 153, 51)); //Verde
        }
    }

    private void txtImporteRecibidoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteRecibidoKeyReleased
        if (evt.getKeyCode() != (char) KeyEvent.VK_LEFT && evt.getKeyCode() != (char) KeyEvent.VK_RIGHT) { //Ignorar derecha e izquierda
            if (txtImporteRecibido.getText().equals("") == false) {
                txtImporteRecibido.setText(metodostxt.StringAFormatSudamericaKeyRelease(txtImporteRecibido.getText()));
                CalcularVuelto();
            }
        }
    }//GEN-LAST:event_txtImporteRecibidoKeyReleased

    private void txtTotalAPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtTotalAPagarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtTotalAPagarActionPerformed

    private void txtImporteRecibidoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtImporteRecibidoActionPerformed
        btnGuardar.doClick();
    }//GEN-LAST:event_txtImporteRecibidoActionPerformed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        metodos.FiltroJTable(txtBuscar.getText(), 1, tbAllConceptos);
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void txtCedulaApoderadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaApoderadoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCedulaApoderadoKeyReleased

    private void txtCedulaApoderadoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaApoderadoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCedulaApoderadoKeyTyped

    private void cbApoderadoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbApoderadoItemStateChanged
        if (cbApoderado.getSelectedIndex() != -1) {
            modelTablePoderantes.setRowCount(0); //Vacia la tabla
            for (int f = 0; f < tbAllConceptos.getRowCount(); f++) { //Vaciar num cuotas pagados
                tbAllConceptos.setValueAt("0", f, 6);
            }

            con = con.ObtenerRSSentencia("SELECT CONCAT(alu_nombre,' ',alu_apellido) AS nomapealumno, alu_cedula, "
                    + "(CASE "
                    + "WHEN mat_alumno IS NULL THEN 'NO MATRICULADO' "
                    + "ELSE "
                    + "(CASE niv_seccion "
                    + "WHEN 'SIN ESPECIFICAR' THEN CONCAT(niv_descripcion,' ',niv_turno) "
                    + "ELSE CONCAT(niv_descripcion,' \\\"', niv_seccion,'\\\"', ' ',niv_turno,' (',mat_periodo,')') END) END) AS nivel, niv_codigo, apo_cedula "
                    + "FROM (alumno LEFT OUTER JOIN matricula ON alu_codigo=mat_alumno LEFT OUTER JOIN nivel ON mat_nivel=niv_codigo), apoderado "
                    + "WHERE (mat_alumno IS NULL OR mat_alumno=alu_codigo) AND (mat_nivel IS NULL OR mat_nivel=niv_codigo) AND alu_apoderado = apo_codigo "
                    + "AND alu_apoderado='" + metodoscombo.ObtenerIDSelectCombo(cbApoderado) + "' "
                    + "AND mat_periodo='" + Integer.parseInt(lblPeriodoActual.getText()) + "' "
                    + "ORDER BY alu_nombre");

            try {
                String nomapealumno, cedula, nivel, codnivel;
                while (con.getResultSet().next()) {
                    nomapealumno = con.getResultSet().getString("nomapealumno");
                    cedula = metodostxt.StringPuntosMiles(con.getResultSet().getString("alu_cedula"));
                    nivel = con.getResultSet().getString("nivel");
                    codnivel = con.getResultSet().getString("niv_codigo");
                    txtCedulaApoderado.setText(metodostxt.StringPuntosMiles(con.getResultSet().getString("apo_cedula")));

                    modelTablePoderantes.addRow(new Object[]{nomapealumno, cedula, nivel, codnivel});
                }
                tbPoderantes.setModel(modelTablePoderantes);
                metodos.OcultarColumna(tbPoderantes, 3); //Ocultar columna

                //Colorear
                tbPoderantes.setDefaultRenderer(Object.class, new ColorearCelda());
                tbAllConceptos.setDefaultRenderer(Object.class, new ColorearCelda());
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.DesconectarBasedeDatos();

            //Obtener pagos ya realizados
            try {
                con = con.ObtenerRSSentencia(" SELECT pagcon_concepto, SUM(pagcon_numcuotas) AS sumnumcuotas\n"
                        + " FROM pago, pago_concepto\n"
                        + " WHERE pag_codigo=pagcon_pago AND pag_apoderado = '" + metodoscombo.ObtenerIDSelectCombo(cbApoderado) + "'\n"
                          + " AND pag_periodo = '" + lblPeriodoActual.getText() + "'\n"
                        + " GROUP BY pagcon_concepto");
                int idconcepto, sumnumcuotas, totalcuotas;
                while (con.getResultSet().next()) {
                    idconcepto = con.getResultSet().getInt("pagcon_concepto");
                    sumnumcuotas = con.getResultSet().getInt("sumnumcuotas");

                    for (int f = 0; f < tbAllConceptos.getRowCount(); f++) {
                        if (tbAllConceptos.getValueAt(f, 0).equals(idconcepto)) {
                            totalcuotas = (int) tbAllConceptos.getValueAt(f, 4);
                            if (sumnumcuotas == totalcuotas) {
                                tbAllConceptos.setValueAt(sumnumcuotas + " CANCELADO", f, 6);
                            } else {
                                tbAllConceptos.setValueAt(sumnumcuotas, f, 6);
                            }
                            f = tbAllConceptos.getRowCount();
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            con.DesconectarBasedeDatos();
        }
    }//GEN-LAST:event_cbApoderadoItemStateChanged

    private void tbAllConceptosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAllConceptosMousePressed
        if (tbAllConceptos.isEnabled()) {
            btnAgregar.setEnabled(true);
            btnEliminar.setEnabled(false);
        }
    }//GEN-LAST:event_tbAllConceptosMousePressed

    private void tbConceptoAPagarMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbConceptoAPagarMousePressed
        if (tbConceptoAPagar.isEnabled()) {
            btnAgregar.setEnabled(false);
            btnEliminar.setEnabled(true);
        }
    }//GEN-LAST:event_tbConceptoAPagarMousePressed

    private void txtConceptoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtConceptoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtConceptoKeyReleased

    private void txtConceptoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtConceptoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtConceptoKeyTyped

    int numactual = 0;
    private void txtNumCuotasAPagarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumCuotasAPagarKeyReleased
        IconMesesAPagar();
    }//GEN-LAST:event_txtNumCuotasAPagarKeyReleased

    private void IconMesesAPagar() {
        if (txtNumCuotasAPagar.getText().equals("") == false) {
            int numCuotasAPagar = Integer.parseInt(txtNumCuotasAPagar.getText());
            int numCuotasPagados = Integer.parseInt(lblNumCuotasPagados.getText());
            int numTotalCuotas = Integer.parseInt(lblNumTotalCuotas.getText());

            if (numCuotasAPagar > (numTotalCuotas - numCuotasPagados) && (numTotalCuotas - numCuotasPagados) != 0) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(AgregarPago, "El número de cuotas a pagar no puede ser mayor al número de cuotas faltantes", "Advertencia", JOptionPane.WARNING_MESSAGE);
                txtNumCuotasAPagar.setText("");
                txtSubtotal.setText("0");
                return;
            }
               
            txtCantAlumnosPaganCuota.setText(String.valueOf(sumarCantidadPoderantes()));
            
            double importe = metodostxt.StringAFormatoAmericano(txtImporte.getText());
            
            importe = conceptoPagoService.isConsideraCantidadAlumnos(tbAllConceptos.getValueAt(tbAllConceptos.getSelectedRow(), 0)+"") ? importe * sumarCantidadPoderantes() : importe;
            
            txtSubtotal.setText(metodostxt.DoubleAFormatSudamerica((numCuotasAPagar * importe)));
            if (numactual != Integer.parseInt(txtNumCuotasAPagar.getText())) { //Si el numero ingresado no es el mismo
                numactual = Integer.parseInt(txtNumCuotasAPagar.getText());
                //Recorrer los meses y poner checks sin pagar
                Component[] componentes = pnMesesPagados.getComponents();
                JLabel labelactual;
                ImageIcon iconoX = new ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"));
                ImageIcon iconoOkSinPagar = new ImageIcon(getClass().getResource("/iconos/Iconos20x20/3.png"));
                int numcuotasapagar = Integer.parseInt(txtNumCuotasAPagar.getText());
                for (int i = 0; i < componentes.length; i++) {
                    if (numcuotasapagar > 0 && componentes[i] instanceof JLabel) {//Si es Jlabel
                        labelactual = ((JLabel) componentes[i]);
                        if (labelactual.getIcon().toString().equals(iconoOkSinPagar.toString())) {
                            labelactual.setIcon(iconoX);
                        }
                        if (labelactual.isEnabled() && labelactual.getIcon().toString().equals(iconoX.toString())) {
                            labelactual.setIcon(iconoOkSinPagar);
                            numcuotasapagar = numcuotasapagar - 1;
                        }
                    }
                }
            }
        } else { //Si es vacio
            txtSubtotal.setText("0");
            numactual = 0;
            //Recorrer los meses y sacar checks sin pagar
            Component[] componentes = pnMesesPagados.getComponents();
            JLabel labelactual;
            ImageIcon iconoX = new ImageIcon(getClass().getResource("/iconos/Iconos20x20/0.png"));
            ImageIcon iconoOkSinPagar = new ImageIcon(getClass().getResource("/iconos/Iconos20x20/3.png"));
            for (int i = 0; i < componentes.length; i++) {
                if (componentes[i] instanceof JLabel) { //Si es Jlabel
                    labelactual = ((JLabel) componentes[i]);
                    if (labelactual.isEnabled() && labelactual.getIcon().toString().equals(iconoOkSinPagar.toString())) {
                        labelactual.setIcon(iconoX);
                    }
                }
            }
        }
    }

    private int sumarCantidadPoderantes(){
        //Sumar cantidad de poderantes basicos y medio
            int numPoderantes = 0;
            if (lblPoderantesBasico.getText().equals("SI")) {
                numPoderantes = numPoderantes + 1;
            }
            if (lblPoderantesMedio.getText().equals("0") == false) {
                numPoderantes = numPoderantes + Integer.parseInt(lblPoderantesMedio.getText());
            }
            
            return numPoderantes;
    }
    
    private void txtImporteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteKeyReleased

        txtImporte.setText(metodostxt.StringAFormatSudamericaKeyRelease(txtImporte.getText()));

        double importe = metodostxt.StringAFormatoAmericano(txtImporte.getText());
        int numcuotas = Integer.parseInt(txtNumCuotasAPagar.getText());

        double subtotal = importe * numcuotas;
        txtSubtotal.setText(metodostxt.DoubleAFormatSudamerica(subtotal));
    }//GEN-LAST:event_txtImporteKeyReleased

    private void txtImporteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteKeyTyped
        metodostxt.SoloNumeroDecimalKeyTyped(evt, txtImporte);
    }//GEN-LAST:event_txtImporteKeyTyped

    private void buttonSeven2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSeven2ActionPerformed
        AgregarPago.dispose();
    }//GEN-LAST:event_buttonSeven2ActionPerformed

    private void txtSubtotalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSubtotalKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSubtotalKeyReleased

    private void txtSubtotalKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSubtotalKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtSubtotalKeyTyped

    private void btnAgregar2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregar2ActionPerformed
        if (txtNumCuotasAPagar.getText().equals("") || txtNumCuotasAPagar.getText().equals("0")) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(AgregarPago, "El número de cuotas a pagar no puede ser vacío o 0", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtNumCuotasAPagar.requestFocus();
            return;
        }

        if (txtImporte.getText().equals("") || txtImporte.getText().equals("0")) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(AgregarPago, "El importe no puede ser vacío o 0", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtNumCuotasAPagar.requestFocus();
            return;
        }

        String codigo = tbAllConceptos.getValueAt(tbAllConceptos.getSelectedRow(), 0) + "";
        String concepto = txtConcepto.getText();
        int numcuotaspagadosyapagar = Integer.parseInt(lblNumCuotasPagados.getText()) + Integer.parseInt(txtNumCuotasAPagar.getText());
        String numcuotastotalpagados = numcuotaspagadosyapagar + " de " + lblNumTotalCuotas.getText();
        if (numcuotaspagadosyapagar == Integer.parseInt(lblNumTotalCuotas.getText())) {
            numcuotastotalpagados = numcuotastotalpagados + " CANCELADO";
        }

        //Meses
        Component[] comMesesPagados = pnMesesPagados.getComponents();
        ImageIcon iconoOkSinPagar = new ImageIcon(getClass().getResource("/iconos/Iconos20x20/3.png"));
        JLabel labelactual;
        //Obtener meses para poner en tabla
        String mes1 = "";
        String mes2 = "";
        for (int i = 0; i < comMesesPagados.length; i++) {
            if (comMesesPagados[i] instanceof JLabel) { //Si es Jlabel 
                labelactual = ((JLabel) comMesesPagados[i]);
                if (mes1.equals("") && labelactual.isEnabled() && labelactual.getIcon().toString().equals(iconoOkSinPagar.toString())) {
                    mes1 = labelactual.getText();
                } else {
                    if (mes1.equals("") == false && labelactual.isEnabled() && labelactual.getIcon().toString().equals(iconoOkSinPagar.toString())) {
                        mes2 = labelactual.getText();
                    }
                }
            }
        }
        String meses;

        if (mes2.equals("")) {
            meses = mes1;
        } else {
            meses = mes1 + " a " + mes2;
        }

        int numcuotas = Integer.parseInt(txtNumCuotasAPagar.getText());
        String importe = txtImporte.getText();
        String subtotal = txtSubtotal.getText();
        String cantAlumnosPaganCuota = txtCantAlumnosPaganCuota.getText();

        if (numcuotas <= 0) {
            JOptionPane.showMessageDialog(this, "El número de cuotas no puede ser menor o igual a 0", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtNumCuotasAPagar.requestFocus();
            return;
        }

        modelTableConceptoAPagar.addRow(new Object[]{codigo, concepto, numcuotastotalpagados, numcuotas, meses, importe, subtotal, cantAlumnosPaganCuota});
        tbConceptoAPagar.setModel(modelTableConceptoAPagar);
        metodos.AnchuraColumna(tbConceptoAPagar);

        SumarSubtotal();

        if (tbConceptoAPagar.getRowCount() > 0) {
            txtImporteRecibido.setEnabled(true);
        } else {
            txtImporteRecibido.setEnabled(false);
        }

        txtImporteRecibido.requestFocus();
        AgregarPago.dispose();
    }//GEN-LAST:event_btnAgregar2ActionPerformed

    private void txtNumCuotasAPagarKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNumCuotasAPagarKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtNumCuotasAPagarKeyTyped

    private void panel3KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_panel3KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_panel3KeyReleased

    private void txtNumCuotasAPagarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtNumCuotasAPagarActionPerformed
        btnAgregar2.doClick();
    }//GEN-LAST:event_txtNumCuotasAPagarActionPerformed

    private void txtBuscarApoderadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarApoderadoKeyReleased
        metodos.FiltroJTable(txtBuscarApoderado.getText(), cbCampoBuscarApoderado.getSelectedIndex(), tbApoderado);

        if (tbApoderado.getRowCount() == 1) {
            lbCantRegistrosApoderado.setText(tbApoderado.getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistrosApoderado.setText(tbApoderado.getRowCount() + " Registros encontrados");
        }
    }//GEN-LAST:event_txtBuscarApoderadoKeyReleased

    private void tbApoderadoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbApoderadoMousePressed
        if (evt.getClickCount() == 2) {
            int codselect = Integer.parseInt(tbApoderado.getValueAt(tbApoderado.getSelectedRow(), 0) + "");
            metodoscombo.SetSelectedCodigoItem(cbApoderado, codselect);
            BuscadorApoderado.dispose();
        }
    }//GEN-LAST:event_tbApoderadoMousePressed

    private void tbApoderadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbApoderadoKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            int codselect = Integer.parseInt(tbApoderado.getValueAt(tbApoderado.getSelectedRow(), 0) + "");
            metodoscombo.SetSelectedCodigoItem(cbApoderado, codselect);
            BuscadorApoderado.dispose();
        }
    }//GEN-LAST:event_tbApoderadoKeyReleased

    private void tbPoderantesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPoderantesMousePressed
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPoderantesMousePressed

    private void txtCantAlumnosPaganCuotaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCantAlumnosPaganCuotaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantAlumnosPaganCuotaActionPerformed

    private void txtCantAlumnosPaganCuotaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantAlumnosPaganCuotaKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantAlumnosPaganCuotaKeyReleased

    private void txtCantAlumnosPaganCuotaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCantAlumnosPaganCuotaKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCantAlumnosPaganCuotaKeyTyped

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(cbApoderado);
        ordenTabulador.add(dcFechaPago);
        ordenTabulador.add(txtImporteRecibido);
        ordenTabulador.add(btnGuardar);
        setFocusTraversalPolicy(new PersonalizadoFocusTraversalPolicy());

    }

    private class PersonalizadoFocusTraversalPolicy extends FocusTraversalPolicy {

        public Component getComponentAfter(Container focusCycleRoot, Component aComponent) {
            int currentPosition = ordenTabulador.indexOf(aComponent);
            currentPosition = (currentPosition + 1) % ordenTabulador.size();
            return (Component) ordenTabulador.get(currentPosition);
        }

        public Component getComponentBefore(Container focusCycleRoot, Component aComponent) {
            int currentPosition = ordenTabulador.indexOf(aComponent);
            currentPosition = (ordenTabulador.size() + currentPosition - 1) % ordenTabulador.size();
            return (Component) ordenTabulador.get(currentPosition);
        }

        public Component getFirstComponent(Container cntnr) {
            return (Component) ordenTabulador.get(0);
        }

        public Component getLastComponent(Container cntnr) {
            return (Component) ordenTabulador.get(ordenTabulador.size() - 1);
        }

        public Component getDefaultComponent(Container cntnr) {
            return (Component) ordenTabulador.get(0);
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JDialog AgregarPago;
    private javax.swing.JDialog BuscadorApoderado;
    private javax.swing.JButton btnAgregar;
    private org.edisoncor.gui.button.ButtonSeven btnAgregar2;
    private javax.swing.JButton btnBuscarApoderado;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private org.edisoncor.gui.button.ButtonSeven buttonSeven2;
    private static javax.swing.JComboBox<MetodosCombo> cbApoderado;
    private javax.swing.JComboBox cbCampoBuscarApoderado;
    private com.toedter.calendar.JDateChooser dcFechaPago;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpDatosVenta;
    private javax.swing.JPanel jpPrincipal;
    private org.edisoncor.gui.label.LabelMetric labelMetric1;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistrosApoderado;
    private javax.swing.JLabel lbl10;
    private javax.swing.JLabel lbl3;
    private javax.swing.JLabel lbl4;
    private javax.swing.JLabel lbl5;
    private javax.swing.JLabel lbl6;
    private javax.swing.JLabel lbl7;
    private javax.swing.JLabel lbl8;
    private javax.swing.JLabel lblAbr;
    private javax.swing.JLabel lblAbr2;
    private javax.swing.JLabel lblAgo;
    private javax.swing.JLabel lblAgo2;
    private javax.swing.JLabel lblApoderado;
    private javax.swing.JLabel lblBuscarCampoApoderado;
    private javax.swing.JLabel lblCancelado;
    private javax.swing.JLabel lblCedula10;
    private javax.swing.JLabel lblCedula6;
    private javax.swing.JLabel lblCedulaApoderado;
    private javax.swing.JLabel lblDic;
    private javax.swing.JLabel lblDic2;
    private javax.swing.JLabel lblEne;
    private javax.swing.JLabel lblEne2;
    private javax.swing.JLabel lblFeb;
    private javax.swing.JLabel lblFeb2;
    private javax.swing.JLabel lblFechaPago;
    private javax.swing.JLabel lblImporte;
    private javax.swing.JLabel lblJul;
    private javax.swing.JLabel lblJul2;
    private javax.swing.JLabel lblJun;
    private javax.swing.JLabel lblJun2;
    private javax.swing.JLabel lblMar;
    private javax.swing.JLabel lblMar2;
    private javax.swing.JLabel lblMay;
    private javax.swing.JLabel lblMay2;
    private javax.swing.JLabel lblNov;
    private javax.swing.JLabel lblNov2;
    private javax.swing.JLabel lblNumCuotasPagados;
    private org.edisoncor.gui.label.LabelMetric lblNumPago;
    private javax.swing.JLabel lblNumTotalCuotas;
    private javax.swing.JLabel lblOct;
    private javax.swing.JLabel lblOct2;
    private javax.swing.JLabel lblPeriodoActual;
    private javax.swing.JLabel lblPoderantes;
    private javax.swing.JLabel lblPoderantesBasico;
    private javax.swing.JLabel lblPoderantesBasico2;
    private javax.swing.JLabel lblPoderantesBasico3;
    private javax.swing.JLabel lblPoderantesMedio;
    private javax.swing.JLabel lblPoderantesMedio2;
    private javax.swing.JLabel lblSep;
    private javax.swing.JLabel lblSep2;
    private javax.swing.JLabel lblTituloTotalCompra;
    private javax.swing.JLabel lblTituloTotalCompra1;
    private javax.swing.JLabel lblTituloTotalCompra2;
    private javax.swing.JLabel lblTotalMoneda;
    private javax.swing.JLabel lblTotalMoneda1;
    private javax.swing.JLabel lblTotalMoneda2;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel2;
    private org.edisoncor.gui.panel.Panel panel3;
    private org.edisoncor.gui.panel.Panel panel4;
    private org.edisoncor.gui.panel.Panel panel6;
    private org.edisoncor.gui.panel.Panel panel7;
    private org.edisoncor.gui.panel.Panel pnCuotasAPagar;
    private org.edisoncor.gui.panel.Panel pnCuotasPagadas;
    private org.edisoncor.gui.panel.Panel pnMesesAPagar;
    private org.edisoncor.gui.panel.Panel pnMesesPagados;
    private javax.swing.JScrollPane scAllConcepto;
    private javax.swing.JScrollPane scAllConcepto1;
    private javax.swing.JScrollPane scApoderado;
    private javax.swing.JScrollPane scConceptoAPagar;
    private javax.swing.JTable tbAllConceptos;
    private javax.swing.JTable tbApoderado;
    private javax.swing.JTable tbConceptoAPagar;
    private javax.swing.JTable tbPoderantes;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtBuscarApoderado;
    private javax.swing.JTextField txtCantAlumnosPaganCuota;
    private javax.swing.JTextField txtCedulaApoderado;
    private javax.swing.JTextField txtConcepto;
    private javax.swing.JTextField txtImporte;
    private javax.swing.JTextField txtImporteRecibido;
    private javax.swing.JTextField txtNumCuotasAPagar;
    private javax.swing.JTextField txtSubtotal;
    private javax.swing.JTextField txtTotalAPagar;
    private javax.swing.JTextField txtVuelto;
    // End of variables declaration//GEN-END:variables
}
