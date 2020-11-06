/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.awt.Toolkit;
import java.sql.SQLException;
import javax.swing.JOptionPane;
import static login.Login.codUsuario;
import utilidades.Metodos;
import utilidades.MetodosTXT;

public class PagoSalarial extends javax.swing.JDialog {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();

    public PagoSalarial(javax.swing.JFrame parent, boolean eliminar) {
        super(parent);
        initComponents();

        //Metodos
        CargarFiltroAnho();
        ConsultaAllPagoSal(cbFiltroAnho.getSelectedItem() + "");

        if (eliminar == false) {
            //Oculta los botones si no es para eliminar pago
            btnEliminar.setVisible(eliminar);
        } else {
            //Permiso Roles de usuario
            String permisos = metodos.PermisoRol(codUsuario, "PAGO_SALARIO");
            btnEliminar.setVisible(permisos.contains("B"));
        }
    }

    private void CargarFiltroAnho() {
        try {
            cbFiltroAnho.addItem("TODOS");
            con = con.ObtenerRSSentencia("SELECT YEAR(pasal_fecha) AS anho FROM pago_salario GROUP BY(pasal_fecha) ORDER BY pasal_fecha DESC");
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

    private void ConsultaAllPagoSal(String anho) {
        String sentencia = "CALL SP_PagoSalarioConsultaPorAnho('" + anho + "')";
        String titlesJtabla[] = {"Código", "N° de pago", "Funcionario", "Salario", "Fecha", "Mes", "Obs"};
        tbPrincipal.setModel(con.ConsultaTableBD(sentencia, titlesJtabla, cbCampoBuscar));

        double salario;
        for (int i = 0; i < tbPrincipal.getRowCount(); i++) {
            salario = Double.parseDouble(tbPrincipal.getValueAt(i, 3) + "");
            salario = metodostxt.FormatearATresDecimales(salario);
            tbPrincipal.setValueAt(metodostxt.DoubleAFormatoSudamerica(salario), i, 3);
        }

        metodos.AnchuraColumna(tbPrincipal);
        cbCampoBuscar.setSelectedIndex(2);

        if (tbPrincipal.getModel().getRowCount() == 1) {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registros encontrados");
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panel1 = new org.edisoncor.gui.panel.Panel();
        jLabel10 = new javax.swing.JLabel();
        txtBuscar = new javax.swing.JTextField();
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
        cbCampoBuscar = new javax.swing.JComboBox();
        lblBuscarCampo2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ventana Pagos de salario");
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
        labelMetric2.setText("PAGOS DE SALARIO");
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
        btnEliminar.setText("Eliminar pago salarial");
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
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 226, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(lblBuscarCampo1)
                                .addComponent(cbFiltroAnho, javax.swing.GroupLayout.PREFERRED_SIZE, 139, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblBuscarCampo2)
                                    .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 168, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(165, 165, 165))))
                    .addComponent(scPrincipal)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 274, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(117, 117, 117))
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(271, 271, 271)
                .addComponent(btnEliminar)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(19, 19, 19)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtBuscar, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblBuscarCampo2)
                            .addComponent(lblBuscarCampo1))
                        .addGap(2, 2, 2)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(cbFiltroAnho, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(4, 4, 4)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 181, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 18, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, 718, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void tbPrincipalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMousePressed

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
            int confirmado = javax.swing.JOptionPane.showConfirmDialog(this, "¿Realmente desea anular este pago salarial?", "Confirmación", JOptionPane.YES_OPTION);
            if (confirmado == JOptionPane.YES_OPTION) {
                String codigo = tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0).toString();
                //Elimina el pago (Primero se debe eliminar los conceptos del pago)
                String sentencia = "CALL SP_PagoSalarioEliminar(" + codigo + ")";
                con.EjecutarABM(sentencia, true);

                ConsultaAllPagoSal(cbFiltroAnho.getSelectedItem() + "");//Actualizar tabla
            }
        }
    }//GEN-LAST:event_btnEliminarActionPerformed

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased

    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked

    }//GEN-LAST:event_tbPrincipalMouseClicked

    private void cbFiltroAnhoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbFiltroAnhoItemStateChanged
        ConsultaAllPagoSal(cbFiltroAnho.getSelectedItem() + "");
    }//GEN-LAST:event_cbFiltroAnhoItemStateChanged

    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                PagoSalarial dialog = new PagoSalarial(new javax.swing.JFrame(), false);
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
    private javax.swing.JButton btnEliminar;
    private javax.swing.JComboBox cbCampoBuscar;
    private javax.swing.JComboBox cbFiltroAnho;
    private javax.swing.JLabel jLabel10;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lblBuscarCampo1;
    private javax.swing.JLabel lblBuscarCampo2;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel3;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtBuscar;
    // End of variables declaration//GEN-END:variables
}
