/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import dao.DAO;
import java.awt.Toolkit;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;

/**
 *
 * @author Arnaldo_Cantero
 */
public class ReporteMatriculados extends javax.swing.JDialog {

    MetodosCombo metodoscombo = new MetodosCombo();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    DAO con = new DAO();
    DefaultTableModel modelTablaMatriculados;
    static org.apache.log4j.Logger log_historial = org.apache.log4j.Logger.getLogger(ReporteMatriculados.class.getName());

    public ReporteMatriculados(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        //Obtener Fechas
        Calendar cal = Calendar.getInstance();
        spPeriodo.setValue(cal.get(Calendar.YEAR));
    }

    //--------------------------METODOS----------------------------//
    private void ConsultaMatriculados() {
        modelTablaMatriculados = (DefaultTableModel) tbPrincipal.getModel();//Cargamos campos de jtable al modeltable
        modelTablaMatriculados.setRowCount(0); //Vacia la tabla

        //Carga el combobox con los titulos de la tabla, solo si esta vacio
        if (cbOrdenar != null && cbOrdenar.getItemCount() == 0) {
            for (int i = 0; i < tbPrincipal.getColumnCount(); i++) {
                cbOrdenar.addItem(tbPrincipal.getColumnName(i));
            }
            cbOrdenar.setSelectedIndex(0);
        }

        String sentencia = "CALL SP_ReporteMatriculados('" + spPeriodo.getValue() + "')";

        try {
            con = con.ObtenerRSSentencia(sentencia);
            String nivel, docente;
            int codigo, masc, fem, total, totales = 0;
            while (con.getResultSet().next()) {
                codigo = con.getResultSet().getInt("niv_codigo");
                nivel = con.getResultSet().getString("nivel");
                docente = con.getResultSet().getString("docente");
                masc = con.getResultSet().getInt("cantmasc");
                fem = con.getResultSet().getInt("cantfem");
                total = con.getResultSet().getInt("totalmatriculados");
                totales = totales + total;
                modelTablaMatriculados.addRow(new Object[]{codigo, nivel, docente, masc, fem, total});
            }

            lblTotal.setText(totales + "");
            lblTotal1.setText("Total de alumnos en el periodo " + spPeriodo.getValue() + ":");
            tbPrincipal.setModel(modelTablaMatriculados);
            metodos.AnchuraColumna(tbPrincipal);
            metodos.OrdenarColumna(tbPrincipal, cbOrdenar.getSelectedIndex());
            lbCantRegistros.setText(tbPrincipal.getRowCount() + " Registros encontrados");
        } catch (SQLException e) {
            log_historial.error("Error 10218: " + e);
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        panel3 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        panel1 = new org.edisoncor.gui.panel.Panel();
        lblFechaPago = new javax.swing.JLabel();
        btnFiltrar = new org.edisoncor.gui.button.ButtonSeven();
        spPeriodo = new javax.swing.JSpinner();
        panel4 = new org.edisoncor.gui.panel.Panel();
        lblBuscarCampo = new javax.swing.JLabel();
        cbOrdenar = new javax.swing.JComboBox();
        scPrincipal = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lbCantRegistros = new javax.swing.JLabel();
        lblTotal = new javax.swing.JLabel();
        lblTotal1 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnGenerarReporte = new org.edisoncor.gui.button.ButtonSeven();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ventana reporte de gastos");

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        panel3.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel3.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REPORTE DE MATRICULAS");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 22)); // NOI18N

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel3Layout.setVerticalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        panel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel1.setColorPrimario(new java.awt.Color(233, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(255, 255, 255));

        lblFechaPago.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        lblFechaPago.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaPago.setText("Periodo");

        btnFiltrar.setBackground(new java.awt.Color(0, 153, 153));
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        spPeriodo.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        spPeriodo.setModel(new javax.swing.SpinnerNumberModel(2000, 2000, 2400, 1));

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(298, 298, 298)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(lblFechaPago)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(spPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(spPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        panel4.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        panel4.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel4.setColorSecundario(new java.awt.Color(233, 255, 255));

        lblBuscarCampo.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo.setText("Ordenar por:");

        cbOrdenar.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbOrdenarItemStateChanged(evt);
            }
        });

        scPrincipal.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tbPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbPrincipal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Nivel", "Docente", "Masculino", "Femenino", "Total"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.Integer.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
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
        tbPrincipal.setEnabled(false);
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

        lbCantRegistros.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lbCantRegistros.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistros.setText("0 Registros encontrados");
        lbCantRegistros.setPreferredSize(new java.awt.Dimension(57, 25));

        lblTotal.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        lblTotal.setForeground(new java.awt.Color(0, 0, 0));
        lblTotal.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblTotal.setText("0");
        lblTotal.setPreferredSize(new java.awt.Dimension(57, 25));

        lblTotal1.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lblTotal1.setForeground(new java.awt.Color(0, 0, 0));
        lblTotal1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTotal1.setText("Total de alumnos matriculados en el periodo XXXX:");
        lblTotal1.setPreferredSize(new java.awt.Dimension(57, 25));

        javax.swing.GroupLayout panel4Layout = new javax.swing.GroupLayout(panel4);
        panel4.setLayout(panel4Layout);
        panel4Layout.setHorizontalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lbCantRegistros, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 424, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 727, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                        .addComponent(lblBuscarCampo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbOrdenar, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                        .addComponent(lblTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 398, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panel4Layout.setVerticalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbOrdenar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscarCampo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 241, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTotal, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTotal1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jLabel11.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        jLabel11.setText("FILTRAR POR:");

        btnGenerarReporte.setBackground(new java.awt.Color(0, 153, 153));
        btnGenerarReporte.setText("Generar reporte");
        btnGenerarReporte.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGenerarReporteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jLabel11)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(308, 308, 308))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 610, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarReporteActionPerformed
        if (tbPrincipal.getRowCount() <= 0) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "No existe ningún registro en la tabla", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Map parametros;
        String rutajasper;
        InputStream logo = this.getClass().getResourceAsStream("/reportes/images/logo_ace.jpg");
        InputStream logo2 = this.getClass().getResourceAsStream("/reportes/images/logo_sanroque.png");

        //Parametros
        parametros = new HashMap();
        parametros.clear();
        parametros.put("LOGO", logo);
        parametros.put("LOGO2", logo2);

        parametros.put("ORDENADOPOR", cbOrdenar.getSelectedItem() + "");
        parametros.put("PERIODO", spPeriodo.getValue() + "");
        parametros.put("TOTALMAT", lblTotal.getText());

        rutajasper = "/reportes/reporte_matriculas.jasper";

        //Crea una tabla auxiliar en donde se carga los registros ordenados por cierta en la tabla principal
        JTable tableAuxiliar = new JTable(); //tabla auxiliar
        DefaultTableModel tablemodelAuxiliar = new DefaultTableModel(); //tablemodel de la tabla auxiliar
        tablemodelAuxiliar.setRowCount(0); // Vaciar filas del modelo
        //Establecer el número y el nombre de las columnas en el tablemodel auxiliar
        for (int i = 0; i < tbPrincipal.getColumnCount(); i++) {
            tablemodelAuxiliar.addColumn(tbPrincipal.getColumnName(i));
        }
        //Cargamos los registros ordenados al modelo auxiliar
        for (int f = 0; f < tbPrincipal.getRowCount(); f++) {
            Object fila[] = new Object[tbPrincipal.getColumnCount()];
            for (int c = 0; c < tbPrincipal.getColumnCount(); c++) {
                fila[c] = tbPrincipal.getValueAt(f, c);
            }
            tablemodelAuxiliar.addRow(fila);
        }
        tableAuxiliar.setModel(tablemodelAuxiliar); // Asignamos el tablemodel auxiliar a la tabla auxiliar

        metodos.GenerarReporteJTABLE(rutajasper, parametros, tableAuxiliar.getModel());
    }//GEN-LAST:event_btnGenerarReporteActionPerformed

    private void cbOrdenarItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbOrdenarItemStateChanged
        metodos.OrdenarColumna(tbPrincipal, cbOrdenar.getSelectedIndex());
    }//GEN-LAST:event_cbOrdenarItemStateChanged

    private void tbPrincipalMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMousePressed

    }//GEN-LAST:event_tbPrincipalMousePressed

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased

    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void btnFiltrarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFiltrarActionPerformed
        ConsultaMatriculados();
    }//GEN-LAST:event_btnFiltrarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
            log_historial.error("Error 10221: " + e);
            e.printStackTrace();
        }

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ReporteMatriculados dialog = new ReporteMatriculados(new javax.swing.JFrame(), true);
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
    private org.edisoncor.gui.button.ButtonSeven btnFiltrar;
    private org.edisoncor.gui.button.ButtonSeven btnGenerarReporte;
    private javax.swing.JComboBox cbOrdenar;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JPanel jpPrincipal;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblFechaPago;
    private javax.swing.JLabel lblTotal;
    private javax.swing.JLabel lblTotal1;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel3;
    private org.edisoncor.gui.panel.Panel panel4;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JSpinner spPeriodo;
    private javax.swing.JTable tbPrincipal;
    // End of variables declaration//GEN-END:variables
}
