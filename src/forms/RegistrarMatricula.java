/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import dao.DAO;
import java.awt.Component;
import java.awt.Container;
import java.awt.FocusTraversalPolicy;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static login.Login.codUsuario;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class RegistrarMatricula extends javax.swing.JDialog {

    private DAO con = new DAO();
    private Metodos metodos = new Metodos();
    private MetodosTXT metodostxt = new MetodosTXT();
    private MetodosCombo metodoscombo = new MetodosCombo();
    private DefaultTableModel tableModelAlumnos;

    public RegistrarMatricula(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        //Obtener fecha actual
        dcFechaMatricula.setDate(new Date());

        Date fechaActual = new Date();
        Calendar c2 = new GregorianCalendar();
        if (fechaActual.after(FechaFinPeriodo())) {
            int periodo = c2.get(Calendar.YEAR) + 1;
            txtPeriodo.setText(periodo + "");
        } else {
            txtPeriodo.setText(c2.get(Calendar.YEAR) + "");
        }

        //Metodos
        CargarComboBoxes();
        TablaAllAlumno(false);

        //Permiso Roles de usuario
        String permisos = metodos.PermisoRol(codUsuario, "MATRICULA");
        btnGuardar.setVisible(permisos.contains("A"));

        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboConsulta(cbAlumno, "SELECT alu_codigo, CONCAT(alu_nombre,' ', alu_apellido) AS nomape "
                + "FROM alumno ORDER BY alu_nombre", -1);
        metodoscombo.CargarComboConsulta(cbNivel, "SELECT niv_codigo, "
                + "CASE niv_seccion WHEN 'SIN ESPECIFICAR' THEN CONCAT(niv_descripcion,' ',niv_turno) "
                + "ELSE CONCAT(niv_descripcion,' \"', niv_seccion,'\"', ' ',niv_turno) END AS nivel "
                + "FROM nivel ORDER BY niv_codigo", 1);
    }

    public void TablaAllAlumno(boolean confiltroSinMat) {
        String sentencia;
        if (confiltroSinMat == false) {
            sentencia = "CALL SP_AlumnoConsulta()";
        } else {
            sentencia = "SELECT alu_codigo, alu_nombre, alu_apellido, alu_cedula, "
                    + "DATE_FORMAT(alu_fechanacimiento, '%d/%m/%Y') AS fechanacimiento, "
                    + "DATE_FORMAT(alu_fechainscripcion, '%d/%m/%Y') AS fechainscripcion, alu_sexo, alu_telefono, alu_email, alu_obs, "
                    + "CONCAT(apo_nombre,' ',apo_apellido) AS nomapeapoderado, "
                    + "CASE alu_estado  WHEN 1 THEN 'ACTIVO'  WHEN 0 THEN 'INACTIVO' END AS estado "
                    + "FROM alumno LEFT OUTER JOIN matricula ON alu_codigo=mat_alumno, apoderado "
                    + "WHERE alu_apoderado = apo_codigo  AND mat_alumno IS NULL ORDER BY alu_codigo DESC";
        }

        if (cbCampoBuscarAlumno.getItemCount() == 0) {
            metodos.CargarTitlesaCombo(cbCampoBuscarAlumno, tbAlumnos);
        }

        tableModelAlumnos = (DefaultTableModel) tbAlumnos.getModel();
        tableModelAlumnos.setRowCount(0);
        try {
            con = con.ObtenerRSSentencia(sentencia);
            int codigo;
            String nombre, apellido, cedula, fechanacimiento, fechainscripcion, sexo, telefono, email, obs, apoderado, estado;
            while (con.getResultSet().next()) {
                codigo = con.getResultSet().getInt("alu_codigo");
                nombre = con.getResultSet().getString("alu_nombre");
                apellido = con.getResultSet().getString("alu_apellido");
                cedula = con.getResultSet().getString("alu_cedula");
                System.out.println("cedula " + cedula);
                if (cedula == null) {
                    cedula = "0";
                }
                fechanacimiento = con.getResultSet().getString("fechanacimiento");
                fechainscripcion = con.getResultSet().getString("fechainscripcion");
                sexo = con.getResultSet().getString("alu_sexo");
                telefono = con.getResultSet().getString("alu_telefono");
                email = con.getResultSet().getString("alu_email");
                obs = con.getResultSet().getString("alu_obs");
                apoderado = con.getResultSet().getString("nomapeapoderado");
                estado = con.getResultSet().getString("estado");

                tableModelAlumnos.addRow(new Object[]{codigo, nombre, apellido, cedula, fechanacimiento, fechainscripcion, sexo, telefono, email, obs, apoderado, estado});
            }
            tbAlumnos.setModel(tableModelAlumnos);
            metodos.AnchuraColumna(tbAlumnos);
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();

        if (tbAlumnos.getModel().getRowCount() == 1) {
            lbCantRegistrosAlumno.setText(tbAlumnos.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistrosAlumno.setText(tbAlumnos.getModel().getRowCount() + " Registros encontrados");
        }
    }

    public void RegistroNuevo() {
        if (ComprobarCampos() == true) {
            int idalumno = metodoscombo.ObtenerIDSelectCombo(cbAlumno);
            int idnivel = metodoscombo.ObtenerIDSelectCombo(cbNivel);
            DateFormat formatoFecha = new SimpleDateFormat("yyyy-MM-dd");
            String fecha = formatoFecha.format(dcFechaMatricula.getDate());
            String periodo = txtPeriodo.getText();

            int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de registrar este nueva matricula?", "Confirmación", JOptionPane.YES_OPTION);
            if (JOptionPane.YES_OPTION == confirmado) {
                //Registrar nuevo
                String sentencia = "CALL SP_MatriculaAlta('" + idalumno + "','" + idnivel + "','" + fecha + "','" + periodo + "')";
                con.EjecutarABM(sentencia, true);

                //Cambiar estado de alumno
                sentencia = "UPDATE alumno SET alu_estado=1 WHERE alu_codigo=" + idalumno;
                con.EjecutarABM(sentencia, false);

                Limpiar();
            }
        }
    }

    private boolean ComprobarCampos() {
        if (cbNivel.getSelectedIndex() == -1) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Seleccione el nivel", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbNivel.requestFocus();
            return false;
        }

        if (cbAlumno.getSelectedIndex() == -1) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Seleccione el alumno/a", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbAlumno.requestFocus();
            return false;
        }

        if (dcFechaMatricula.getDate() == null) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Complete la fecha de matriculación", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dcFechaMatricula.requestFocus();
            return false;
        }

        Boolean YaExisteEnLaBD = con.SiYaExisteEnLaBD("SELECT mat_codigo FROM matricula WHERE mat_alumno='"
                + metodoscombo.ObtenerIDSelectCombo(cbAlumno) + "' AND mat_periodo='" + txtPeriodo.getText() + "'");
        if (YaExisteEnLaBD == true) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "El alumno/a " + cbAlumno.getSelectedItem().toString()
                    + " ya se encuentra matriculado en el periodo " + txtPeriodo.getText(), "Advertencia", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        return true;
    }

    private void Limpiar() {
        cbAlumno.setSelectedIndex(-1);
        dcFechaMatricula.setDate(new Date());
        cbNivel.setSelectedIndex(0);

    }

//--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BuscadorAlumno = new javax.swing.JDialog();
        panel6 = new org.edisoncor.gui.panel.Panel();
        jLabel12 = new javax.swing.JLabel();
        txtBuscarAlumno = new javax.swing.JTextField();
        lblBuscarCampoAlumno = new javax.swing.JLabel();
        cbCampoBuscarAlumno = new javax.swing.JComboBox();
        scAlumnos = new javax.swing.JScrollPane();
        tbAlumnos = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lbCantRegistrosAlumno = new javax.swing.JLabel();
        chbNoMatriculados = new javax.swing.JCheckBox();
        jpPrincipal = new javax.swing.JPanel();
        jpBotones = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        jpDatosVenta = new javax.swing.JPanel();
        lblRucCedula = new javax.swing.JLabel();
        cbAlumno = new javax.swing.JComboBox<>();
        btnBuscarAlumno = new javax.swing.JButton();
        lblFechaRegistro = new javax.swing.JLabel();
        dcFechaMatricula = new com.toedter.calendar.JDateChooser();
        lblNivel = new javax.swing.JLabel();
        cbNivel = new javax.swing.JComboBox<>();
        txtPeriodo = new javax.swing.JTextField();
        lblCantMatriculados = new javax.swing.JLabel();
        lblCodigo7 = new javax.swing.JLabel();
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        lblAlumnoActual = new javax.swing.JLabel();
        lblRucCedula3 = new javax.swing.JLabel();
        lblRucCedula4 = new javax.swing.JLabel();
        lblUltimaMatriculacion = new javax.swing.JLabel();

        BuscadorAlumno.setTitle("Buscador de alumnos");
        BuscadorAlumno.setModal(true);
        BuscadorAlumno.setSize(new java.awt.Dimension(760, 310));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoBuscar.png"))); // NOI18N
        jLabel12.setText("  BUSCAR ");

        txtBuscarAlumno.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        txtBuscarAlumno.setForeground(new java.awt.Color(0, 153, 153));
        txtBuscarAlumno.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBuscarAlumno.setCaretColor(new java.awt.Color(0, 204, 204));
        txtBuscarAlumno.setDisabledTextColor(new java.awt.Color(0, 204, 204));
        txtBuscarAlumno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarAlumnoKeyReleased(evt);
            }
        });

        lblBuscarCampoAlumno.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampoAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblBuscarCampoAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampoAlumno.setText("Buscar por:");

        tbAlumnos.setAutoCreateRowSorter(true);
        tbAlumnos.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbAlumnos.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbAlumnos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Nombre", "Apellido", "N° de cedula", "Fecha de nacimiento", "Fecha de inscripcion", "Sexo", "Telefono", "Email", "Observacion", "Apoderado", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbAlumnos.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbAlumnos.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbAlumnos.setGridColor(new java.awt.Color(0, 153, 204));
        tbAlumnos.setOpaque(false);
        tbAlumnos.setRowHeight(20);
        tbAlumnos.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbAlumnos.getTableHeader().setReorderingAllowed(false);
        tbAlumnos.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbAlumnosMousePressed(evt);
            }
        });
        tbAlumnos.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbAlumnosKeyReleased(evt);
            }
        });
        scAlumnos.setViewportView(tbAlumnos);

        lbCantRegistrosAlumno.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lbCantRegistrosAlumno.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistrosAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistrosAlumno.setText("0 Registros encontrados");
        lbCantRegistrosAlumno.setPreferredSize(new java.awt.Dimension(57, 25));

        chbNoMatriculados.setForeground(new java.awt.Color(255, 255, 255));
        chbNoMatriculados.setText("Solo alumnos no matriculados");
        chbNoMatriculados.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbNoMatriculadosItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panel6Layout = new javax.swing.GroupLayout(panel6);
        panel6.setLayout(panel6Layout);
        panel6Layout.setHorizontalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel6Layout.createSequentialGroup()
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(panel6Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(scAlumnos, javax.swing.GroupLayout.PREFERRED_SIZE, 709, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, panel6Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(panel6Layout.createSequentialGroup()
                                .addComponent(chbNoMatriculados)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lbCantRegistrosAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(panel6Layout.createSequentialGroup()
                                .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtBuscarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(52, 52, 52)
                                .addComponent(lblBuscarCampoAlumno)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbCampoBuscarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(14, 14, 14))
        );
        panel6Layout.setVerticalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbCampoBuscarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscarCampoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(scAlumnos, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lbCantRegistrosAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(chbNoMatriculados))
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout BuscadorAlumnoLayout = new javax.swing.GroupLayout(BuscadorAlumno.getContentPane());
        BuscadorAlumno.getContentPane().setLayout(BuscadorAlumnoLayout);
        BuscadorAlumnoLayout.setHorizontalGroup(
            BuscadorAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        BuscadorAlumnoLayout.setVerticalGroup(
            BuscadorAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setTitle("Ventana Registrar Matricula");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);
        setSize(new java.awt.Dimension(616, 311));

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpBotones.setBackground(new java.awt.Color(233, 255, 255));
        jpBotones.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder()));
        jpBotones.setPreferredSize(new java.awt.Dimension(100, 50));

        btnGuardar.setBackground(new java.awt.Color(0, 153, 255));
        btnGuardar.setFont(new java.awt.Font("sansserif", 1, 18)); // NOI18N
        btnGuardar.setForeground(new java.awt.Color(255, 255, 255));
        btnGuardar.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoGuardar.png"))); // NOI18N
        btnGuardar.setText("Registrar");
        btnGuardar.setToolTipText("Registra la nueva matricula");
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
        btnCancelar.setToolTipText("Cancela lo que se estaba cargando");
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

        jpDatosVenta.setBackground(new java.awt.Color(233, 255, 255));
        jpDatosVenta.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        lblRucCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblRucCedula.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblRucCedula.setText("Alumno/a:");
        lblRucCedula.setToolTipText("");
        lblRucCedula.setFocusable(false);

        cbAlumno.setToolTipText("Seleccione el alumno al cual desea matricular");
        cbAlumno.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbAlumnoItemStateChanged(evt);
            }
        });

        btnBuscarAlumno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnBuscarAlumno.setToolTipText("Buscar cliente");
        btnBuscarAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarAlumnoActionPerformed(evt);
            }
        });

        lblFechaRegistro.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaRegistro.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFechaRegistro.setText("Fecha de matricula:");
        lblFechaRegistro.setToolTipText("");
        lblFechaRegistro.setFocusable(false);

        dcFechaMatricula.setToolTipText("Fecha de la matriculación que se está realizando");
        dcFechaMatricula.setEnabled(false);
        dcFechaMatricula.setMaxSelectableDate(new java.util.Date(4102455600000L));
        dcFechaMatricula.setMinSelectableDate(new java.util.Date(631162800000L));

        lblNivel.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblNivel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNivel.setText("Nivel:");
        lblNivel.setToolTipText("");
        lblNivel.setFocusable(false);

        cbNivel.setToolTipText("Seleccione el nivel en donde se matriculará al alumno seleccionado");
        cbNivel.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbNivelItemStateChanged(evt);
            }
        });

        txtPeriodo.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtPeriodo.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtPeriodo.setToolTipText("Periodo del año lectivo en donde se matriculará el alumno seleccionado");
        txtPeriodo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPeriodo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtPeriodoKeyTyped(evt);
            }
        });

        lblCantMatriculados.setFont(new java.awt.Font("SansSerif", 1, 13)); // NOI18N
        lblCantMatriculados.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCantMatriculados.setText("X alumnos matriculados en el periodo XXXX");
        lblCantMatriculados.setFocusable(false);

        lblCodigo7.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo7.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo7.setText("Periodo:");
        lblCodigo7.setFocusable(false);

        javax.swing.GroupLayout jpDatosVentaLayout = new javax.swing.GroupLayout(jpDatosVenta);
        jpDatosVenta.setLayout(jpDatosVentaLayout);
        jpDatosVentaLayout.setHorizontalGroup(
            jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosVentaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblRucCedula, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNivel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(2, 2, 2)
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpDatosVentaLayout.createSequentialGroup()
                        .addComponent(btnBuscarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblCodigo7)
                        .addGap(2, 2, 2)
                        .addComponent(txtPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblCantMatriculados, javax.swing.GroupLayout.DEFAULT_SIZE, 314, Short.MAX_VALUE))
                .addGap(24, 24, 24))
        );
        jpDatosVentaLayout.setVerticalGroup(
            jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpDatosVentaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtPeriodo, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCodigo7, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRucCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10)
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCantMatriculados, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpDatosVentaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(dcFechaMatricula, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaRegistro, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(8, Short.MAX_VALUE))
        );

        panel2.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel2.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REGISTRAR MATRICULA  ");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFocusable(false);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 22)); // NOI18N

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        lblAlumnoActual.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblAlumnoActual.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAlumnoActual.setText("FULANO");
        lblAlumnoActual.setToolTipText("");
        lblAlumnoActual.setFocusable(false);

        lblRucCedula3.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblRucCedula3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula3.setText("Alumno/a:");
        lblRucCedula3.setToolTipText("");
        lblRucCedula3.setFocusable(false);

        lblRucCedula4.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        lblRucCedula4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblRucCedula4.setText("Última matriculación:");
        lblRucCedula4.setToolTipText("");
        lblRucCedula4.setFocusable(false);

        lblUltimaMatriculacion.setFont(new java.awt.Font("SansSerif", 0, 14)); // NOI18N
        lblUltimaMatriculacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblUltimaMatriculacion.setText("Nivel X (20XX)");
        lblUltimaMatriculacion.setToolTipText("");
        lblUltimaMatriculacion.setFocusable(false);

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(185, 185, 185))
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jpDatosVenta, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                        .addComponent(lblRucCedula3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblAlumnoActual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                        .addComponent(lblRucCedula4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(lblUltimaMatriculacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblRucCedula3, javax.swing.GroupLayout.DEFAULT_SIZE, 25, Short.MAX_VALUE)
                    .addComponent(lblAlumnoActual, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(2, 2, 2)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblRucCedula4, javax.swing.GroupLayout.DEFAULT_SIZE, 22, Short.MAX_VALUE)
                    .addComponent(lblUltimaMatriculacion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12)
                .addComponent(jpDatosVenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(8, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 734, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 311, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        getAccessibleContext().setAccessibleName("RegistrarCompra");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents


    private void btnBuscarAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarAlumnoActionPerformed
        BuscadorAlumno.setLocationRelativeTo(this);
        BuscadorAlumno.setVisible(true);
    }//GEN-LAST:event_btnBuscarAlumnoActionPerformed

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        RegistroNuevo();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnGuardarKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyPressed


    }//GEN-LAST:event_btnGuardarKeyPressed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        Limpiar();
    }//GEN-LAST:event_btnCancelarActionPerformed

    private void txtBuscarAlumnoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarAlumnoKeyReleased
        metodos.FiltroJTable(txtBuscarAlumno.getText(), cbCampoBuscarAlumno.getSelectedIndex(), tbAlumnos);

        if (tbAlumnos.getRowCount() == 1) {
            lbCantRegistrosAlumno.setText(tbAlumnos.getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistrosAlumno.setText(tbAlumnos.getRowCount() + " Registros encontrados");
        }
    }//GEN-LAST:event_txtBuscarAlumnoKeyReleased

    private void tbAlumnosMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbAlumnosMousePressed
        if (evt.getClickCount() == 2) {
            int codselect = Integer.parseInt(tbAlumnos.getValueAt(tbAlumnos.getSelectedRow(), 0) + "");
            metodoscombo.SetSelectedCodigoItem(cbAlumno, codselect);
            BuscadorAlumno.dispose();
        }
    }//GEN-LAST:event_tbAlumnosMousePressed

    private void tbAlumnosKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbAlumnosKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            int codselect = Integer.parseInt(tbAlumnos.getValueAt(tbAlumnos.getSelectedRow(), 0) + "");
            metodoscombo.SetSelectedCodigoItem(cbAlumno, codselect);
            BuscadorAlumno.dispose();
        }
    }//GEN-LAST:event_tbAlumnosKeyReleased

    private void cbAlumnoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbAlumnoItemStateChanged
        String nivelAnterior;
        String seccionAnterior;
        String turnoAnterior;
        try {
            lblAlumnoActual.setText(cbAlumno.getSelectedItem().toString());

            con = con.ObtenerRSSentencia("SELECT niv_descripcion, niv_seccion, niv_turno, mat_periodo, "
                    + "CASE niv_seccion WHEN 'SIN ESPECIFICAR' THEN CONCAT(niv_descripcion,' ',niv_turno) "
                    + "ELSE CONCAT(niv_descripcion,' \"', niv_seccion,'\"', ' ',niv_turno) END AS nivel "
                    + "FROM matricula, nivel WHERE mat_alumno='" + metodoscombo.ObtenerIDSelectCombo(cbAlumno) + "' "
                    + "AND mat_nivel=niv_codigo ORDER BY mat_periodo DESC LIMIT 1");
            if (con.getResultSet().next()) {
                lblUltimaMatriculacion.setText(con.getResultSet().getString("nivel") + " (" + con.getResultSet().getString("mat_periodo") + ")");
                nivelAnterior = con.getResultSet().getString("niv_descripcion");
                seccionAnterior = con.getResultSet().getString("niv_seccion");
                turnoAnterior = con.getResultSet().getString("niv_turno");

            } else {
                lblUltimaMatriculacion.setText("Nunca");
                nivelAnterior = "-";
                seccionAnterior = "-";
                turnoAnterior = "-";
            }

            if (lblUltimaMatriculacion.getText().equals("Nunca")) {
                if (cbNivel.getItemCount() > 0) {
                    cbNivel.setSelectedIndex(0);
                }
            } else {
                String niveles[] = {"JARDIN", "PREESCOLAR", "1° GRADO", "2° GRADO", "3° GRADO", "4° GRADO", "5° GRADO", "6° GRADO", "7° GRADO", "8° GRADO", "9° GRADO",
                    "1° CURSO", "2° CURSO", "3° CURSO"};
                int nivelAnteriorInt = 0;
                for (int i = 0; i < niveles.length; i++) {
                    if (nivelAnterior.equals(niveles[i])) {
                        nivelAnteriorInt = i;
                        i = niveles.length;
                    }
                }

                if (nivelAnteriorInt > 10 && nivelAnteriorInt < 13) {//Si NivelAnterior es 1 Curso  a 3 curso
                    System.out.println("1 " + niveles[nivelAnteriorInt + 1]);
                    metodoscombo.SetSelectedNombreItem(cbNivel, niveles[nivelAnteriorInt + 1]);
                }

                if (nivelAnteriorInt < 11 && nivelAnteriorInt >= 0) {//Si NivelAnterior es jardin hasta 9 grado
                    System.out.println("2 " + niveles[nivelAnteriorInt + 1] + " \"" + seccionAnterior + "\" " + turnoAnterior);
                    metodoscombo.SetSelectedNombreItem(cbNivel, niveles[nivelAnteriorInt + 1] + " \"" + seccionAnterior + "\" " + turnoAnterior);
                }

                if (nivelAnteriorInt == 12) { //Si ya esta en 3 curso
                    cbNivel.setSelectedIndex(0);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            lblAlumnoActual.setText("");
            lblUltimaMatriculacion.setText("");
        }
        con.DesconectarBasedeDatos();
    }//GEN-LAST:event_cbAlumnoItemStateChanged

    private void chbNoMatriculadosItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbNoMatriculadosItemStateChanged
        TablaAllAlumno(chbNoMatriculados.isSelected());
    }//GEN-LAST:event_chbNoMatriculadosItemStateChanged

    private void txtPeriodoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPeriodoKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtPeriodo, 4);
    }//GEN-LAST:event_txtPeriodoKeyTyped

    private void cbNivelItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbNivelItemStateChanged

        try {
            con = con.ObtenerRSSentencia("SELECT COUNT(mat_nivel) AS cantidad FROM matricula WHERE "
                    + "mat_nivel = '" + metodoscombo.ObtenerIDSelectCombo(cbNivel) + "' AND mat_periodo = '" + txtPeriodo.getText() + "'");

            while (con.getResultSet().next()) {
                lblCantMatriculados.setText(con.getResultSet().getString("cantidad") + " alumnos matriculados en el periodo " + txtPeriodo.getText());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }//GEN-LAST:event_cbNivelItemStateChanged

    private Date FechaFinPeriodo() {
        Date fechaFinPeriodo = new Date();
        String[] diaMes;
        String diaFinPeriodo;
        String mesFinPeriodo;
        String añoActual;
        Calendar calendar = new java.util.GregorianCalendar();
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        try {
            con = con.ObtenerRSSentencia("SELECT conf_valor FROM configuracion WHERE conf_codigo = '3'");
            while (con.getResultSet().next()) {
                diaMes = con.getResultSet().getString("conf_valor").split("/");
                diaFinPeriodo = diaMes[0];
                mesFinPeriodo = diaMes[1];
                añoActual = calendar.get(Calendar.YEAR) + "";
                fechaFinPeriodo = formatoFecha.parse(diaFinPeriodo + "/" + mesFinPeriodo + "/" + añoActual);
            }
        } catch (SQLException | ParseException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
        return fechaFinPeriodo;
    }

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(cbAlumno);
        ordenTabulador.add(cbNivel);
        ordenTabulador.add(dcFechaMatricula);
        ordenTabulador.add(btnGuardar);
        setFocusTraversalPolicy(new PersonalizadoFocusTraversalPolicy());
    }

    static void PonerAlumnoSeleccionado(int codseleccionado) {
        MetodosCombo metodoscombo = new MetodosCombo();
        metodoscombo.SetSelectedCodigoItem(cbAlumno, codseleccionado);
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
    private javax.swing.JDialog BuscadorAlumno;
    private javax.swing.JButton btnBuscarAlumno;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnGuardar;
    private static javax.swing.JComboBox<MetodosCombo> cbAlumno;
    private javax.swing.JComboBox cbCampoBuscarAlumno;
    private javax.swing.JComboBox<MetodosCombo> cbNivel;
    private javax.swing.JCheckBox chbNoMatriculados;
    private com.toedter.calendar.JDateChooser dcFechaMatricula;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpDatosVenta;
    private javax.swing.JPanel jpPrincipal;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistrosAlumno;
    private javax.swing.JLabel lblAlumnoActual;
    private javax.swing.JLabel lblBuscarCampoAlumno;
    private javax.swing.JLabel lblCantMatriculados;
    private javax.swing.JLabel lblCodigo7;
    private javax.swing.JLabel lblFechaRegistro;
    private javax.swing.JLabel lblNivel;
    private javax.swing.JLabel lblRucCedula;
    private javax.swing.JLabel lblRucCedula3;
    private javax.swing.JLabel lblRucCedula4;
    private javax.swing.JLabel lblUltimaMatriculacion;
    private org.edisoncor.gui.panel.Panel panel2;
    private org.edisoncor.gui.panel.Panel panel6;
    private javax.swing.JScrollPane scAlumnos;
    private javax.swing.JTable tbAlumnos;
    private javax.swing.JTextField txtBuscarAlumno;
    private javax.swing.JTextField txtPeriodo;
    // End of variables declaration//GEN-END:variables
}
