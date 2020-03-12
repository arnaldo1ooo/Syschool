/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static login.Login.Alias;
import utilidades.Metodos;
import utilidades.MetodosTXT;

public class Pago extends javax.swing.JDialog {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();

    public Pago(javax.swing.JFrame parent, boolean eliminar) {
        super(parent);
        initComponents();

        CargarFiltroAnho();
        ConsultaPagosPorAnho(cbFiltroAnho.getSelectedItem() + "");

        if (eliminar == false) {
            //Oculta los botones si no es para eliminar pago
            btnEliminar.setVisible(eliminar);
        } else {
            //Permiso Roles de usuario
            btnEliminar.setVisible(metodos.PermisoRol(Alias, "PAGO", "BAJA"));
        }
    }

    private void CargarFiltroAnho() {
        try {
            cbFiltroAnho.addItem("TODOS");
            con = con.ObtenerRSSentencia("SELECT YEAR(pag_fechapago) AS anho FROM pago GROUP BY(pag_fechapago) ORDER BY pag_fechapago DESC");
            while (con.rs.next()) {
                cbFiltroAnho.addItem(con.rs.getString("anho"));
            }
            if (cbFiltroAnho.getItemCount() == 1) {
                cbFiltroAnho.setSelectedIndex(0);
            } else {
                cbFiltroAnho.setSelectedIndex(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    private void ConsultaPagosPorAnho(String anho) {
        String sentencia = "CALL SP_PagoConsultaPorAnho('" + anho + "')";
        String titlesJtabla[] = {"Código", "N° de pago", "Apoderado", "Fecha de pago", "Importe", "Total"};
        tbPrincipal.setModel(con.ConsultaTableBD(sentencia, titlesJtabla, cbCampoBuscar));

        double importe;
        double total;
        for (int i = 0; i < tbPrincipal.getRowCount(); i++) {
            importe = Double.parseDouble(tbPrincipal.getValueAt(i, 4) + "");
            importe = metodostxt.FormatearATresDecimales(importe);
            tbPrincipal.setValueAt(metodostxt.DoubleAFormatoSudamerica(importe), i, 4);

            total = Double.parseDouble(tbPrincipal.getValueAt(i, 5) + "");
            total = metodostxt.FormatearATresDecimales(total);
            tbPrincipal.setValueAt(metodostxt.DoubleAFormatoSudamerica(total), i, 5);
        }
        metodos.AnchuraColumna(tbPrincipal);
        cbCampoBuscar.setSelectedIndex(1);

        if (tbPrincipal.getModel().getRowCount() == 1) {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registros encontrados");
        }
    }

    private void ConceptosDelPago() {
        int codPagoSelect = Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0).toString());
        String sentencia = "CALL SP_PagoConceptosConsulta(" + codPagoSelect + ")";
        String titlesJtabla[] = {"Concepto", "N° de cuotas pagadas", "Meses", "Monto", "Subtotal"};

        tbConceptosPagados.setModel(con.ConsultaTableBD(sentencia, titlesJtabla, null));

        //Convertir monto y subtotal
        double monto;
        double subtotal;
        for (int i = 0; i < tbConceptosPagados.getRowCount(); i++) {
            monto = Double.parseDouble(tbConceptosPagados.getValueAt(i, 3).toString());
            monto = metodostxt.FormatearATresDecimales(monto);
            tbConceptosPagados.setValueAt(metodostxt.DoubleAFormatoSudamerica(monto), i, 3);
            subtotal = Double.parseDouble(tbConceptosPagados.getValueAt(i, 4).toString());
            subtotal = metodostxt.FormatearATresDecimales(subtotal);
            tbConceptosPagados.setValueAt(metodostxt.DoubleAFormatoSudamerica(subtotal), i, 4);
        }
        metodos.AnchuraColumna(tbConceptosPagados);

        if (tbConceptosPagados.getModel().getRowCount() == 1) {
            lbCantRegistrosProductos.setText(tbConceptosPagados.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistrosProductos.setText(tbConceptosPagados.getModel().getRowCount() + " Registros encontrados");
        }

        if (tbPrincipal.getSelectedRowCount() > 0) {
            String numcompra = tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 1).toString();
            pnProductosComprados.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(),
                    "Conceptos del pago N° " + numcompra));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        brightPassFilter1 = new org.edisoncor.gui.util.BrightPassFilter();
        panel1 = new org.edisoncor.gui.panel.Panel();
        jLabel10 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
        cbCampoBuscar = new javax.swing.JComboBox();
        pnProductosComprados = new javax.swing.JPanel();
        scPrincipal1 = new javax.swing.JScrollPane();
        tbConceptosPagados = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lbCantRegistrosProductos = new javax.swing.JLabel();
        scPrincipal = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lbCantRegistros = new javax.swing.JLabel();
        panel3 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        btnEliminar = new javax.swing.JButton();
        cbFiltroAnho = new javax.swing.JComboBox();
        lblBuscarCampo1 = new javax.swing.JLabel();
        lblBuscarCampo2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pagos");
        setModal(true);
        setResizable(false);

        panel1.setColorPrimario(new java.awt.Color(233, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(255, 255, 255));

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoBuscar.png"))); // NOI18N
        jLabel10.setText("  BUSCAR ");
        jLabel10.setIconTextGap(1);

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

        pnProductosComprados.setBackground(new java.awt.Color(255, 255, 255));
        pnProductosComprados.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Conceptos del pago N° 000000"));

        tbConceptosPagados.setAutoCreateRowSorter(true);
        tbConceptosPagados.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbConceptosPagados.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbConceptosPagados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbConceptosPagados.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbConceptosPagados.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbConceptosPagados.setEnabled(false);
        tbConceptosPagados.setGridColor(new java.awt.Color(0, 153, 204));
        tbConceptosPagados.setOpaque(false);
        tbConceptosPagados.setRowHeight(20);
        tbConceptosPagados.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbConceptosPagados.getTableHeader().setReorderingAllowed(false);
        scPrincipal1.setViewportView(tbConceptosPagados);

        lbCantRegistrosProductos.setFont(new java.awt.Font("Arial", 1, 13)); // NOI18N
        lbCantRegistrosProductos.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistrosProductos.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistrosProductos.setText("0 Registros encontrados");
        lbCantRegistrosProductos.setPreferredSize(new java.awt.Dimension(57, 25));

        javax.swing.GroupLayout pnProductosCompradosLayout = new javax.swing.GroupLayout(pnProductosComprados);
        pnProductosComprados.setLayout(pnProductosCompradosLayout);
        pnProductosCompradosLayout.setHorizontalGroup(
            pnProductosCompradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnProductosCompradosLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnProductosCompradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnProductosCompradosLayout.createSequentialGroup()
                        .addComponent(scPrincipal1, javax.swing.GroupLayout.DEFAULT_SIZE, 804, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnProductosCompradosLayout.createSequentialGroup()
                        .addComponent(lbCantRegistrosProductos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18))))
        );
        pnProductosCompradosLayout.setVerticalGroup(
            pnProductosCompradosLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnProductosCompradosLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scPrincipal1, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(lbCantRegistrosProductos, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

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
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tbPrincipalMouseClicked(evt);
            }
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

        lbCantRegistros.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lbCantRegistros.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistros.setText("0 Registros encontrados");
        lbCantRegistros.setPreferredSize(new java.awt.Dimension(57, 25));

        panel3.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel3.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("PAGOS");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        btnEliminar.setBackground(new java.awt.Color(229, 11, 11));
        btnEliminar.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        btnEliminar.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminar.setText("Eliminar pago");
        btnEliminar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarActionPerformed(evt);
            }
        });

        cbFiltroAnho.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbFiltroAnhoItemStateChanged(evt);
            }
        });

        lblBuscarCampo1.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        lblBuscarCampo1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblBuscarCampo1.setText("Filtrar por año");

        lblBuscarCampo2.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        lblBuscarCampo2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblBuscarCampo2.setText("Buscar por");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(344, 344, 344))
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(pnProductosComprados, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addGap(0, 0, Short.MAX_VALUE)
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblBuscarCampo1)
                                    .addComponent(cbFiltroAnho, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(scPrincipal, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(lbCantRegistros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(15, 15, 15)))
                        .addGap(10, 10, 10))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(388, 388, 388))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblBuscarCampo2)
                            .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(175, 175, 175))))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblBuscarCampo2)
                    .addComponent(lblBuscarCampo1))
                .addGap(2, 2, 2)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbFiltroAnho, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscar)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnProductosComprados, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tbPrincipalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMousePressed
        int filaselect = tbPrincipal.getSelectedRow();
        if (tbPrincipal.getRowCount() > 0) {
            ConceptosDelPago();
        }
    }//GEN-LAST:event_tbPrincipalMousePressed

    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        metodos.FiltroJTable(txtBuscar.getText(), cbCampoBuscar.getSelectedIndex(), tbPrincipal);
    }//GEN-LAST:event_txtBuscarKeyReleased

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if (tbPrincipal.getSelectedRow() == -1) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtBuscar.requestFocus();
        } else {
            int confirmado = javax.swing.JOptionPane.showConfirmDialog(this, "¿Realmente desea anular este pago?", "Confirmación", JOptionPane.YES_OPTION);
            if (confirmado == JOptionPane.YES_OPTION) {
                String codpago = tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0).toString();

                try {
                    //Elimina los conceptos del pago  
                    String sentencia;
                    sentencia = "CALL SP_PagoConceptosEliminar('" + codpago + "')";
                    con.EjecutarABM(sentencia, false);

                    //Elimina el pago (Primero se debe eliminar los conceptos del pago)
                    sentencia = "CALL SP_PagoEliminar(" + codpago + ")";
                    con.EjecutarABM(sentencia, false);
                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }

                ConsultaPagosPorAnho(cbFiltroAnho.getSelectedItem() + ""); //Actualizar tabla
                tbConceptosPagados.setModel(new DefaultTableModel()); //Vaciar tabla
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Pago anulado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            ConceptosDelPago();
        }
    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked

    }//GEN-LAST:event_tbPrincipalMouseClicked

    private void cbFiltroAnhoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFiltroAnhoItemStateChanged
        ConsultaPagosPorAnho(cbFiltroAnho.getSelectedItem() + "");
    }//GEN-LAST:event_cbFiltroAnhoItemStateChanged

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                Pago dialog = new Pago(new javax.swing.JFrame(), false);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private org.edisoncor.gui.util.BrightPassFilter brightPassFilter1;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JComboBox cbCampoBuscar;
    private javax.swing.JComboBox cbFiltroAnho;
    private javax.swing.JLabel jLabel10;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lbCantRegistrosProductos;
    private javax.swing.JLabel lblBuscarCampo1;
    private javax.swing.JLabel lblBuscarCampo2;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel3;
    private javax.swing.JPanel pnProductosComprados;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JScrollPane scPrincipal1;
    private javax.swing.JTable tbConceptosPagados;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
