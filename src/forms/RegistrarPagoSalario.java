/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Toolkit;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static login.Login.codUsuario;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class RegistrarPagoSalario extends javax.swing.JDialog {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    MetodosCombo metodoscombo = new MetodosCombo();

    public RegistrarPagoSalario(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        //Metodos
        GenerarNumpago();
        CargarComboBoxes();
        Limpiar();

        txtCedula.setText("");
        txtSalario.setText("");
        txtCargo.setText("");

        Calendar cal = Calendar.getInstance();
        int mesActual = cal.get(Calendar.MONTH);
        cbMes.setSelectedIndex(mesActual);

        //Permiso Roles de usuario
        String permisos = metodos.PermisoRol(codUsuario, "PAGO_SALARIO");
        btnGuardar.setVisible(permisos.contains("A"));

        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboConsulta(cbFuncionario, "SELECT fun_codigo, CONCAT(fun_nombre,' ',fun_apellido) AS nomape "
                + "FROM funcionario ORDER BY fun_nombre ", -1);
    }

    public void RegistroNuevo() {
        if (ComprobarCampos() == true) {
            String numpago = lblNumPago.getText();
            int idfuncionario = metodoscombo.ObtenerIDSelectCombo(cbFuncionario);
            double salario = metodostxt.StringAFormatoAmericano(txtSalario.getText());

            DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            String fecha = formatoFecha.format(dcFecha.getDate());
            String mes = cbMes.getSelectedItem().toString() + "";
            String obs = txtObs.getText();

            int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de registrar este pago de salario?", "Confirmación", JOptionPane.YES_OPTION);
            if (JOptionPane.YES_OPTION == confirmado) {
                //Registrar nuevo gasto
                String sentencia = "CALL SP_PagoSalarioAlta('" + numpago + "','" + idfuncionario + "','" + salario + "','" + fecha + "','" + mes + "','" + obs + "')";
                con.EjecutarABM(sentencia, true);
                ImprimirRecibo();
                GenerarNumpago();
                Limpiar();
            }
        }
    }

    private void Limpiar() {
        cbFuncionario.setSelectedIndex(-1);
        txtSalario.setText("");
        txtCedula.setText("");
        txtCargo.setText("");
        dcFecha.setDate(new Date());
        cbMes.setSelectedIndex(0);
        txtObs.setText("");
    }

    private void ImprimirRecibo() {
        //Imprimir recibo
        int confirmado2 = JOptionPane.showConfirmDialog(this, "¿Quieres imprimir el recibo de pago salarial?", "Confirmación", JOptionPane.YES_OPTION);
        if (JOptionPane.YES_OPTION == confirmado2) {

            InputStream logo = this.getClass().getResourceAsStream("/reportes/images/logo_ace.jpg");
            InputStream logo2 = this.getClass().getResourceAsStream("/reportes/images/logo_ace.jpg");
            //Parametros
            Map parametros = new HashMap();
            parametros.clear();
            parametros.put("NUMPAGO", lblNumPago.getText());
            parametros.put("NUMPAGO2", lblNumPago.getText());
            parametros.put("LOGO", logo);
            parametros.put("LOGO2", logo2);
            parametros.put("FUNCIONARIO", cbFuncionario.getSelectedItem() + "");
            parametros.put("FUNCIONARIO2", cbFuncionario.getSelectedItem() + "");
            parametros.put("CEDULA", txtCedula.getText());
            parametros.put("CEDULA2", txtCedula.getText());
            parametros.put("CARGO", txtCargo.getText());
            parametros.put("CARGO2", txtCargo.getText());
            parametros.put("MES", cbMes.getSelectedItem() + "");
            parametros.put("MES2", cbMes.getSelectedItem() + "");
            SimpleDateFormat formatosuda = new SimpleDateFormat("dd/MM/yyyy");  //25/08/2015
            parametros.put("DESDE", formatosuda.format(dcFechaDesde.getDate()));
            parametros.put("DESDE2", formatosuda.format(dcFechaDesde.getDate()));
            parametros.put("HASTA", formatosuda.format(dcFechaHasta.getDate()));
            parametros.put("HASTA2", formatosuda.format(dcFechaHasta.getDate()));
            parametros.put("SALARIO", txtSalario.getText() + " Gs");
            parametros.put("SALARIO2", txtSalario.getText() + " Gs");

            //Enviar directorio del subreporte
            String directoriosub = this.getClass().getResource("/reportes/recibo_salario/reporte_recibo_salarial.jasper").toString();
            directoriosub = directoriosub.replaceAll("reporte_recibo_salarial.jasper", "");
            parametros.put("SUBREPORT_DIR", directoriosub); //Direccion del subreporte

            String tipohoja = "";
            try {
                con = con.ObtenerRSSentencia("SELECT conf_descripcion, conf_valor FROM configuracion");
                while (con.getResultSet().next()) {
                    switch (con.getResultSet().getString("conf_descripcion")) {
                        case "TIPOHOJA":
                            tipohoja = con.getResultSet().getString("conf_valor");
                            break;
                        default:
                            System.out.println("Error switch " + con.getResultSet().getString("conf_descripcion"));
                            //JOptionPane.showMessageDialog(this, "No se encontró la hoja seleccionada", "Error", JOptionPane.ERROR_MESSAGE);
                            break;
                    }
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.DesconectarBasedeDatos();

            String rutajasperPrincipal = "/reportes/recibo_salario/reporte_recibo_principal_" + tipohoja.toLowerCase() + ".jasper";
            System.out.println("tipohoja " + tipohoja);
            System.out.println("Ruta del jasper principal " + rutajasperPrincipal);

            metodos.GenerarReporteJTABLE(rutajasperPrincipal, parametros, null);
        }
    }

    private boolean ComprobarCampos() {
        if (cbFuncionario.getSelectedIndex() == -1) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Seleccione un funcionario", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbFuncionario.requestFocus();
            return false;
        }

        return true;
    }

//--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        labelMetric1 = new org.edisoncor.gui.label.LabelMetric();
        lblNumPago = new org.edisoncor.gui.label.LabelMetric();
        jpBotones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panel1 = new org.edisoncor.gui.panel.Panel();
        lblFuncionario = new javax.swing.JLabel();
        cbFuncionario = new javax.swing.JComboBox<>();
        lblCodigo6 = new javax.swing.JLabel();
        txtSalario = new javax.swing.JTextField();
        lblFechaRegistro = new javax.swing.JLabel();
        txtObs = new javax.swing.JTextField();
        lblCodigo8 = new javax.swing.JLabel();
        lblFechaRegistro1 = new javax.swing.JLabel();
        dcFecha = new com.toedter.calendar.JDateChooser();
        lblCodigo9 = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        lblFechaRegistro2 = new javax.swing.JLabel();
        dcFechaDesde = new com.toedter.calendar.JDateChooser();
        dcFechaHasta = new com.toedter.calendar.JDateChooser();
        lblFechaRegistro3 = new javax.swing.JLabel();
        lblCodigo7 = new javax.swing.JLabel();
        cbMes = new javax.swing.JComboBox();
        lblCodigo10 = new javax.swing.JLabel();
        txtCargo = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();

        setTitle("Ventana Registrar Pago de Salarios");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        panel2.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel2.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REGISTRAR PAGO DE SALARIO");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 22)); // NOI18N

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
                .addGap(17, 17, 17)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(147, 147, 147)
                .addComponent(labelMetric1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblNumPago, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jpBotones.setBackground(new java.awt.Color(233, 255, 255));
        jpBotones.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()));
        jpBotones.setPreferredSize(new java.awt.Dimension(100, 50));

        btnGuardar.setBackground(new java.awt.Color(0, 153, 255));
        btnGuardar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoGuardar.png"))); // NOI18N
        btnGuardar.setText("Registrar");
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
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancelar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel1.setColorPrimario(new java.awt.Color(233, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(255, 255, 255));

        lblFuncionario.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblFuncionario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFuncionario.setText("Funcionario*:");
        lblFuncionario.setToolTipText("");

        cbFuncionario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFuncionarioItemStateChanged(evt);
            }
        });

        lblCodigo6.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCodigo6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo6.setText("Salario:");

        txtSalario.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtSalario.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtSalario.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtSalario.setEnabled(false);
        txtSalario.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtSalarioKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtSalarioKeyTyped(evt);
            }
        });

        lblFechaRegistro.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblFechaRegistro.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaRegistro.setText("Gs.");
        lblFechaRegistro.setToolTipText("");

        txtObs.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtObs.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtObs.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        lblCodigo8.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCodigo8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo8.setText("Obs:");

        lblFechaRegistro1.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblFechaRegistro1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFechaRegistro1.setText("Fecha:");
        lblFechaRegistro1.setToolTipText("");

        dcFecha.setEnabled(false);
        dcFecha.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFecha.setMinSelectableDate(new java.util.Date(631162800000L));

        lblCodigo9.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCodigo9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo9.setText("N° de cédula:");

        txtCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCedula.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtCedula.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCedula.setEnabled(false);

        lblFechaRegistro2.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblFechaRegistro2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFechaRegistro2.setText("Desde:");
        lblFechaRegistro2.setToolTipText("");

        dcFechaDesde.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFechaDesde.setMinSelectableDate(new java.util.Date(631162800000L));

        dcFechaHasta.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFechaHasta.setMinSelectableDate(new java.util.Date(631162800000L));

        lblFechaRegistro3.setFont(new java.awt.Font("SansSerif", 0, 11)); // NOI18N
        lblFechaRegistro3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFechaRegistro3.setText("Hasta:");
        lblFechaRegistro3.setToolTipText("");

        lblCodigo7.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCodigo7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo7.setText("Periodo:");

        cbMes.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        cbMes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbMesItemStateChanged(evt);
            }
        });

        lblCodigo10.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblCodigo10.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo10.setText("Cargo:");

        txtCargo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCargo.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtCargo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCargo.setEnabled(false);
        txtCargo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCargoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCargoKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblCodigo7, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCodigo6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFuncionario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCodigo8, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel1Layout.createSequentialGroup()
                                .addComponent(cbMes, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(14, 14, 14)
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(dcFechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblFechaRegistro2))
                                .addGap(18, 18, 18)
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblFechaRegistro3)
                                    .addComponent(dcFechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 136, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(399, 399, 399))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(txtObs, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(txtSalario)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addComponent(lblFechaRegistro)
                                        .addGap(6, 6, 6))
                                    .addGroup(panel1Layout.createSequentialGroup()
                                        .addComponent(cbFuncionario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)))
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(lblCodigo9)
                                    .addComponent(lblCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(lblFechaRegistro1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(204, 204, 204))))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFechaRegistro1, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblCodigo10, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCargo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(cbFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblCodigo6, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCodigo9, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(10, 10, 10)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaRegistro3)
                    .addComponent(lblFechaRegistro2))
                .addGap(2, 2, 2)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dcFechaHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbMes, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigo7, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo8, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtObs, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Campos con (*) son obligatorios");

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, 743, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(197, 197, 197))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addContainerGap())))))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(14, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 341, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("RegistrarCompra");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        RegistroNuevo();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed


    }//GEN-LAST:event_btnGuardarKeyPressed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        Limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void cbFuncionarioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFuncionarioItemStateChanged
        try {
            int idfuncionario = metodoscombo.ObtenerIDSelectCombo(cbFuncionario);

            String sentencia = "SELECT fun_cedula, fun_salario, car_descripcion FROM funcionario,cargo "
                    + "WHERE fun_codigo = '" + idfuncionario + "' AND fun_cargo=car_codigo";
            con = con.ObtenerRSSentencia(sentencia);
            while (con.getResultSet().next()) {
                txtCedula.setText(metodostxt.StringPuntosMiles(con.getResultSet().getString("fun_cedula")));
                txtSalario.setText(metodostxt.DoubleAFormatSudamerica(con.getResultSet().getDouble("fun_salario")));
                txtCargo.setText(con.getResultSet().getString("car_descripcion"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }//GEN-LAST:event_cbFuncionarioItemStateChanged

    private void txtSalarioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSalarioKeyReleased
        txtSalario.setText(metodostxt.StringAFormatSudamericaKeyRelease(txtSalario.getText()));
    }//GEN-LAST:event_txtSalarioKeyReleased

    private void txtSalarioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSalarioKeyTyped
        metodostxt.SoloNumeroDecimalKeyTyped(evt, txtSalario);
        metodostxt.TxtCantidadCaracteresKeyTyped(txtSalario, 11);
    }//GEN-LAST:event_txtSalarioKeyTyped

    private void cbMesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbMesItemStateChanged
        Calendar cal = Calendar.getInstance();

        cal.set(cal.get(Calendar.YEAR), cbMes.getSelectedIndex(), 1); //Primer dia del mes seleccionado
        dcFechaDesde.setCalendar(cal);

        cal.set(cal.get(Calendar.YEAR), cbMes.getSelectedIndex(), cal.getActualMaximum(Calendar.DAY_OF_MONTH)); //Ultimo dia del mes seleccionado
        dcFechaHasta.setCalendar(cal);
    }//GEN-LAST:event_cbMesItemStateChanged

    private void txtCargoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCargoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCargoKeyReleased

    private void txtCargoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCargoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCargoKeyTyped

    private void GenerarNumpago() {
        try {
            con = con.ObtenerRSSentencia("SELECT MAX(pasal_numpago) AS numultimopago FROM pago_salario");
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

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(cbFuncionario);
        ordenTabulador.add(txtObs);
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
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private static javax.swing.JComboBox<MetodosCombo> cbFuncionario;
    private javax.swing.JComboBox cbMes;
    private com.toedter.calendar.JDateChooser dcFecha;
    private com.toedter.calendar.JDateChooser dcFechaDesde;
    private com.toedter.calendar.JDateChooser dcFechaHasta;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpPrincipal;
    private org.edisoncor.gui.label.LabelMetric labelMetric1;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lblCodigo10;
    private javax.swing.JLabel lblCodigo6;
    private javax.swing.JLabel lblCodigo7;
    private javax.swing.JLabel lblCodigo8;
    private javax.swing.JLabel lblCodigo9;
    private javax.swing.JLabel lblFechaRegistro;
    private javax.swing.JLabel lblFechaRegistro1;
    private javax.swing.JLabel lblFechaRegistro2;
    private javax.swing.JLabel lblFechaRegistro3;
    private javax.swing.JLabel lblFuncionario;
    private org.edisoncor.gui.label.LabelMetric lblNumPago;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel2;
    private javax.swing.JTextField txtCargo;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtObs;
    private javax.swing.JTextField txtSalario;
    // End of variables declaration//GEN-END:variables
}
