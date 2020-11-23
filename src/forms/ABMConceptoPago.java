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
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utilidades.Metodos;
import utilidades.MetodosTXT;
import static login.Login.codUsuario;

/**
 *
 * @author Arnaldo Cantero
 */
public class ABMConceptoPago extends javax.swing.JDialog {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();

    public ABMConceptoPago(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        //Permiso Roles de usuario
        String permisos = metodos.PermisoRol(codUsuario, "CONCEPTO_PAGO");
        btnNuevo.setVisible(permisos.contains("A"));
        btnModificar.setVisible(permisos.contains("M"));
        btnEliminar.setVisible(permisos.contains("B"));

        TablaConsultaBDAll(); //Trae todos los registros
        txtBuscar.requestFocus();

        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void RegistroNuevoModificar() {
        if (ComprobarCampos() == true) {
            String codigo = txtCodigo.getText();
            String descripcion = txtDescripcion.getText().toUpperCase();
            String tipoimporte = cbTipoImporte.getSelectedItem().toString();
            double importe = metodostxt.DoubleAFormatoAmericano(txtImporte.getText());
            String tipopago = cbTipoPago.getSelectedItem().toString();
            int numpagos = sfNumPagos.getValue();
            int ene = chbEne.isSelected() ? 1 : 0;
            int feb = chbFeb.isSelected() ? 1 : 0;
            int mar = chbMar.isSelected() ? 1 : 0;
            int abr = chbAbr.isSelected() ? 1 : 0;
            int may = chbMay.isSelected() ? 1 : 0;
            int jun = chbJun.isSelected() ? 1 : 0;
            int jul = chbJul.isSelected() ? 1 : 0;
            int ago = chbAgo.isSelected() ? 1 : 0;
            int sep = chbSep.isSelected() ? 1 : 0;
            int oct = chbOct.isSelected() ? 1 : 0;
            int nov = chbNov.isSelected() ? 1 : 0;
            int dic = chbDic.isSelected() ? 1 : 0;

            if (txtCodigo.getText().equals("")) { //NUEVO REGISTRO
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de registrar este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    String sentencia = "CALL SP_ConceptoAlta ('" + descripcion + "','" + tipoimporte + "','" + importe + "','" + tipopago + "','" + numpagos + "','"
                            + ene + "','" + feb + "','" + mar + "','" + abr + "','" + may + "','" + jun + "','" + jul + "','" + ago + "','" + sep + "','" + oct + "','" + nov + "','" + dic + "')";
                    con.EjecutarABM(sentencia, true);

                    TablaConsultaBDAll(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            } else { //MODIFICAR REGISTRO
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de modificar este regitro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    String sentencia = "CALL SP_ConceptoModificar(" + codigo + ",'" + descripcion + "','" + tipoimporte + "','" + importe + "','" + tipopago + "','" + numpagos + "','"
                            + ene + "','" + feb + "','" + mar + "','" + abr + "','" + may + "','" + jun + "','" + jul + "','" + ago + "','" + sep + "','" + oct + "','" + nov + "','" + dic + "')";
                    con.EjecutarABM(sentencia, true);

                    TablaConsultaBDAll(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            }
        }
    }

    private void RegistroEliminar() {
        int filasel = tbPrincipal.getSelectedRow();
        if (filasel != -1) {
            int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro eliminar este concepto?, TAMBIEN SE ELIMINARÁN TODOS LOS PAGOS REALIZADOS CON ESTE CONCEPTO", "Confirmación", JOptionPane.YES_OPTION);
            if (JOptionPane.YES_OPTION == confirmado) {
                String codigo = tbPrincipal.getValueAt(filasel, 0) + "";
                String sentencia = "CALL SP_ConceptoEliminar(" + codigo + ")";
                con.EjecutarABM(sentencia, true);

                TablaConsultaBDAll(); //Actualizar tabla
                ModoEdicion(false);
                Limpiar();
            }
        } else {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtBuscar.requestFocus();
        }
    }

    DefaultTableModel modelotabla;
    Object[] fila;
    double importe;

    private void TablaConsultaBDAll() {//Realiza la consulta de los productos que tenemos en la base de datos
        String sentencia = "CALL SP_ConceptoConsulta";
        String titlesJtabla[] = {"Código", "Descripción", "Tipo de importe", "Importe", "Tipo de pago", "N° de pagos",
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        if (cbCampoBuscar.getItemCount() == 0) {//Si combo esta vacio
            for (int i = 0; i < titlesJtabla.length; i++) {
                cbCampoBuscar.addItem(titlesJtabla[i]);
            }
            cbCampoBuscar.setSelectedIndex(1);
        }

        modelotabla = new DefaultTableModel(null, titlesJtabla);

        con = con.ObtenerRSSentencia(sentencia);

        try {
            fila = new Object[18];
            while (con.rs.next()) {
                fila[0] = con.rs.getString(1);
                fila[1] = con.rs.getString(2);
                fila[2] = con.rs.getString(3);

                importe = con.rs.getDouble(4);
                fila[3] = metodostxt.DoubleAFormatoSudamerica(importe);
                con.rs.getString(4);
                fila[4] = con.rs.getString(5);
                fila[5] = con.rs.getString(6);
                fila[6] = con.rs.getString(7);
                fila[7] = con.rs.getString(8);
                fila[8] = con.rs.getString(9);
                fila[9] = con.rs.getString(10);
                fila[10] = con.rs.getString(11);
                fila[11] = con.rs.getString(12);
                fila[12] = con.rs.getString(13);
                fila[13] = con.rs.getString(14);
                fila[14] = con.rs.getString(15);
                fila[15] = con.rs.getString(16);
                fila[16] = con.rs.getString(17);
                fila[17] = con.rs.getString(18);

                modelotabla.addRow(fila);//agrega el registro a la tabla
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
        tbPrincipal.setModel(modelotabla);
        metodos.AnchuraColumna(tbPrincipal);

        if (tbPrincipal.getModel().getRowCount() == 1) {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registros encontrados");
        }
    }

    private void ModoVistaPrevia() {
        txtCodigo.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0) + ""));
        txtDescripcion.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 1) + ""));
        cbTipoImporte.setSelectedItem(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 2) + ""));
        txtImporte.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 3) + ""));
        cbTipoPago.setSelectedItem(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 4) + ""));
        sfNumPagos.setValue(Integer.parseInt(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 5) + "")));
        chbEne.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 6) + "") == 1));
        chbFeb.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 7) + "") == 1));
        chbMar.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 8) + "") == 1));
        chbAbr.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 9) + "") == 1));
        chbMay.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 10) + "") == 1));
        chbJun.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 11) + "") == 1));
        chbJul.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 12) + "") == 1));
        chbAgo.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 13) + "") == 1));
        chbSep.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 14) + "") == 1));
        chbOct.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 15) + "") == 1));
        chbNov.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 16) + "") == 1));
        chbDic.setSelected((Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 17) + "") == 1));
    }

    private void ModoEdicion(boolean valor) {
        txtBuscar.setEnabled(!valor);
        tbPrincipal.setEnabled(!valor);
        txtDescripcion.setEnabled(valor);
        cbTipoImporte.setEnabled(valor);
        sfNumPagos.setEnabled(valor);
        txtImporte.setEnabled(valor);
        cbTipoPago.setEnabled(valor);
        chbEne.setEnabled(valor);
        chbFeb.setEnabled(valor);
        chbMar.setEnabled(valor);
        chbAbr.setEnabled(valor);
        chbMay.setEnabled(valor);
        chbJun.setEnabled(valor);
        chbJul.setEnabled(valor);
        chbAgo.setEnabled(valor);
        chbSep.setEnabled(valor);
        chbOct.setEnabled(valor);
        chbNov.setEnabled(valor);
        chbDic.setEnabled(valor);

        btnNuevo.setEnabled(!valor);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);

        txtDescripcion.requestFocus();
    }

    private void Limpiar() {
        txtCodigo.setText("");
        txtDescripcion.setText("");
        cbTipoImporte.setSelectedIndex(0);
        sfNumPagos.setValue(0);
        txtImporte.setText("0");
        cbTipoPago.setSelectedIndex(0);
        chbEne.setSelected(false);
        chbFeb.setSelected(false);
        chbMar.setSelected(false);
        chbAbr.setSelected(false);
        chbMay.setSelected(false);
        chbJun.setSelected(false);
        chbJul.setSelected(false);
        chbAgo.setSelected(false);
        chbSep.setSelected(false);
        chbOct.setSelected(false);
        chbNov.setSelected(false);
        chbDic.setSelected(false);

        txtBuscar.requestFocus();
        tbPrincipal.clearSelection();
    }

    public boolean ComprobarCampos() {

        if (sfNumPagos.getValue() == 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "El número de pagos no puede ser 0", "Advertencia", JOptionPane.WARNING_MESSAGE);
            sfNumPagos.requestFocus();
            return false;
        }

        if (txtDescripcion.getText().equals("")) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "La descripción no puede estar vacia", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtDescripcion.requestFocus();
            return false;
        }

        if (sfNumPagos.getValue() == 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "El número de pagos no puede ser 0", "Advertencia", JOptionPane.WARNING_MESSAGE);
            sfNumPagos.requestFocus();
            return false;
        }

        //Si cantidad de meses seleccionados es mayor o menor a cantidad de cuotas
        int cantmes = 0;
        Component[] comMesesAPagar = pnMesesAPagar.getComponents();
        JCheckBox chActual;
        for (int i = 0; i < comMesesAPagar.length; i++) {
            if (comMesesAPagar[i] instanceof JCheckBox) { //Si es Checkbox
                chActual = ((JCheckBox) comMesesAPagar[i]);
                if (chActual.isSelected()) {
                    cantmes = cantmes + 1;
                }
            }
        }

        if (cantmes > sfNumPagos.getValue()) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "El número de meses seleccionados no puede ser mayor al número de pagos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        if (cantmes < sfNumPagos.getValue()) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "El número de meses seleccionados no puede ser menor al número de pagos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

//--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        jpTabla = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        scPrincipal = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lblBuscarCampo = new javax.swing.JLabel();
        cbCampoBuscar = new javax.swing.JComboBox();
        lbCantRegistros = new javax.swing.JLabel();
        jpBotones = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jtpEdicion = new javax.swing.JTabbedPane();
        jpEdicion = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblDescripcion = new javax.swing.JLabel();
        txtDescripcion = new javax.swing.JTextField();
        lblImporte = new javax.swing.JLabel();
        txtImporte = new javax.swing.JTextField();
        lblNumPagos = new javax.swing.JLabel();
        sfNumPagos = new com.toedter.components.JSpinField();
        lblImporte2 = new javax.swing.JLabel();
        cbTipoPago = new javax.swing.JComboBox<>();
        lblImporte3 = new javax.swing.JLabel();
        cbTipoImporte = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        pnMesesAPagar = new org.edisoncor.gui.panel.Panel();
        chbEne = new javax.swing.JCheckBox();
        chbFeb = new javax.swing.JCheckBox();
        chbAbr = new javax.swing.JCheckBox();
        chbMar = new javax.swing.JCheckBox();
        chbJun = new javax.swing.JCheckBox();
        chbMay = new javax.swing.JCheckBox();
        chbJul = new javax.swing.JCheckBox();
        chbAgo = new javax.swing.JCheckBox();
        chbOct = new javax.swing.JCheckBox();
        chbSep = new javax.swing.JCheckBox();
        chbNov = new javax.swing.JCheckBox();
        chbDic = new javax.swing.JCheckBox();
        jpBotones2 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();

        setTitle("Ventana Conceptos de pago");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpTabla.setBackground(new java.awt.Color(233, 255, 255));
        jpTabla.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoBuscar.png"))); // NOI18N
        jLabel10.setText("  BUSCAR ");

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

        tbPrincipal.setAutoCreateRowSorter(true);
        tbPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbPrincipal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbPrincipal.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbPrincipal.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbPrincipal.setGridColor(new java.awt.Color(0, 153, 204));
        tbPrincipal.setOpaque(false);
        tbPrincipal.setRowHeight(20);
        tbPrincipal.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbPrincipal.getTableHeader().setReorderingAllowed(false);
        tbPrincipal.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbPrincipalMousePressed(evt);
            }
        });
        tbPrincipal.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbPrincipalKeyReleased(evt);
            }
        });
        scPrincipal.setViewportView(tbPrincipal);

        lblBuscarCampo.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo.setText("Buscar por:");

        lbCantRegistros.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lbCantRegistros.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistros.setText("0 Registros encontrados");
        lbCantRegistros.setPreferredSize(new java.awt.Dimension(57, 25));

        javax.swing.GroupLayout jpTablaLayout = new javax.swing.GroupLayout(jpTabla);
        jpTabla.setLayout(jpTablaLayout);
        jpTablaLayout.setHorizontalGroup(
            jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scPrincipal)
                    .addGroup(jpTablaLayout.createSequentialGroup()
                        .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpTablaLayout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 247, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(102, 102, 102))
                            .addGroup(jpTablaLayout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblBuscarCampo)
                                .addGap(3, 3, 3)))
                        .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jpTablaLayout.createSequentialGroup()
                .addGap(341, 341, 341)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 294, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpTablaLayout.setVerticalGroup(
            jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscarCampo, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jpBotones.setBackground(new java.awt.Color(233, 255, 255));
        jpBotones.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));
        jpBotones.setPreferredSize(new java.awt.Dimension(100, 50));

        btnNuevo.setBackground(new java.awt.Color(14, 154, 153));
        btnNuevo.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnNuevo.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevo.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoNuevo.png"))); // NOI18N
        btnNuevo.setText("NUEVO");
        btnNuevo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNuevo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        btnNuevo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoActionPerformed(evt);
            }
        });

        btnModificar.setBackground(new java.awt.Color(14, 154, 153));
        btnModificar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnModificar.setForeground(new java.awt.Color(255, 255, 255));
        btnModificar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoModifcar.png"))); // NOI18N
        btnModificar.setText("MODIFICAR");
        btnModificar.setEnabled(false);
        btnModificar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnModificar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarActionPerformed(evt);
            }
        });

        btnEliminar.setBackground(new java.awt.Color(14, 154, 153));
        btnEliminar.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoEliminar.png"))); // NOI18N
        btnEliminar.setText("ELIMINAR");
        btnEliminar.setEnabled(false);
        btnEliminar.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpBotonesLayout = new javax.swing.GroupLayout(jpBotones);
        jpBotones.setLayout(jpBotonesLayout);
        jpBotonesLayout.setHorizontalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtpEdicion.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jtpEdicion.setName(""); // NOI18N

        jpEdicion.setBackground(new java.awt.Color(233, 255, 255));
        jpEdicion.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        lblCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo.setForeground(new java.awt.Color(102, 102, 102));
        lblCodigo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo.setText("Código:");

        txtCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo.setEnabled(false);

        lblDescripcion.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblDescripcion.setForeground(new java.awt.Color(102, 102, 102));
        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDescripcion.setText("Descripción*:");

        txtDescripcion.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtDescripcion.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDescripcion.setEnabled(false);
        txtDescripcion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDescripcionKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDescripcionKeyTyped(evt);
            }
        });

        lblImporte.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblImporte.setForeground(new java.awt.Color(102, 102, 102));
        lblImporte.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblImporte.setText("Importe:");

        txtImporte.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtImporte.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtImporte.setText("0");
        txtImporte.setToolTipText("");
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

        lblNumPagos.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblNumPagos.setForeground(new java.awt.Color(102, 102, 102));
        lblNumPagos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNumPagos.setText("N° de pagos:");

        sfNumPagos.setEnabled(false);

        lblImporte2.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblImporte2.setForeground(new java.awt.Color(102, 102, 102));
        lblImporte2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblImporte2.setText("Tipo de pago:");

        cbTipoPago.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MENSUAL", "UNICO" }));
        cbTipoPago.setEnabled(false);
        cbTipoPago.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbTipoPagoItemStateChanged(evt);
            }
        });

        lblImporte3.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblImporte3.setForeground(new java.awt.Color(102, 102, 102));
        lblImporte3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblImporte3.setText("Tipo de importe:");

        cbTipoImporte.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "FIJO", "VARIABLE" }));
        cbTipoImporte.setEnabled(false);
        cbTipoImporte.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbTipoImporteItemStateChanged(evt);
            }
        });

        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Campos con (*) son obligatorios");

        pnMesesAPagar.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Meses", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.TOP));
        pnMesesAPagar.setColorPrimario(new java.awt.Color(233, 255, 255));
        pnMesesAPagar.setColorSecundario(new java.awt.Color(233, 255, 255));

        chbEne.setText("Enero");
        chbEne.setEnabled(false);

        chbFeb.setText("Febrero");
        chbFeb.setEnabled(false);

        chbAbr.setText("Abril");
        chbAbr.setEnabled(false);

        chbMar.setText("Marzo");
        chbMar.setEnabled(false);

        chbJun.setText("Junio");
        chbJun.setEnabled(false);

        chbMay.setText("Mayo");
        chbMay.setEnabled(false);

        chbJul.setText("Julio");
        chbJul.setEnabled(false);

        chbAgo.setText("Agosto");
        chbAgo.setEnabled(false);

        chbOct.setText("Octubre");
        chbOct.setEnabled(false);

        chbSep.setText("Septiembre");
        chbSep.setEnabled(false);

        chbNov.setText("Noviembre");
        chbNov.setEnabled(false);
        chbNov.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chbNovActionPerformed(evt);
            }
        });

        chbDic.setText("Diciembre");
        chbDic.setEnabled(false);

        javax.swing.GroupLayout pnMesesAPagarLayout = new javax.swing.GroupLayout(pnMesesAPagar);
        pnMesesAPagar.setLayout(pnMesesAPagarLayout);
        pnMesesAPagarLayout.setHorizontalGroup(
            pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMesesAPagarLayout.createSequentialGroup()
                .addContainerGap(23, Short.MAX_VALUE)
                .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnMesesAPagarLayout.createSequentialGroup()
                        .addComponent(chbEne, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chbFeb, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnMesesAPagarLayout.createSequentialGroup()
                        .addComponent(chbJul, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chbAgo, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnMesesAPagarLayout.createSequentialGroup()
                        .addComponent(chbSep, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chbOct, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chbNov, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chbDic, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnMesesAPagarLayout.createSequentialGroup()
                        .addComponent(chbMar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chbAbr, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chbMay, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(chbJun, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        pnMesesAPagarLayout.setVerticalGroup(
            pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnMesesAPagarLayout.createSequentialGroup()
                .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(chbEne)
                    .addComponent(chbFeb)
                    .addComponent(chbMar)
                    .addComponent(chbAbr)
                    .addComponent(chbMay)
                    .addComponent(chbJun))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chbSep)
                        .addComponent(chbOct)
                        .addComponent(chbNov)
                        .addComponent(chbDic))
                    .addGroup(pnMesesAPagarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(chbJul)
                        .addComponent(chbAgo)))
                .addGap(0, 10, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpEdicionLayout = new javax.swing.GroupLayout(jpEdicion);
        jpEdicion.setLayout(jpEdicionLayout);
        jpEdicionLayout.setHorizontalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addGap(39, 39, 39)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(lblImporte3)
                            .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtCodigo, javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbTipoImporte, javax.swing.GroupLayout.Alignment.LEADING, 0, 105, Short.MAX_VALUE)
                            .addComponent(txtImporte))
                        .addGap(113, 113, 113)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblNumPagos, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblDescripcion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblImporte2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(sfNumPagos, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(cbTipoPago, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(txtDescripcion)))
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addGap(71, 71, 71)
                        .addComponent(pnMesesAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(83, 83, 83))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpEdicionLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jLabel2)
                .addGap(14, 14, 14))
        );
        jpEdicionLayout.setVerticalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNumPagos, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(sfNumPagos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblImporte3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblImporte, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtImporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblImporte2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbTipoPago, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnMesesAPagar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jtpEdicion.addTab("Edición", jpEdicion);

        jpBotones2.setBackground(new java.awt.Color(233, 255, 255));
        jpBotones2.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        btnGuardar.setBackground(new java.awt.Color(0, 153, 255));
        btnGuardar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoGuardar.png"))); // NOI18N
        btnGuardar.setText("Guardar");
        btnGuardar.setToolTipText("Inserta el nuevo registro");
        btnGuardar.setEnabled(false);
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

        btnCancelar.setBackground(new java.awt.Color(255, 138, 138));
        btnCancelar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnCancelar.setForeground(new java.awt.Color(255, 255, 255));
        btnCancelar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoCancelar.png"))); // NOI18N
        btnCancelar.setText("Cancelar");
        btnCancelar.setToolTipText("Cancela la acción");
        btnCancelar.setEnabled(false);
        btnCancelar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelarActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpBotones2Layout = new javax.swing.GroupLayout(jpBotones2);
        jpBotones2.setLayout(jpBotones2Layout);
        jpBotones2Layout.setHorizontalGroup(
            jpBotones2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotones2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnCancelar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpBotones2Layout.setVerticalGroup(
            jpBotones2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBotones2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpBotones2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCancelar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panel2.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel2.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("CONCEPTOS DE PAGO");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jtpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 815, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(251, 251, 251))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpBotones, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .addComponent(jpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jtpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtpEdicion.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 827, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 585, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("Encargados");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

//--------------------------Eventos de componentes----------------------------//
    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        metodos.FiltroJTable(txtBuscar.getText(), cbCampoBuscar.getSelectedIndex(), tbPrincipal);

        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);

        if (tbPrincipal.getRowCount() == 1) {
            lbCantRegistros.setText(tbPrincipal.getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistros.setText(tbPrincipal.getRowCount() + " Registros encontrados");
        }
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        RegistroNuevoModificar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        ModoEdicion(false);
        Limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void btnNuevoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoActionPerformed
        Limpiar();
        ModoEdicion(true);
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        ModoEdicion(true);
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        RegistroEliminar();
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnGuardar.doClick();
        }
    }//GEN-LAST:event_btnGuardarKeyPressed

    private void tbPrincipalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMousePressed
        if (tbPrincipal.isEnabled() == true) {
            btnModificar.setEnabled(true);
            btnEliminar.setEnabled(true);

            ModoVistaPrevia();
        }
    }//GEN-LAST:event_tbPrincipalMousePressed

    private void txtDescripcionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyTyped
        metodostxt.SoloTextoKeyTyped(evt);

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtDescripcion, 30);
    }//GEN-LAST:event_txtDescripcionKeyTyped

    private void txtDescripcionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDescripcionKeyReleased
        metodostxt.TxtMayusKeyReleased(txtDescripcion, evt);
    }//GEN-LAST:event_txtDescripcionKeyReleased

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            ModoVistaPrevia();
        }
    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void chbNovActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chbNovActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_chbNovActionPerformed

    boolean valor;
    private void cbTipoImporteItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbTipoImporteItemStateChanged
        if (cbTipoImporte.isEnabled()) {
            if (cbTipoImporte.getSelectedItem().equals("FIJO")) {
                valor = true;
            } else {
                if (cbTipoImporte.getSelectedItem().equals("VARIABLE")) {
                    valor = false;
                    txtImporte.setText("0");
                }
            }

            txtImporte.setEnabled(valor);
    }//GEN-LAST:event_cbTipoImporteItemStateChanged
    }
    private void cbTipoPagoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbTipoPagoItemStateChanged
        if (cbTipoPago.isEnabled()) {
            if (cbTipoPago.getSelectedItem().equals("MENSUAL")) {
                sfNumPagos.setEnabled(true);
            } else {
                if (cbTipoPago.getSelectedItem().equals("UNICO")) {
                    sfNumPagos.setValue(1);
                    sfNumPagos.setEnabled(false);
                }
            }
        }
    }//GEN-LAST:event_cbTipoPagoItemStateChanged

    private void txtImporteKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteKeyReleased
        txtImporte.setText(metodostxt.DoubleFormatoSudamericaKeyReleased(txtImporte.getText()));
    }//GEN-LAST:event_txtImporteKeyReleased

    private void txtImporteKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtImporteKeyTyped
        metodostxt.SoloNumeroDecimalKeyTyped(evt, txtImporte);
    }//GEN-LAST:event_txtImporteKeyTyped

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(txtDescripcion);
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
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox cbCampoBuscar;
    private javax.swing.JComboBox<String> cbTipoImporte;
    private javax.swing.JComboBox<String> cbTipoPago;
    private javax.swing.JCheckBox chbAbr;
    private javax.swing.JCheckBox chbAgo;
    private javax.swing.JCheckBox chbDic;
    private javax.swing.JCheckBox chbEne;
    private javax.swing.JCheckBox chbFeb;
    private javax.swing.JCheckBox chbJul;
    private javax.swing.JCheckBox chbJun;
    private javax.swing.JCheckBox chbMar;
    private javax.swing.JCheckBox chbMay;
    private javax.swing.JCheckBox chbNov;
    private javax.swing.JCheckBox chbOct;
    private javax.swing.JCheckBox chbSep;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpBotones2;
    private javax.swing.JPanel jpEdicion;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpTabla;
    private javax.swing.JTabbedPane jtpEdicion;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblDescripcion;
    private javax.swing.JLabel lblImporte;
    private javax.swing.JLabel lblImporte2;
    private javax.swing.JLabel lblImporte3;
    private javax.swing.JLabel lblNumPagos;
    private org.edisoncor.gui.panel.Panel panel2;
    private org.edisoncor.gui.panel.Panel pnMesesAPagar;
    private javax.swing.JScrollPane scPrincipal;
    private com.toedter.components.JSpinField sfNumPagos;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtDescripcion;
    private javax.swing.JTextField txtImporte;
    // End of variables declaration//GEN-END:variables
}
