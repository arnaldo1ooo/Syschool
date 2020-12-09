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
import static login.Login.codUsuario;
import org.oxbow.swingbits.table.filter.TableRowFilterSupport;
import utilidades.Metodos;
import utilidades.MetodosTXT;

public class Pago extends javax.swing.JDialog {

    private Conexion con = new Conexion();
    private Metodos metodos = new Metodos();
    private MetodosTXT metodostxt = new MetodosTXT();
    private DefaultTableModel modelTablePago;
    private DefaultTableModel modelTablePagoConceptos;

    public Pago(javax.swing.JFrame parent, boolean eliminar) {
        super(parent);
        initComponents();

        if (eliminar == false) {
            //Oculta los botones si no es para eliminar pago
            btnEliminar.setVisible(eliminar);
        } else {
            //Permiso Roles de usuario
            String permisos = metodos.PermisoRol(codUsuario, "PAGO");
            btnEliminar.setVisible(permisos.contains("B"));
        }
        TableRowFilterSupport.forTable(tbPrincipal).searchable(true).apply(); //Activar filtrado de tabla click derecho en cabecera
        TableRowFilterSupport.forTable(tbConceptosPagados).searchable(true).apply(); //Activar filtrado de tabla click derecho en cabecera
        ConsultaPagos();
    }

    private void ConsultaPagos() {
        modelTablePago = (DefaultTableModel) tbPrincipal.getModel();
        modelTablePago.setRowCount(0); //Vacia tabla

        try {
            String sentencia = "CALL SP_PagoConsulta()";
            con = con.ObtenerRSSentencia(sentencia);
            int codigo, periodo;
            String apoderado, fechapago, numpago;
            double importe, total;
            while (con.getResultSet().next()) {
                codigo = con.getResultSet().getInt("pag_codigo");
                numpago = con.getResultSet().getString("pag_numpago");
                apoderado = con.getResultSet().getString("nomape");
                fechapago = con.getResultSet().getString("fechapago");
                importe = metodostxt.FormatearATresDecimales(con.getResultSet().getDouble("pag_importe"));
                total = metodostxt.FormatearATresDecimales(con.getResultSet().getDouble("totalpago"));
                periodo = con.getResultSet().getInt("pag_periodo");

                modelTablePago.addRow(new Object[]{codigo, numpago, apoderado, fechapago, importe, total, periodo});
            }
            con.DesconectarBasedeDatos();
            tbPrincipal.setModel(modelTablePago);
            metodos.AnchuraColumna(tbPrincipal);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }

        if (tbPrincipal.getModel().getRowCount() == 1) {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registros encontrados");
        }
    }

    private void ConsultaConceptoPagos() {
        modelTablePagoConceptos = (DefaultTableModel) tbConceptosPagados.getModel();
        modelTablePagoConceptos.setRowCount(0); //Vacia tabla
        try {
            int idPagoSelect = (int) tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0);
            String sentencia = "CALL SP_PagoConceptosConsulta(" + idPagoSelect + ")";
            con = con.ObtenerRSSentencia(sentencia);
            String concepto, meses;
            int numcuotaspagadas;
            double monto, subtotal;
            while (con.getResultSet().next()) {
                concepto = con.getResultSet().getString("con_descripcion");
                numcuotaspagadas = con.getResultSet().getInt("pagcon_numcuotas");
                meses = con.getResultSet().getString("pagcon_meses");
                monto = con.getResultSet().getDouble("pagcon_monto");
                subtotal = con.getResultSet().getDouble("subtotal");

                modelTablePagoConceptos.addRow(new Object[]{concepto, numcuotaspagadas, meses, monto, subtotal});
            }
            tbConceptosPagados.setModel(modelTablePagoConceptos);
            metodos.AnchuraColumna(tbConceptosPagados);

        } catch (Exception e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();

        if (tbConceptosPagados.getModel().getRowCount() == 1) {
            lbCantRegistrosProductos.setText(tbConceptosPagados.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistrosProductos.setText(tbConceptosPagados.getModel().getRowCount() + " Registros encontrados");
        }

        if (tbPrincipal.getSelectedRowCount() > 0) {
            String numcompra = tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 1).toString();
            pnProductosComprados.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Conceptos del pago N° " + numcompra));
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        brightPassFilter1 = new org.edisoncor.gui.util.BrightPassFilter();
        panel1 = new org.edisoncor.gui.panel.Panel();
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

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Pagos");
        setModal(true);
        setResizable(false);

        panel1.setColorPrimario(new java.awt.Color(233, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(255, 255, 255));

        pnProductosComprados.setBackground(new java.awt.Color(255, 255, 255));
        pnProductosComprados.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Conceptos del pago N° 000000"));

        scPrincipal1.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tbConceptosPagados.setAutoCreateRowSorter(true);
        tbConceptosPagados.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbConceptosPagados.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbConceptosPagados.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Concepto", "N° de cuotas pagadas", "Meses", "Monto", "Subtotal"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.Double.class, java.lang.Double.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
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
                        .addComponent(scPrincipal1)
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

        scPrincipal.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tbPrincipal.setAutoCreateRowSorter(true);
        tbPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbPrincipal.setFont(new java.awt.Font("Tahoma", 0, 14)); // NOI18N
        tbPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "N° de pago", "Apoderado", "Fecha de pago", "Importe", "Total", "Periodo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Object.class, java.lang.Double.class, java.lang.Double.class, java.lang.Integer.class
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
                        .addComponent(lbCantRegistros, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(25, 25, 25))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 847, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 230, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        if (tbPrincipal.getRowCount() > 0) {
            ConsultaConceptoPagos();
        }
    }//GEN-LAST:event_tbPrincipalMousePressed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        if (tbPrincipal.getSelectedRow() == -1) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
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

                ConsultaPagos();
                tbConceptosPagados.setModel(new DefaultTableModel()); //Vaciar tabla
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "Pago anulado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            ConsultaConceptoPagos();
        }
    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked

    }//GEN-LAST:event_tbPrincipalMouseClicked

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
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lbCantRegistrosProductos;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel3;
    private javax.swing.JPanel pnProductosComprados;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JScrollPane scPrincipal1;
    private javax.swing.JTable tbConceptosPagados;
    private javax.swing.JTable tbPrincipal;
    // End of variables declaration//GEN-END:variables
}
