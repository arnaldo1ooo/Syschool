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
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import static login.Login.Alias;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public final class RegistrarPagoSalario extends javax.swing.JDialog {
    
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

        //Permiso Roles de usuario
        btnGuardar.setVisible(metodos.PermisoRol(Alias, "PAGO_SALARIO", "ALTA"));
        
        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboBox(cbFuncionario, "SELECT fun_codigo, CONCAT(fun_nombre,' ',fun_apellido) AS nomape "
                + "FROM funcionario ORDER BY fun_nombre ", -1);
    }
    
    public void RegistroNuevo() {
        if (ComprobarCampos() == true) {
            int idfuncionario = metodoscombo.ObtenerIDSelectComboBox(cbFuncionario);
            double salario = metodostxt.DoubleAFormatoAmericano(txtSalario.getText());
            
            DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            String fecha = formatoFecha.format(dcFecha.getDate());
            String obs = txtObs.getText();
            
            int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de registrar este pago de salario?", "Confirmación", JOptionPane.YES_OPTION);
            if (JOptionPane.YES_OPTION == confirmado) {
                //Registrar nuevo gasto
                String sentencia = "CALL SP_PagoSalarioAlta('" + idfuncionario + "','" + salario + "','" + fecha + "','" + obs + "')";
                con.EjecutarABM(sentencia, true);
                
                Limpiar();
            }
        }
    }
    
    private void Limpiar() {
        cbFuncionario.setSelectedIndex(-1);
        txtSalario.setText("");
        dcFecha.setDate(new Date());
        txtObs.setText("");
    }
    
    private boolean ComprobarCampos() {
        if (cbFuncionario.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Seleccione un funcionario", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbFuncionario.requestFocus();
            return false;
        }
        
        if (txtSalario.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Salario vacío", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtSalario.requestFocus();
            return false;
        }
        
        if (dcFecha.getDate() == null) {
            JOptionPane.showMessageDialog(this, "Seleccione la fecha", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dcFecha.requestFocus();
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
        jLabel2 = new javax.swing.JLabel();

        setTitle("Ventana Registrar Gastos");
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
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(labelMetric1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(3, 3, 3)
                .addComponent(lblNumPago, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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

        lblFuncionario.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFuncionario.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFuncionario.setText("Funcionario*:");
        lblFuncionario.setToolTipText("");

        cbFuncionario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFuncionarioItemStateChanged(evt);
            }
        });

        lblCodigo6.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo6.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo6.setText("Salario:");

        txtSalario.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
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

        lblFechaRegistro.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaRegistro.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaRegistro.setText("Gs.");
        lblFechaRegistro.setToolTipText("");

        txtObs.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtObs.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtObs.setDisabledTextColor(new java.awt.Color(0, 0, 0));

        lblCodigo8.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo8.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo8.setText("Obs:");

        lblFechaRegistro1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaRegistro1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFechaRegistro1.setText("Fecha*:");
        lblFechaRegistro1.setToolTipText("");

        dcFecha.setEnabled(false);
        dcFecha.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFecha.setMinSelectableDate(new java.util.Date(631162800000L));

        lblCodigo9.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo9.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo9.setText("N° de cédula:");

        txtCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCedula.setHorizontalAlignment(javax.swing.JTextField.LEFT);
        txtCedula.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCedula.setEnabled(false);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFuncionario, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCodigo6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigo8, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(txtSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 192, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(lblFechaRegistro))
                            .addComponent(cbFuncionario, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(29, 29, 29)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCodigo9, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblFechaRegistro1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 82, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtCedula)
                            .addComponent(dcFecha, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE)))
                    .addComponent(txtObs))
                .addGap(17, 17, 17))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblCodigo9, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigo6, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFecha, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaRegistro1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCodigo8, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtObs, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(16, Short.MAX_VALUE))
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
                .addGap(151, 151, 151)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addGap(49, 49, 49)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 171, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(36, 36, 36))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                        .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(49, 49, 49))))
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
                .addContainerGap(17, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 677, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 285, Short.MAX_VALUE)
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
            int idfuncionario = metodoscombo.ObtenerIDSelectComboBox(cbFuncionario);
            String sentencia = "SELECT * FROM funcionario WHERE fun_codigo = '" + idfuncionario + "'";
            con = con.ObtenerRSSentencia(sentencia);
            while (con.rs.next()) {
                txtCedula.setText(con.rs.getString("fun_cedula"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }//GEN-LAST:event_cbFuncionarioItemStateChanged

    private void txtSalarioKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSalarioKeyReleased
        txtSalario.setText(metodostxt.DoubleFormatoSudamericaKeyReleased(txtSalario.getText()));
    }//GEN-LAST:event_txtSalarioKeyReleased

    private void txtSalarioKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtSalarioKeyTyped
        metodostxt.SoloNumeroDecimalKeyTyped(evt, txtSalario);
        metodostxt.TxtCantidadCaracteresKeyTyped(txtSalario, 11);
    }//GEN-LAST:event_txtSalarioKeyTyped
    
    private void GenerarNumpago() {
        try {
            con = con.ObtenerRSSentencia("SELECT MAX(pasal_numpago) AS numultimopago FROM pago_salario");
            String numultimapago = null;
            while (con.rs.next()) {
                numultimapago = con.rs.getString("numultimopago");
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
        ordenTabulador.add(dcFecha);
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
    private com.toedter.calendar.JDateChooser dcFecha;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpPrincipal;
    private org.edisoncor.gui.label.LabelMetric labelMetric1;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lblCodigo6;
    private javax.swing.JLabel lblCodigo8;
    private javax.swing.JLabel lblCodigo9;
    private javax.swing.JLabel lblFechaRegistro;
    private javax.swing.JLabel lblFechaRegistro1;
    private javax.swing.JLabel lblFuncionario;
    private org.edisoncor.gui.label.LabelMetric lblNumPago;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel2;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtObs;
    private javax.swing.JTextField txtSalario;
    // End of variables declaration//GEN-END:variables
}
