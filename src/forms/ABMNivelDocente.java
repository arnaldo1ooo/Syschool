/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Toolkit;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JOptionPane;
import utilidades.Metodos;
import utilidades.MetodosTXT;
import utilidades.MetodosCombo;

/**
 *
 * @author Arnaldo Cantero
 */
public class ABMNivelDocente extends javax.swing.JDialog {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    MetodosCombo metodoscombo = new MetodosCombo();
    Calendar c2 = new GregorianCalendar();

    public ABMNivelDocente(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        CargarComboBoxes();
        ConsultaDocentes(); //Trae todos los registros
        txtBuscar.requestFocus();

        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    private void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboConsulta(cbNivel, "SELECT niv_codigo, "
                + "CASE niv_seccion WHEN 'SIN ESPECIFICAR' THEN CONCAT(niv_descripcion,' ',niv_turno) "
                + "ELSE CONCAT(niv_descripcion,' \"', niv_seccion,'\"', ' ',niv_turno) END AS nivel "
                + "FROM nivel ORDER BY niv_codigo", 1);

        metodoscombo.CargarComboConsulta(cbDocente, "SELECT fun_codigo, CONCAT(fun_nombre, ' ', fun_apellido) AS nomapedocente "
                + "FROM funcionario, cargo WHERE car_descripcion = 'DOCENTE' AND fun_cargo = car_codigo ORDER BY fun_nombre", -1);
        cbDocente.addItem(new MetodosCombo(0, "SIN ESPECIFICAR"));
        cbDocente.setMaximumRowCount(cbDocente.getModel().getSize());
        cbDocente.setSelectedItem("SIN ESPECIFICAR");
    }

    public void RegistroNuevoModificar() {
        if (ComprobarCampos() == true) {
            String codigo = txtCodigo.getText();
            int docente = metodoscombo.ObtenerIDSelectCombo(cbDocente);
            int nivel = metodoscombo.ObtenerIDSelectCombo(cbNivel);
            int periodo = dyPeriodo.getYear();

            if (txtCodigo.getText().equals("")) { //NUEVO REGISTRO
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de registrar este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    String sentencia = "CALL SP_NivelDocenteAlta ('" + docente + "','" + nivel + "','" + periodo + "')";
                    con.EjecutarABM(sentencia, true);

                    ConsultaDocentes(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            } else { //MODIFICAR REGISTRO
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de modificar este regitro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    String sentencia = "CALL SP_NivelDocenteModificar('" + codigo + "','" + docente + "','" + nivel + "','" + periodo + "')";
                    con.EjecutarABM(sentencia, true);

                    ConsultaDocentes(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            }
        }
    }

    private void RegistroEliminar() {
        String codigo;
        int filasel = tbPrincipal.getSelectedRow();
        if (filasel != -1) {
            int confirmado = javax.swing.JOptionPane.showConfirmDialog(this, "¿Realmente desea eliminar el registro seleccionado?", "Confirmación", JOptionPane.YES_OPTION);
            if (confirmado == JOptionPane.YES_OPTION) {
                codigo = (String) tbPrincipal.getModel().getValueAt(filasel, 0);
                String sentencia = "CALL SP_NivelDocenteEliminar(" + codigo + ")";
                con.EjecutarABM(sentencia, true);

                ConsultaDocentes(); //Actualizar tabla
                ModoEdicion(false);
                Limpiar();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtBuscar.requestFocus();
        }
    }

    private void ConsultaDocentes() {//Realiza la consulta de los productos que tenemos en la base de datos
        String sentencia = "SELECT nivfun_codigo, CONCAT(fun_nombre,' ', fun_apellido) AS docente, nivfun_periodo "
                + "FROM nivel_docente, funcionario "
                + "WHERE nivfun_docente = fun_codigo AND nivfun_nivel = '" + metodoscombo.ObtenerIDSelectCombo(cbNivel) + "'";
        String titlesJtabla[] = {"Código", "Docente", "Periodo"};
        tbPrincipal.setModel(con.ConsultaTableBD(sentencia, titlesJtabla, cbCampoBuscar));
        cbCampoBuscar.setSelectedIndex(1);
        metodos.AnchuraColumna(tbPrincipal);

        if (tbPrincipal.getModel().getRowCount() == 1) {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registros encontrados");
        }
    }

    private void ModoVistaPrevia() {
        txtCodigo.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0) + ""));
        txtNivel.setText(cbNivel.getSelectedItem() + "");
        metodoscombo.SetSelectedNombreItem(cbDocente, tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 1) + "");
        dyPeriodo.setYear(Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 2) + ""));

    }

    private void ModoEdicion(boolean valor) {
        cbNivel.setEnabled(!valor);
        txtBuscar.setEnabled(!valor);
        tbPrincipal.setEnabled(!valor);
        cbDocente.setEnabled(valor);
        dyPeriodo.setEnabled(valor);

        btnNuevo.setEnabled(!valor);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);;
    }

    private void Limpiar() {
        txtCodigo.setText("");
        txtNivel.setText("");
        metodoscombo.SetSelectedNombreItem(cbDocente, "SIN ESPECIFICAR");

        dyPeriodo.setYear(c2.get(Calendar.YEAR));

        txtBuscar.requestFocus();
        tbPrincipal.clearSelection();
    }

    public boolean ComprobarCampos() {
        if (cbDocente.getSelectedItem().toString().equals("SIN ESPECIFICAR")) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Seleccione el docente", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbDocente.requestFocus();
            return false;
        }

        if (dyPeriodo.getYear() < c2.get(Calendar.YEAR)) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "El periodo no puede ser menor al año actual", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dyPeriodo.requestFocus();
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
        jLabel2 = new javax.swing.JLabel();
        lblDocente = new javax.swing.JLabel();
        cbDocente = new javax.swing.JComboBox<>();
        lblRucCedula1 = new javax.swing.JLabel();
        txtNivel = new javax.swing.JTextField();
        dyPeriodo = new com.toedter.calendar.JYearChooser();
        jpBotones2 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        lblNivel = new javax.swing.JLabel();
        cbNivel = new javax.swing.JComboBox<>();

        setTitle("Ventana niveles");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpTabla.setBackground(new java.awt.Color(233, 255, 255));
        jpTabla.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoBuscar.png"))); // NOI18N
        jLabel10.setText("  BUSCAR ");
        jLabel10.setIconTextGap(0);

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
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 623, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpTablaLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addGap(2, 2, 2)
                        .addComponent(txtBuscar)
                        .addGap(18, 18, 18)
                        .addComponent(lblBuscarCampo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jpTablaLayout.createSequentialGroup()
                .addGap(292, 292, 292)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 331, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addGap(0, 0, 0)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                    .addComponent(btnNuevo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpBotonesLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminar)
                .addGap(25, 25, 25))
        );

        jtpEdicion.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jtpEdicion.setName(""); // NOI18N

        jpEdicion.setBackground(new java.awt.Color(233, 255, 255));
        jpEdicion.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        lblCodigo.setBackground(new java.awt.Color(0, 0, 0));
        lblCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo.setText("Código:");

        txtCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo.setEnabled(false);

        lblDescripcion.setBackground(new java.awt.Color(0, 0, 0));
        lblDescripcion.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblDescripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDescripcion.setText("Periodo*:");

        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Campos con (*) son obligatorios");

        lblDocente.setBackground(new java.awt.Color(0, 0, 0));
        lblDocente.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblDocente.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDocente.setText("Docente encargado*:");

        cbDocente.setEnabled(false);

        lblRucCedula1.setBackground(new java.awt.Color(0, 0, 0));
        lblRucCedula1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRucCedula1.setText("Nivel:");
        lblRucCedula1.setToolTipText("");

        txtNivel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtNivel.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNivel.setEnabled(false);
        txtNivel.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNivelKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNivelKeyTyped(evt);
            }
        });

        dyPeriodo.setBackground(new java.awt.Color(0, 153, 153));
        dyPeriodo.setEnabled(false);

        javax.swing.GroupLayout jpEdicionLayout = new javax.swing.GroupLayout(jpEdicion);
        jpEdicion.setLayout(jpEdicionLayout);
        jpEdicionLayout.setHorizontalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCodigo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 59, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDocente, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(1, 1, 1)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(txtNivel)
                    .addComponent(cbDocente, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(39, 39, 39)
                .addComponent(lblDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(dyPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 72, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(202, 202, 202))
        );
        jpEdicionLayout.setVerticalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblDescripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dyPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblRucCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNivel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDocente, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDocente, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(14, 14, 14))
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
        labelMetric2.setText("DOCENTE ENCARGADO");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 358, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        lblNivel.setBackground(new java.awt.Color(0, 0, 0));
        lblNivel.setFont(new java.awt.Font("SansSerif", 1, 12)); // NOI18N
        lblNivel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNivel.setText("Seleccione el nivel");

        cbNivel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbNivelItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGap(252, 252, 252)
                        .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtpEdicion)
                            .addGroup(jpPrincipalLayout.createSequentialGroup()
                                .addComponent(jpTabla, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(cbNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 221, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 118, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(cbNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jpBotones, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jtpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtpEdicion.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 814, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
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

        if (cbNivel.getSelectedIndex() != -1) {
            txtNivel.setText(cbNivel.getSelectedItem().toString());
        } else {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Seleccione un nivel", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbNivel.requestFocus();
            return;
        }

        if (tbPrincipal.getRowCount() > 0) {
            int periodo = Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getRowCount() - 1, 2) + "");
            dyPeriodo.setValue(periodo + 1);
        }
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

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            ModoVistaPrevia();
        }
    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void txtNivelKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNivelKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNivelKeyReleased

    private void txtNivelKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNivelKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtNivelKeyTyped

    private void cbNivelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbNivelItemStateChanged
        ConsultaDocentes();
        Limpiar();
    }//GEN-LAST:event_cbNivelItemStateChanged

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(dyPeriodo);
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
    private javax.swing.JComboBox<MetodosCombo> cbDocente;
    private javax.swing.JComboBox<MetodosCombo> cbNivel;
    private com.toedter.calendar.JYearChooser dyPeriodo;
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
    private javax.swing.JLabel lblDocente;
    private javax.swing.JLabel lblNivel;
    private javax.swing.JLabel lblRucCedula1;
    private org.edisoncor.gui.panel.Panel panel2;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtNivel;
    // End of variables declaration//GEN-END:variables
}
