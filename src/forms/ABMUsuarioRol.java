/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.sql.SQLException;
import javax.swing.table.DefaultTableModel;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;

/**
 *
 * @author Arnaldo Cantero
 */
public class ABMUsuarioRol extends javax.swing.JDialog {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosCombo metodoscombo = new MetodosCombo();
    DefaultTableModel tablemodelModulos;

    public ABMUsuarioRol(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        CargarComboBoxes();
    }

//--------------------------METODOS----------------------------//
    private void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboBox(cbUsuario, "SELECT usu_codigo, CONCAT(usu_nombre,' ', usu_apellido) AS nomape "
                + "FROM usuario ORDER BY usu_nombre", -1);
    }

    private void ConsultaModulosUsuario(String idusuario) {
        tablemodelModulos = (DefaultTableModel) tbModulosUsuario.getModel();
        tablemodelModulos.setRowCount(0);

        String sentencia = "CALL SP_UsuarioModuloConsulta(" + idusuario + ")";
        con = con.ObtenerRSSentencia(sentencia);

        try {
            String codigo, descripcion;
            while (con.rs.next()) {
                codigo = con.rs.getString("mo_codigo");
                descripcion = con.rs.getString("mo_denominacion");

                tablemodelModulos.addRow(new Object[]{codigo, descripcion});
            }
            tbModulosUsuario.setModel(tablemodelModulos);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    public void RegistroNuevoModificar() {
        /* if (ComprobarCampos() == true) {
            String codigo = txtCodigo.getText();
            String denominacion = txtDenominacion.getText().toUpperCase();

            if (txtCodigo.getText().equals("")) { //NUEVO REGISTRO
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de registrar este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    String sentencia = "CALL SP_ModuloAlta ('" + denominacion + "')";
                    con.EjecutarABM(sentencia, true);

                    TablaConsultaBDAll(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            } else { //MODIFICAR REGISTRO
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de modificar este regitro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    String sentencia = "CALL SP_ModuloModificar(" + codigo + ",'" + denominacion + "')";
                    con.EjecutarABM(sentencia, true);

                    TablaConsultaBDAll(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            }
        }*/
    }

    private void ModoVistaPrevia() {

    }

    private void ModoEdicion(boolean valor) {
        /*txtBuscar.setEnabled(!valor);
        tbModulosUsuario.setEnabled(!valor);
        txtDenominacion.setEnabled(valor);
        btnNuevo.setEnabled(!valor);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);

        txtDenominacion.requestFocus();*/
    }

    private void Limpiar() {
        /*txtCodigo.setText("");
        txtDenominacion.setText("");

        txtBuscar.requestFocus();
        tbModulosUsuario.clearSelection();*/
    }

    //--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jpPrincipal = new javax.swing.JPanel();
        jpEdicion = new javax.swing.JPanel();
        scPrincipal = new javax.swing.JScrollPane();
        tbModulosUsuario = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lblBuscarCampo = new javax.swing.JLabel();
        btnGuardar = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        chbAlta = new javax.swing.JCheckBox();
        chbModificar = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        lblRolesUsuario = new javax.swing.JLabel();
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        cbUsuario = new javax.swing.JComboBox();
        lblSeleccioneUsuario = new javax.swing.JLabel();

        setTitle("Ventana módulos");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpEdicion.setBackground(new java.awt.Color(233, 255, 255));
        jpEdicion.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        tbModulosUsuario.setAutoCreateRowSorter(true);
        tbModulosUsuario.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbModulosUsuario.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbModulosUsuario.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Descripción"
            }
        ));
        tbModulosUsuario.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbModulosUsuario.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbModulosUsuario.setGridColor(new java.awt.Color(0, 153, 204));
        tbModulosUsuario.setOpaque(false);
        tbModulosUsuario.setRowHeight(20);
        tbModulosUsuario.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbModulosUsuario.getTableHeader().setReorderingAllowed(false);
        scPrincipal.setViewportView(tbModulosUsuario);

        lblBuscarCampo.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        lblBuscarCampo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampo.setText("Módulos que puede acceder el usuario");

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

        jPanel1.setBackground(new java.awt.Color(0, 102, 102));
        jPanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        chbAlta.setFont(new java.awt.Font("sansserif", 1, 15)); // NOI18N
        chbAlta.setForeground(new java.awt.Color(255, 255, 255));
        chbAlta.setText("ALTA");
        chbAlta.setEnabled(false);

        chbModificar.setFont(new java.awt.Font("sansserif", 1, 15)); // NOI18N
        chbModificar.setForeground(new java.awt.Color(255, 255, 255));
        chbModificar.setText("MODIFICAR");
        chbModificar.setEnabled(false);

        jCheckBox3.setFont(new java.awt.Font("sansserif", 1, 15)); // NOI18N
        jCheckBox3.setForeground(new java.awt.Color(255, 255, 255));
        jCheckBox3.setText("BAJA");
        jCheckBox3.setEnabled(false);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(chbModificar)
                    .addComponent(chbAlta, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox3, javax.swing.GroupLayout.Alignment.LEADING))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(17, Short.MAX_VALUE)
                .addComponent(chbAlta)
                .addGap(18, 18, 18)
                .addComponent(chbModificar)
                .addGap(18, 18, 18)
                .addComponent(jCheckBox3)
                .addGap(15, 15, 15))
        );

        lblRolesUsuario.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        lblRolesUsuario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRolesUsuario.setText("Roles del módulo X");

        javax.swing.GroupLayout jpEdicionLayout = new javax.swing.GroupLayout(jpEdicion);
        jpEdicion.setLayout(jpEdicionLayout);
        jpEdicionLayout.setHorizontalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblBuscarCampo, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 80, Short.MAX_VALUE)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnGuardar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblRolesUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(20, 20, 20))
        );
        jpEdicionLayout.setVerticalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpEdicionLayout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblBuscarCampo)
                    .addComponent(lblRolesUsuario))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnGuardar, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18))
        );

        panel2.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel2.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("ROLES DE USUARIO");
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

        cbUsuario.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbUsuarioItemStateChanged(evt);
            }
        });

        lblSeleccioneUsuario.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        lblSeleccioneUsuario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblSeleccioneUsuario.setText("Seleccione el usuario");

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSeleccioneUsuario)
                            .addComponent(cbUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblSeleccioneUsuario)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 514, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 383, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("Encargados");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        RegistroNuevoModificar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            btnGuardar.doClick();
        }
    }//GEN-LAST:event_btnGuardarKeyPressed

    private void cbUsuarioItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbUsuarioItemStateChanged
        if (cbUsuario.getSelectedIndex() != -1) {
            ConsultaModulosUsuario(metodoscombo.ObtenerIDSelectComboBox(cbUsuario) + "");
        }
    }//GEN-LAST:event_cbUsuarioItemStateChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGuardar;
    private javax.swing.JComboBox cbUsuario;
    private javax.swing.JCheckBox chbAlta;
    private javax.swing.JCheckBox chbModificar;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jpEdicion;
    private javax.swing.JPanel jpPrincipal;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblRolesUsuario;
    private javax.swing.JLabel lblSeleccioneUsuario;
    private org.edisoncor.gui.panel.Panel panel2;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbModulosUsuario;
    // End of variables declaration//GEN-END:variables
}
