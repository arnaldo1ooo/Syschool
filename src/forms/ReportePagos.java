/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.awt.Toolkit;
import java.io.InputStream;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
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
public class ReportePagos extends javax.swing.JDialog {

    MetodosCombo metodoscombo = new MetodosCombo();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    Conexion con = new Conexion();
    DefaultTableModel modelTablePagos;
    int totalMasc, totalFem;
    static org.apache.log4j.Logger log_historial = org.apache.log4j.Logger.getLogger(ReportePagos.class.getName());

    public ReportePagos(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        CargarComboBoxes();

        //Obtener Fechas
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1); //Primer dia del mes seleccionado
        System.out.println("Cale " + cal.get(Calendar.MONTH));
        dcDesde.setCalendar(cal);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.getActualMaximum(Calendar.DAY_OF_MONTH)); //Ultimo dia del mes seleccionado
        dcHasta.setCalendar(cal);
    }

    //--------------------------METODOS----------------------------//
    private void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboBox(cbConcepto, "SELECT con_codigo, con_descripcion FROM concepto ORDER BY con_descripcion", 1);
    }

    private void ConsultaListadoAlumnos() {
        modelTablePagos = (DefaultTableModel) tbPrincipal.getModel();//Cargamos campos de jtable al modeltable
        modelTablePagos.setRowCount(0); //Vacia la tabla
        String sentencia = "CALL SP_ReporteListadoAlumnos('" + metodoscombo.ObtenerIDSelectComboBox(cbConcepto) + "', '" + "" + "')";
        String titlesJtabla[] = {"Apellido, Nombre", "Cédula", "Sexo", "Edad"};

        //Carga el combobox con los titulos de la tabla, solo si esta vacio
        if (cbOrdenar != null && cbOrdenar.getItemCount() == 0) {
            javax.swing.DefaultComboBoxModel modelCombo = new javax.swing.DefaultComboBoxModel(titlesJtabla);
            cbOrdenar.setModel(modelCombo);
        }

        con = con.ObtenerRSSentencia(sentencia);

        try {
            String nomape, sexo;
            int cedula, edad;
            totalMasc = 0;
            totalFem = 0;
            while (con.rs.next()) {
                nomape = con.rs.getString("nomape");
                cedula = (con.rs.getInt("alu_cedula"));
                sexo = con.rs.getString("alu_sexo");
                if (sexo.equals("MASCULINO")) {
                    totalMasc = totalMasc + 1;
                }
                if (sexo.equals("FEMENINO")) {
                    totalFem = totalFem + 1;
                }
                edad = con.rs.getInt("edad");

                modelTablePagos.addRow(new Object[]{nomape, cedula, sexo, edad});
            }
            con.DesconectarBasedeDatos();
            tbPrincipal.setModel(modelTablePagos);
            metodos.AnchuraColumna(tbPrincipal);
            lbCantRegistros.setText(tbPrincipal.getRowCount() + " Registros encontrados");
        } catch (SQLException e) {
            log_historial.error("Error 10214: " + e);
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        panel3 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        panel1 = new org.edisoncor.gui.panel.Panel();
        lblFechaPago = new javax.swing.JLabel();
        lblHasta = new javax.swing.JLabel();
        lblDesde = new javax.swing.JLabel();
        dcDesde = new com.toedter.calendar.JDateChooser();
        dcHasta = new com.toedter.calendar.JDateChooser();
        btnFiltrar = new org.edisoncor.gui.button.ButtonSeven();
        cbConcepto = new javax.swing.JComboBox<>();
        lblConcepto = new javax.swing.JLabel();
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
        jLabel11 = new javax.swing.JLabel();
        btnGenerarReporte = new org.edisoncor.gui.button.ButtonSeven();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        panel3.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel3.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REPORTE DE PAGOS");
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

        lblFechaPago.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        lblFechaPago.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaPago.setText("Fecha de pago");

        lblHasta.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHasta.setText("Hasta");

        lblDesde.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDesde.setText("Desde");

        btnFiltrar.setBackground(new java.awt.Color(0, 153, 153));
        btnFiltrar.setText("Filtrar");
        btnFiltrar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFiltrarActionPerformed(evt);
            }
        });

        cbConcepto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbConceptoItemStateChanged(evt);
            }
        });

        lblConcepto.setFont(new java.awt.Font("sansserif", 0, 14)); // NOI18N
        lblConcepto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblConcepto.setText("Concepto");

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addComponent(lblFechaPago)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblDesde, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(dcDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel1Layout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addComponent(lblHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(284, 284, 284)
                        .addComponent(btnFiltrar, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lblConcepto)
                .addGap(2, 2, 2)
                .addComponent(cbConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(39, 39, 39))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGap(27, 27, 27)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblFechaPago, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(lblConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(cbConcepto, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(lblHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(1, 1, 1)
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(dcDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(28, 28, 28)
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

        tbPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbPrincipal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "N° de pago", "Fecha de pago", "Concepto", "Monto"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false
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
                .addContainerGap()
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scPrincipal, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 727, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel4Layout.createSequentialGroup()
                                .addComponent(lblBuscarCampo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbOrdenar, javax.swing.GroupLayout.PREFERRED_SIZE, 174, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lbCantRegistros, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 471, javax.swing.GroupLayout.PREFERRED_SIZE))))
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
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
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
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGap(308, 308, 308)
                .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
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

        //Parametros
        parametros = new HashMap();
        parametros.clear();
        parametros.put("LOGO", logo);
        parametros.put("ORDEN", cbOrdenar.getSelectedItem().toString());
        parametros.put("DOCENTE", "");
        parametros.put("NIVEL", cbConcepto.getSelectedItem().toString());
        parametros.put("PERIODO", "");
        parametros.put("TIPO_NIVEL", "");
        parametros.put("ORDENADOPOR", cbOrdenar.getSelectedItem() + "");
        parametros.put("TOTAL_MASC", totalMasc + "");
        parametros.put("TOTAL_FEM", totalFem + "");
        parametros.put("TOTAL", tbPrincipal.getRowCount() + "");

        rutajasper = "/reportes/reporte_listadoalumnos.jasper";

        //Crea una tabla auxiliar en donde se carga los registros ordenados por cierta en la tabla principal
        JTable tableAuxiliar = new JTable();; //tabla auxiliar
        DefaultTableModel tablemodelAuxiliar = new DefaultTableModel(); //tablemodel de la tabla auxiliar
        tablemodelAuxiliar.setRowCount(0); // Vaciar filas del modelo
        //Establecer el número y el nombre de las columnas en el tablemodel auxiliar
        for (int i = 0; i < tbPrincipal.getColumnCount(); i++) {
            tablemodelAuxiliar.addColumn(tbPrincipal.getColumnName(i));
        }
        //Cargamos los registros ordenados al modelo auxiliar
        for (int i = 0; i < tbPrincipal.getRowCount(); i++) {
            Object fila[] = new Object[tbPrincipal.getColumnCount()];
            for (int j = 0; j < tbPrincipal.getColumnCount(); j++) {
                fila[j] = tbPrincipal.getValueAt(i, j);
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
        // TODO add your handling code here:
    }//GEN-LAST:event_btnFiltrarActionPerformed

    private void cbConceptoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbConceptoItemStateChanged
//        ConsultaListadoAlumnos();
    }//GEN-LAST:event_cbConceptoItemStateChanged

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
            log_historial.error("Error 10221: " + e);
            e.printStackTrace();
        }

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                ReportePagos dialog = new ReportePagos(new javax.swing.JFrame(), true);
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
    private javax.swing.JComboBox<MetodosCombo> cbConcepto;
    private javax.swing.JComboBox cbOrdenar;
    private com.toedter.calendar.JDateChooser dcDesde;
    private com.toedter.calendar.JDateChooser dcHasta;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JPanel jpPrincipal;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblConcepto;
    private javax.swing.JLabel lblDesde;
    private javax.swing.JLabel lblFechaPago;
    private javax.swing.JLabel lblHasta;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel3;
    private org.edisoncor.gui.panel.Panel panel4;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbPrincipal;
    // End of variables declaration//GEN-END:variables
}
