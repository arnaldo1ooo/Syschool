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
import java.util.HashMap;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import org.jdesktop.swingx.JXFindBar;
import org.jdesktop.swingx.JXTable;
import org.oxbow.swingbits.table.filter.TableRowFilterSupport;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;

/**
 *
 * @author Arnaldo_Cantero
 */
public class ReporteAlumnosEyC extends javax.swing.JDialog {

    MetodosCombo metodoscombo = new MetodosCombo();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    DAO con = new DAO();
    DefaultTableModel modelTableListadoAlumnos;
    int totalMasc, totalFem;
    static org.apache.log4j.Logger log_historial = org.apache.log4j.Logger.getLogger(ReporteAlumnosEyC.class.getName());

    public ReporteAlumnosEyC(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        metodos.AnchuraColumna(tbPrincipal);
        TableRowFilterSupport.forTable(tbPrincipal).searchable(true).apply(); //Activar filtrado de tabla click derecho en cabecera
    }

    //--------------------------METODOS----------------------------//
    private void ConsultaListadoAlumnos() {
        modelTableListadoAlumnos = (DefaultTableModel) tbPrincipal.getModel();//Cargamos campos de jtable al modeltable
        modelTableListadoAlumnos.setRowCount(0); //Vacia la tabla

        String sentencia = "";
        switch (cbEyoC.getSelectedIndex()) {
            case 0 -> {
                sentencia = "CALL SP_ReporteListadoAlumnosEyC('0', '60', '" + dyAnho.getYear() + "')";
                break;
            }

            case 1 -> {
                sentencia = "CALL SP_ReporteListadoAlumnosEyC('61', '69', '" + dyAnho.getYear() + "')";
                break;
            }

            case 2 -> {
                sentencia = "CALL SP_ReporteListadoAlumnosEyC('0', '69', '" + dyAnho.getYear() + "')";
                break;
            }
            default -> {
                System.out.println("Error switch");
                break;
            }
        }

        String titlesJtabla[] = {"Apellido, Nombre", "Cédula", "Nivel", "Sexo", "Edad"};

        //Carga el combobox con los titulos de la tabla, solo si esta vacio
        if (cbOrdenar != null && cbOrdenar.getItemCount() == 0) {
            javax.swing.DefaultComboBoxModel modelCombo = new javax.swing.DefaultComboBoxModel(titlesJtabla);
            cbOrdenar.setModel(modelCombo);
        }

        try {
            con = con.ObtenerRSSentencia(sentencia);
            String nomape, nivel, sexo;
            int cedula, edad;
            totalMasc = 0;
            totalFem = 0;
            while (con.getResultSet().next()) {
                nomape = con.getResultSet().getString("nomape");
                cedula = (con.getResultSet().getInt("alu_cedula"));
                nivel = (con.getResultSet().getString("nivel"));
                sexo = con.getResultSet().getString("alu_sexo");
                if (sexo.equals("MASCULINO")) {
                    totalMasc = totalMasc + 1;
                }
                if (sexo.equals("FEMENINO")) {
                    totalFem = totalFem + 1;
                }
                edad = con.getResultSet().getInt("edad");

                modelTableListadoAlumnos.addRow(new Object[]{nomape, cedula, nivel, sexo, edad});
            }
        } catch (SQLException e) {
            log_historial.error("Error 1099: " + e);
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
        tbPrincipal.setModel(modelTableListadoAlumnos);

        metodos.AnchuraColumna(tbPrincipal);
        metodos.OrdenarColumna(tbPrincipal, cbOrdenar.getSelectedIndex());
        lbCantRegistros.setText(tbPrincipal.getRowCount() + " Registros encontrados");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        panel3 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        panel1 = new org.edisoncor.gui.panel.Panel();
        lblNivel = new javax.swing.JLabel();
        cbEyoC = new javax.swing.JComboBox();
        lblNivel3 = new javax.swing.JLabel();
        dyAnho = new com.toedter.calendar.JYearChooser();
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
        btnOrden = new javax.swing.JButton();
        jLabel11 = new javax.swing.JLabel();
        btnGenerarReporte = new org.edisoncor.gui.button.ButtonSeven();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ventana de listado de alumnos (Escuela y/o Colegio)");

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        panel3.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel3.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REPORTE DE LISTADO DE ALUMNOS (Por escuela y/o colegio)");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 22)); // NOI18N

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
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

        lblNivel.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        lblNivel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNivel.setText("Escuela y/o Colegio");

        cbEyoC.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "ESCUELA (JARDIN - 9° GRADO)", "COLEGIO (1° MEDIA - 3° MEDIA)", "ESCUELA Y COLEGIO" }));
        cbEyoC.setSelectedIndex(2);
        cbEyoC.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbEyoCItemStateChanged(evt);
            }
        });

        lblNivel3.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        lblNivel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNivel3.setText("Periodo");

        dyAnho.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dyAnhoPropertyChange(evt);
            }
        });
        dyAnho.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                dyAnhoKeyReleased(evt);
            }
        });

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 237, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbEyoC, javax.swing.GroupLayout.PREFERRED_SIZE, 222, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(31, 31, 31)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(dyAnho, javax.swing.GroupLayout.DEFAULT_SIZE, 68, Short.MAX_VALUE)
                    .addComponent(lblNivel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNivel3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dyAnho, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbEyoC, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(20, Short.MAX_VALUE))
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
                "Apellido, Nombre", "Cédula", "Nivel", "Sexo", "Edad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class
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

        javax.swing.GroupLayout panel4Layout = new javax.swing.GroupLayout(panel4);
        panel4.setLayout(panel4Layout);
        panel4Layout.setHorizontalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap(361, Short.MAX_VALUE)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                        .addComponent(lblBuscarCampo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbOrdenar, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                        .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(16, 16, 16))))
            .addGroup(panel4Layout.createSequentialGroup()
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 836, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        panel4Layout.setVerticalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbOrdenar, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscarCampo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnOrden, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 232, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
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
                    .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel11))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(361, 361, 361))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 864, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 554, javax.swing.GroupLayout.PREFERRED_SIZE)
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
        parametros.put("EyC", cbEyoC.getSelectedItem().toString());
        parametros.put("ORDENADOPOR", cbOrdenar.getSelectedItem() + "");
        parametros.put("TOTAL_MASC", totalMasc + "");
        parametros.put("TOTAL_FEM", totalFem + "");
        parametros.put("TOTAL", tbPrincipal.getRowCount() + "");

        rutajasper = "/reportes/reporte_listadoalumnosEyC.jasper";

        //Crea una tabla auxiliar en donde se carga los registros ordenados por cierta en la tabla principal
        JTable tableAuxiliar = new JTable();; //tabla auxiliar
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
                if (tbPrincipal.getColumnName(c).equals("Cédula")) {
                    fila[c] = metodostxt.StringPuntosMiles(tbPrincipal.getValueAt(f, c) + "");
                } else {
                    fila[c] = tbPrincipal.getValueAt(f, c);
                }
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

    private void cbEyoCItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbEyoCItemStateChanged
        ConsultaListadoAlumnos();
    }//GEN-LAST:event_cbEyoCItemStateChanged

    private void dyAnhoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_dyAnhoKeyReleased
        ConsultaListadoAlumnos();
    }//GEN-LAST:event_dyAnhoKeyReleased

    private void dyAnhoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dyAnhoPropertyChange
        ConsultaListadoAlumnos();
    }//GEN-LAST:event_dyAnhoPropertyChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException e) {
            log_historial.error("Error 10255: " + e);
            e.printStackTrace();
        }

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ReporteAlumnosEyC dialog = new ReporteAlumnosEyC(new javax.swing.JFrame(), true);
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
    private org.edisoncor.gui.button.ButtonSeven btnGenerarReporte;
    private javax.swing.JButton btnOrden;
    private javax.swing.JComboBox cbEyoC;
    private javax.swing.JComboBox cbOrdenar;
    private com.toedter.calendar.JYearChooser dyAnho;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JPanel jpPrincipal;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblNivel;
    private javax.swing.JLabel lblNivel3;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel3;
    private org.edisoncor.gui.panel.Panel panel4;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbPrincipal;
    // End of variables declaration//GEN-END:variables
}
