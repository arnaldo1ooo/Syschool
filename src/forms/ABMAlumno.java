/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import conexion.Conexion;
import java.awt.Color;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static login.Login.codUsuario;
import org.oxbow.swingbits.table.filter.TableRowFilterSupport;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;

/**
 *
 * @author Arnaldo Cantero
 */
public class ABMAlumno extends javax.swing.JDialog {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    MetodosCombo metodoscombo = new MetodosCombo();
    DefaultTableModel modeltableAlumnos;

    public ABMAlumno(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        metodos.AnchuraColumna(tbPrincipal);
        TableRowFilterSupport.forTable(tbPrincipal).searchable(true).apply(); //Activar filtrado de tabla click derecho en cabecera

        //Poner fecha actual
        dcFechaInscripcion.setDate(new Date());
        //LLamar metodos
        TablaConsultaBDAll(); //Trae todos los registros
        TablaAllApoderado();
        CargarComboBoxes();

        //Permiso Roles de usuario
        //Permiso Roles de usuario
        String permisos = metodos.PermisoRol(codUsuario, "ALUMNO");
        btnNuevo.setVisible(permisos.contains("A"));
        btnModificar.setVisible(permisos.contains("M"));
        btnEliminar.setVisible(permisos.contains("B"));

        //Cambiar color de disabled combo
        metodoscombo.CambiarColorDisabledCombo(cbSexo, Color.BLACK);
        metodoscombo.CambiarColorDisabledCombo(cbEstado, Color.BLACK);
    }

    //--------------------------METODOS----------------------------//
    private void CargarComboBoxes() {
        //Carga los combobox con las consultas
        metodoscombo.CargarComboConsulta(cbApoderado, "SELECT apo_codigo, CONCAT(apo_nombre,' ', apo_apellido) AS nomape FROM apoderado ORDER BY apo_nombre", -1);
    }

    private void TablaAllApoderado() {//Realiza la consulta de los productos que tenemos en la base de datos
        String sentencia = "CALL SP_ApoderadoConsulta";
        String titlesJtabla[] = {"Código", "N° de cédula", "Nombre", "Apellido", "Sexo", "Dirección", "Teléfono", "Email", "Observación"};

        tbApoderado.setModel(con.ConsultaTableBD(sentencia, titlesJtabla, cbCampoBuscarApoderado));
        cbCampoBuscarApoderado.setSelectedIndex(1);
        metodos.AnchuraColumna(tbApoderado);

        if (tbApoderado.getModel().getRowCount() == 1) {
            lbCantRegistrosApoderado.setText(tbApoderado.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistrosApoderado.setText(tbApoderado.getModel().getRowCount() + " Registros encontrados");
        }
    }

    public void RegistroNuevoModificar() {
        try {
            if (ComprobarCampos() == true) {
                String codigo = txtCodigo.getText();
                String nombre = metodos.MayusCadaPrimeraLetra(txtNombre.getText());
                nombre = metodostxt.QuitaEspaciosString(nombre);
                String apellido = metodos.MayusCadaPrimeraLetra(txtApellido.getText());
                apellido = metodostxt.QuitaEspaciosString(apellido);
                String cedula = null;
                if (chbSincedula.isSelected() == false) {
                    cedula = metodostxt.StringSinPuntosMiles(txtCedula.getText()) + "";
                    cedula = metodostxt.QuitaEspaciosString(cedula);
                }
                SimpleDateFormat formatofecha = new SimpleDateFormat("yyyy-MM-dd");
                String fechanacimiento = formatofecha.format(dcFechaNacimiento.getDate());
                String fechainscripcion = formatofecha.format(dcFechaInscripcion.getDate());
                String sexo = cbSexo.getSelectedItem().toString();
                String telefono = txtTelefono.getText();
                String email = metodostxt.QuitaEspaciosString(txtEmail.getText());
                String obs = metodos.MayusPrimeraLetra(taObs.getText());
                obs = metodostxt.QuitaEspaciosString(obs);
                int apoderado = metodoscombo.ObtenerIDSelectCombo(cbApoderado);
                int estado = cbEstado.getSelectedIndex();

                if (txtCodigo.getText().equals("")) {//Si es nuevo
                    int confirmado = JOptionPane.showConfirmDialog(this, "¿Esta seguro crear este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);
                    if (JOptionPane.YES_OPTION == confirmado) {
                        //REGISTRAR NUEVO
                        String sentencia = "CALL SP_AlumnoAlta ('" + nombre + "','" + apellido + "'," + cedula
                                + ",'" + fechanacimiento + "','" + fechainscripcion + "','" + sexo + "','" + telefono + "','" + email
                                + "','" + obs + "','" + apoderado + "','" + estado + "')";
                        con.EjecutarABM(sentencia, true);

                        TablaConsultaBDAll(); //Actualizar tabla
                        Limpiar();
                        ModoEdicion(false);
                    }
                } else {
                    int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de modificar este registro?", "Confirmación", JOptionPane.YES_OPTION);
                    if (JOptionPane.YES_OPTION == confirmado) {
                        String sentencia = "CALL SP_AlumnoModificar('" + codigo + "','" + nombre + "','" + apellido + "'," + cedula
                                + ",'" + fechanacimiento + "','" + fechainscripcion + "','" + sexo + "','" + telefono + "','" + email
                                + "','" + obs + "','" + apoderado + "','" + estado + "')";

                        con.EjecutarABM(sentencia, true);
                        TablaConsultaBDAll(); //Actualizar tabla                        
                        ModoEdicion(false);
                        Limpiar();
                        //this.repaint();
                    }
                }
            }
        } catch (HeadlessException ex) {
            JOptionPane.showMessageDialog(this, "Completar los campos obligarios marcados con * ", "Advertencia", JOptionPane.WARNING_MESSAGE);
            System.out.println("Completar los campos obligarios marcados con * " + ex);
            txtNombre.requestFocus();
        }
    }

    private void RegistroEliminar() {
        int codigo;
        int filasel = tbPrincipal.getSelectedRow();
        if (filasel != -1) {
            int confirmado = javax.swing.JOptionPane.showConfirmDialog(this, "¿Realmente desea eliminar este alumno?, tambien se ELIMINARÁN las matriculas referentes al mismo", "Confirmación", JOptionPane.YES_OPTION);
            if (confirmado == JOptionPane.YES_OPTION) {
                codigo = Integer.parseInt(tbPrincipal.getModel().getValueAt(filasel, 0) + "");
                String sentencia = "CALL SP_AlumnoEliminar(" + codigo + ")";
                con.EjecutarABM(sentencia, true);

                TablaConsultaBDAll(); //Actualizar tabla
                ModoEdicion(false);
                Limpiar();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void TablaConsultaBDAll() {//Realiza la consulta de los productos que tenemos en la base de datos
        modeltableAlumnos = (DefaultTableModel) tbPrincipal.getModel();
        modeltableAlumnos.setRowCount(0); //Vacia tabla

        try {
            String sentencia = "CALL SP_AlumnoConsulta()";
            con = con.ObtenerRSSentencia(sentencia);
            int codigo, cedula, telefono;
            String nombre, apellido, sexo, fechanac, fechains, email, obs, apoderado, estado;
            while (con.getResultSet().next()) {
                codigo = con.getResultSet().getInt("alu_codigo");
                nombre = con.getResultSet().getString("alu_nombre");
                apellido = con.getResultSet().getString("alu_apellido");
                cedula = con.getResultSet().getInt("alu_cedula");
                fechanac = con.getResultSet().getString("fechanacimiento");
                fechains = con.getResultSet().getString("fechainscripcion");
                sexo = con.getResultSet().getString("alu_sexo");
                telefono = con.getResultSet().getInt("alu_telefono");
                email = con.getResultSet().getString("alu_email");
                obs = con.getResultSet().getString("alu_obs");
                apoderado = con.getResultSet().getString("nomapeapoderado");
                estado = con.getResultSet().getString("estado");

                modeltableAlumnos.addRow(new Object[]{codigo, nombre, apellido, cedula, fechanac, fechains, sexo, telefono, email, obs, apoderado, estado});
            }
            tbPrincipal.setModel(modeltableAlumnos);
            metodos.AnchuraColumna(tbPrincipal);
        } catch (SQLException | NullPointerException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();

        if (tbPrincipal.getModel().getRowCount() == 1) {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registros encontrados");
        }
    }

    private void ModoVistaPrevia() {
        txtCodigo.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0) + ""));
        txtNombre.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 1) + ""));
        txtApellido.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 2) + ""));
        txtCedula.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 3) + ""));
        txtCedula.setText(metodostxt.StringPuntosMiles(txtCedula.getText()));

        if (txtCedula.getText().equals("0")) {
            chbSincedula.setSelected(true);
        } else {
            chbSincedula.setSelected(false);
        }

        try {
            Date fechaParseada;
            fechaParseada = new SimpleDateFormat("dd/MM/yyyy").parse(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 4).toString() + ""));
            dcFechaNacimiento.setDate(fechaParseada);

            //Obtener edad
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaNac = LocalDate.parse(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 4) + ""), fmt);
            LocalDate ahora = LocalDate.now();
            Period periodo = Period.between(fechaNac, ahora);
            txtEdad.setText(metodos.SiStringEsNull(periodo.getYears() + ""));

            fechaParseada = new SimpleDateFormat("dd/MM/yyyy")
                    .parse(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 5) + ""));
            dcFechaInscripcion.setDate(fechaParseada);
        } catch (ParseException e) {
            System.out.println("Error al parsear fecha");
        }

        cbSexo.setSelectedItem(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 6).toString());
        txtTelefono.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 7).toString());
        txtEmail.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 8).toString());
        taObs.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 9).toString());

        String apoderado = tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 10).toString();
        metodoscombo.SetSelectedNombreItem(cbApoderado, apoderado);

        String estado = tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 11).toString();
        cbEstado.setSelectedItem(estado);
    }

    private void ModoEdicion(boolean valor) {
        tbPrincipal.setEnabled(!valor);

        txtNombre.setEnabled(valor);
        txtApellido.setEnabled(valor);
        if (chbSincedula.isSelected()) {
            txtCedula.setEnabled(false);
        } else {
            txtCedula.setEnabled(true);
        }
        chbSincedula.setEnabled(valor);
        dcFechaNacimiento.setEnabled(valor);
        dcFechaInscripcion.setEnabled(valor);
        cbSexo.setEnabled(valor);
        txtTelefono.setEnabled(valor);
        txtEmail.setEnabled(valor);
        taObs.setEnabled(valor);
        cbApoderado.setEnabled(valor);
        btnBuscarApoderado.setEnabled(valor);
        cbEstado.setEnabled(valor);
        btnNuevo.setEnabled(!valor);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);

        txtNombre.requestFocus();
    }

    private void Limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtCedula.setText("");
        chbSincedula.setSelected(false);
        dcFechaNacimiento.setCalendar(null);
        txtEdad.setText("");
        Calendar c2 = new GregorianCalendar();
        dcFechaInscripcion.setCalendar(c2);
        cbSexo.setSelectedIndex(0);
        txtEmail.setText("");
        txtTelefono.setText("");
        taObs.setText("");
        cbEstado.setSelectedItem(0);
        cbApoderado.setSelectedIndex(-1);
        lblFechaIngreso.setForeground(new Color(102, 102, 102));
        lblNombre.setForeground(new Color(102, 102, 102));
        lblApellido.setForeground(new Color(102, 102, 102));

        tbPrincipal.clearSelection();
    }

    public boolean ComprobarCampos() {
        if (metodostxt.ValidarCampoVacioTXT(txtNombre, lblNombre) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtApellido, lblApellido) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtCedula, lblCedula) == false) {
            return false;
        }

        if (txtCedula.getText().equals("0") && chbSincedula.isSelected() == false) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "El N° de cédula no puede ser 0", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (txtCodigo.getText().equals("") && txtCedula.getText().equals("0") == false) {
            try {
                con = con.ObtenerRSSentencia("SELECT alu_cedula FROM alumno WHERE alu_cedula='" + txtCedula.getText() + "'");
                if (con.getResultSet().next() == true) { //Si ya existe el numero de cedula en la tabla
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this, "El N° de cédula ingresado ya se encuentra registrado", "Error", JOptionPane.ERROR_MESSAGE);
                    lblCedula.setForeground(Color.RED);
                    lblCedula.requestFocus();
                    return false;
                }
            } catch (SQLException e) {
                System.out.println("Error al buscar si ci ya existe en bd: " + e);
            } catch (NullPointerException e) {
                System.out.println("La CI ingresada no existe en la bd, aprobado: " + e);
            }
            con.DesconectarBasedeDatos();
        }

        if (dcFechaNacimiento.getDate().after(new Date()) == true) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "La fecha de nacimiento no puede ser mayor a la fecha actual", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dcFechaNacimiento.requestFocus();
            return false;
        }

        if (dcFechaNacimiento.getDate() == null) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Ingrese una fecha de nacimiento válida", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dcFechaNacimiento.requestFocus();
            return false;
        }

        if (dcFechaInscripcion.getDate() == null) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Ingrese una fecha de inscripción válida", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dcFechaInscripcion.requestFocus();
            return false;
        }

        if (cbApoderado.getSelectedIndex() == -1) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(this, "Seleccione un apoderado", "Advertencia", JOptionPane.WARNING_MESSAGE);
            cbApoderado.requestFocus();
            return false;
        }
        return true;
    }

//--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        BuscadorApoderado = new javax.swing.JDialog();
        panel6 = new org.edisoncor.gui.panel.Panel();
        jLabel12 = new javax.swing.JLabel();
        txtBuscarApoderado = new javax.swing.JTextField();
        lblBuscarCampoApoderado = new javax.swing.JLabel();
        cbCampoBuscarApoderado = new javax.swing.JComboBox();
        scApoderado = new javax.swing.JScrollPane();
        tbApoderado = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lbCantRegistrosApoderado = new javax.swing.JLabel();
        jpPrincipal = new javax.swing.JPanel();
        jpTabla = new javax.swing.JPanel();
        scPrincipal = new javax.swing.JScrollPane();
        tbPrincipal = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        lbCantRegistros = new javax.swing.JLabel();
        jpBotones = new javax.swing.JPanel();
        btnNuevo = new javax.swing.JButton();
        btnModificar = new javax.swing.JButton();
        btnEliminar = new javax.swing.JButton();
        jtpEdicion = new javax.swing.JTabbedPane();
        jpEdicion = new javax.swing.JPanel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblFechaIngreso = new javax.swing.JLabel();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblApellido = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblObs = new javax.swing.JLabel();
        scpObs = new javax.swing.JScrollPane();
        taObs = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        dcFechaInscripcion = new com.toedter.calendar.JDateChooser();
        lblSexo = new javax.swing.JLabel();
        cbSexo = new javax.swing.JComboBox<>();
        lblEstado = new javax.swing.JLabel();
        cbEstado = new javax.swing.JComboBox<>();
        lblEstado1 = new javax.swing.JLabel();
        cbApoderado = new javax.swing.JComboBox<>();
        lblCedula = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        dcFechaNacimiento = new com.toedter.calendar.JDateChooser();
        lblFechaNacimiento = new javax.swing.JLabel();
        lblCedula1 = new javax.swing.JLabel();
        txtEdad = new javax.swing.JTextField();
        btnBuscarApoderado = new javax.swing.JButton();
        chbSincedula = new javax.swing.JCheckBox();
        jpBotones2 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panel1 = new org.edisoncor.gui.panel.Panel();
        labelMetric1 = new org.edisoncor.gui.label.LabelMetric();

        BuscadorApoderado.setTitle("Buscador de apoderados");
        BuscadorApoderado.setModal(true);
        BuscadorApoderado.setSize(new java.awt.Dimension(760, 310));

        jLabel12.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 255));
        jLabel12.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoBuscar.png"))); // NOI18N
        jLabel12.setText("  BUSCAR ");

        txtBuscarApoderado.setFont(new java.awt.Font("Tahoma", 1, 17)); // NOI18N
        txtBuscarApoderado.setForeground(new java.awt.Color(0, 153, 153));
        txtBuscarApoderado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        txtBuscarApoderado.setCaretColor(new java.awt.Color(0, 204, 204));
        txtBuscarApoderado.setDisabledTextColor(new java.awt.Color(0, 204, 204));
        txtBuscarApoderado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtBuscarApoderadoKeyReleased(evt);
            }
        });

        lblBuscarCampoApoderado.setFont(new java.awt.Font("sansserif", 1, 14)); // NOI18N
        lblBuscarCampoApoderado.setForeground(new java.awt.Color(255, 255, 255));
        lblBuscarCampoApoderado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblBuscarCampoApoderado.setText("Buscar por:");

        tbApoderado.setAutoCreateRowSorter(true);
        tbApoderado.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbApoderado.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbApoderado.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tbApoderado.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbApoderado.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbApoderado.setGridColor(new java.awt.Color(0, 153, 204));
        tbApoderado.setOpaque(false);
        tbApoderado.setRowHeight(20);
        tbApoderado.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbApoderado.getTableHeader().setReorderingAllowed(false);
        tbApoderado.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbApoderadoMousePressed(evt);
            }
        });
        tbApoderado.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbApoderadoKeyReleased(evt);
            }
        });
        scApoderado.setViewportView(tbApoderado);

        lbCantRegistrosApoderado.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lbCantRegistrosApoderado.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistrosApoderado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistrosApoderado.setText("0 Registros encontrados");
        lbCantRegistrosApoderado.setPreferredSize(new java.awt.Dimension(57, 25));

        javax.swing.GroupLayout panel6Layout = new javax.swing.GroupLayout(panel6);
        panel6.setLayout(panel6Layout);
        panel6Layout.setHorizontalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel6Layout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(scApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 717, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(panel6Layout.createSequentialGroup()
                        .addComponent(lbCantRegistrosApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 359, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8))
                    .addGroup(panel6Layout.createSequentialGroup()
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 276, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(lblBuscarCampoApoderado)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbCampoBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        panel6Layout.setVerticalGroup(
            panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, panel6Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(panel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbCampoBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblBuscarCampoApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 38, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addComponent(scApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(lbCantRegistrosApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout BuscadorApoderadoLayout = new javax.swing.GroupLayout(BuscadorApoderado.getContentPane());
        BuscadorApoderado.getContentPane().setLayout(BuscadorApoderadoLayout);
        BuscadorApoderadoLayout.setHorizontalGroup(
            BuscadorApoderadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        BuscadorApoderadoLayout.setVerticalGroup(
            BuscadorApoderadoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ventana Alumnos");
        setBackground(new java.awt.Color(45, 62, 80));
        setModal(true);
        setResizable(false);
        setSize(new java.awt.Dimension(952, 621));

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpTabla.setBackground(new java.awt.Color(233, 255, 255));
        jpTabla.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        scPrincipal.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        tbPrincipal.setAutoCreateRowSorter(true);
        tbPrincipal.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbPrincipal.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbPrincipal.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Codigo", "Nombre", "Apellido", "N° de cedula", "Fecha de nacimiento", "Fecha de inscripcion", "Sexo", "Telefono", "Email", "Observacion", "Apoderado", "Estado"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
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

        javax.swing.GroupLayout jpTablaLayout = new javax.swing.GroupLayout(jpTabla);
        jpTabla.setLayout(jpTablaLayout);
        jpTablaLayout.setHorizontalGroup(
            jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(scPrincipal)
                    .addGroup(jpTablaLayout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 420, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        jpTablaLayout.setVerticalGroup(
            jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 194, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
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
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btnNuevo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnModificar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminar)
                .addGap(26, 26, 26))
        );

        jtpEdicion.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jtpEdicion.setName(""); // NOI18N

        jpEdicion.setBackground(new java.awt.Color(233, 255, 255));
        jpEdicion.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        lblCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo.setForeground(new java.awt.Color(102, 102, 102));
        lblCodigo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCodigo.setText("Código:");
        lblCodigo.setFocusable(false);

        txtCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo.setEnabled(false);

        lblFechaIngreso.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaIngreso.setForeground(new java.awt.Color(102, 102, 102));
        lblFechaIngreso.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFechaIngreso.setText("Fecha de inscripción*:");
        lblFechaIngreso.setToolTipText("");
        lblFechaIngreso.setFocusable(false);

        lblNombre.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(102, 102, 102));
        lblNombre.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNombre.setText("Nombre*:");
        lblNombre.setFocusable(false);

        txtNombre.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtNombre.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNombre.setEnabled(false);
        txtNombre.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreKeyTyped(evt);
            }
        });

        lblApellido.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblApellido.setForeground(new java.awt.Color(102, 102, 102));
        lblApellido.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblApellido.setText("Apellido*:");
        lblApellido.setFocusable(false);

        txtApellido.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtApellido.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtApellido.setEnabled(false);
        txtApellido.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtApellidoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtApellidoKeyTyped(evt);
            }
        });

        lblTelefono.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblTelefono.setForeground(new java.awt.Color(102, 102, 102));
        lblTelefono.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTelefono.setText("Teléfono:");
        lblTelefono.setFocusable(false);

        txtTelefono.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtTelefono.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTelefono.setEnabled(false);
        txtTelefono.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTelefonoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoKeyTyped(evt);
            }
        });

        lblEmail.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblEmail.setForeground(new java.awt.Color(102, 102, 102));
        lblEmail.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEmail.setText("Email:");
        lblEmail.setFocusable(false);

        txtEmail.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtEmail.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtEmail.setEnabled(false);
        txtEmail.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEmailKeyTyped(evt);
            }
        });

        lblObs.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblObs.setForeground(new java.awt.Color(102, 102, 102));
        lblObs.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblObs.setText("Obs:");
        lblObs.setFocusable(false);

        taObs.setColumns(20);
        taObs.setLineWrap(true);
        taObs.setRows(5);
        taObs.setWrapStyleWord(true);
        taObs.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        taObs.setEnabled(false);
        taObs.setPreferredSize(new java.awt.Dimension(212, 62));
        taObs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                taObsKeyPressed(evt);
            }
        });
        scpObs.setViewportView(taObs);

        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Campos con (*) son obligatorios");

        dcFechaInscripcion.setEnabled(false);
        dcFechaInscripcion.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N

        lblSexo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblSexo.setForeground(new java.awt.Color(102, 102, 102));
        lblSexo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSexo.setText("Sexo*:");
        lblSexo.setToolTipText("");
        lblSexo.setFocusable(false);

        cbSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MASCULINO", "FEMENINO", "SIN ESPECIFICAR" }));
        cbSexo.setEnabled(false);

        lblEstado.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblEstado.setForeground(new java.awt.Color(102, 102, 102));
        lblEstado.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEstado.setText("Estado*:");
        lblEstado.setToolTipText("");
        lblEstado.setFocusable(false);

        cbEstado.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "INACTIVO", "ACTIVO" }));
        cbEstado.setSelectedIndex(1);
        cbEstado.setEnabled(false);

        lblEstado1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblEstado1.setForeground(new java.awt.Color(102, 102, 102));
        lblEstado1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEstado1.setText("Apoderado*:");
        lblEstado1.setToolTipText("");
        lblEstado1.setFocusable(false);

        cbApoderado.setEnabled(false);

        lblCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCedula.setForeground(new java.awt.Color(102, 102, 102));
        lblCedula.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCedula.setText("N° de cédula*:");
        lblCedula.setFocusable(false);

        txtCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCedula.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCedula.setEnabled(false);
        txtCedula.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCedulaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCedulaKeyTyped(evt);
            }
        });

        dcFechaNacimiento.setEnabled(false);
        dcFechaNacimiento.setFont(new java.awt.Font("sansserif", 0, 11)); // NOI18N
        dcFechaNacimiento.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dcFechaNacimientoPropertyChange(evt);
            }
        });

        lblFechaNacimiento.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaNacimiento.setForeground(new java.awt.Color(102, 102, 102));
        lblFechaNacimiento.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFechaNacimiento.setText("Fecha de nacimiento*:");
        lblFechaNacimiento.setToolTipText("");
        lblFechaNacimiento.setFocusable(false);

        lblCedula1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCedula1.setForeground(new java.awt.Color(102, 102, 102));
        lblCedula1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCedula1.setText("Edad:");
        lblCedula1.setFocusable(false);

        txtEdad.setEditable(false);
        txtEdad.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtEdad.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEdad.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtEdad.setEnabled(false);
        txtEdad.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEdadKeyTyped(evt);
            }
        });

        btnBuscarApoderado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoBuscar.png"))); // NOI18N
        btnBuscarApoderado.setToolTipText("Buscar cliente");
        btnBuscarApoderado.setEnabled(false);
        btnBuscarApoderado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBuscarApoderadoActionPerformed(evt);
            }
        });

        chbSincedula.setText("No posee C.I.");
        chbSincedula.setEnabled(false);
        chbSincedula.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbSincedulaItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jpEdicionLayout = new javax.swing.GroupLayout(jpEdicion);
        jpEdicion.setLayout(jpEdicionLayout);
        jpEdicionLayout.setHorizontalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblFechaIngreso, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblNombre, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblApellido, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCedula, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblFechaNacimiento, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCodigo, javax.swing.GroupLayout.Alignment.TRAILING))
                .addGap(2, 2, 2)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtApellido, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEdicionLayout.createSequentialGroup()
                        .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 202, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 293, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEdicionLayout.createSequentialGroup()
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtCedula, javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jpEdicionLayout.createSequentialGroup()
                                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(dcFechaNacimiento, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE)
                                    .addComponent(dcFechaInscripcion, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(lblSexo, javax.swing.GroupLayout.DEFAULT_SIZE, 42, Short.MAX_VALUE)
                                    .addComponent(lblCedula1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jpEdicionLayout.createSequentialGroup()
                                .addGap(2, 2, 2)
                                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEdad)))
                            .addGroup(jpEdicionLayout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(chbSincedula)))))
                .addGap(18, 18, 18)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTelefono, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblObs, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEstado1, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblEstado, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(scpObs, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addComponent(cbApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 240, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(txtEmail, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 282, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbEstado, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50))
        );
        jpEdicionLayout.setVerticalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblObs, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scpObs, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(chbSincedula, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addGap(4, 4, 4)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEstado1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtEdad, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblCedula1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnBuscarApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(4, 4, 4)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(cbEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEstado, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblFechaIngreso, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        panel1.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel1.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric1.setText("ALUMNOS");
        labelMetric1.setDireccionDeSombra(110);
        labelMetric1.setFocusable(false);
        labelMetric1.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(labelMetric1, javax.swing.GroupLayout.PREFERRED_SIZE, 336, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(labelMetric1, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(7, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jtpEdicion)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(314, 314, 314))
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 239, Short.MAX_VALUE)
                    .addComponent(jpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(8, 8, 8)
                .addComponent(jtpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 244, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(11, Short.MAX_VALUE))
        );

        jtpEdicion.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 952, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 621, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("Alumno");
        getAccessibleContext().setAccessibleDescription("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuardarActionPerformed
        RegistroNuevoModificar();
    }//GEN-LAST:event_btnGuardarActionPerformed

    private void btnCancelarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCancelarActionPerformed
        Limpiar();
        ModoEdicion(false);
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
        Limpiar();
        ModoEdicion(false);

        TablaConsultaBDAll();
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

    private void txtTelefonoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtTelefonoKeyTyped

    private void txtTelefonoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoKeyPressed
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtTelefonoKeyPressed

    private void txtApellidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidoKeyTyped
        metodostxt.SoloTextoKeyTyped(evt);

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtApellido, 30);
    }//GEN-LAST:event_txtApellidoKeyTyped

    private void txtApellidoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidoKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtApellido, lblApellido);
    }//GEN-LAST:event_txtApellidoKeyReleased

    private void txtNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyTyped
        metodostxt.SoloTextoKeyTyped(evt);

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtNombre, 30);
    }//GEN-LAST:event_txtNombreKeyTyped

    private void txtNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtNombre, lblNombre);
    }//GEN-LAST:event_txtNombreKeyReleased

    private void taObsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_taObsKeyPressed
        char car = (char) evt.getKeyCode();
        if (car == evt.VK_TAB) {//Al apretar ENTER QUE HAGA ALGO
            btnGuardar.requestFocus();
        }
    }//GEN-LAST:event_taObsKeyPressed

    private void txtEmailKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyTyped
        metodostxt.BloquearTeclaKeyTyped(evt, KeyEvent.VK_SPACE);
    }//GEN-LAST:event_txtEmailKeyTyped

    private void txtEmailKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailKeyPressed

    }//GEN-LAST:event_txtEmailKeyPressed

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            ModoVistaPrevia();
        }
    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void txtCedulaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaKeyTyped
        // Verificar si la tecla pulsada no es un digito
        char caracter = evt.getKeyChar();
        if (((caracter < '0') || (caracter > '9')) && caracter != '-' && (caracter != '\b' /* corresponde a BACK_SPACE */)) {
            evt.consume(); // ignorar el evento de teclado
        }

        metodostxt.TxtCantidadCaracteresKeyTyped(txtCedula, 11);
    }//GEN-LAST:event_txtCedulaKeyTyped

    private void txtEdadKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEdadKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtEdadKeyTyped

    private void dcFechaNacimientoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dcFechaNacimientoPropertyChange
        //Obtener edad
        if (dcFechaNacimiento.getDate() != null) {
            SimpleDateFormat formatosuda = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaNac = LocalDate.parse(formatosuda.format(dcFechaNacimiento.getDate()), fmt);
            LocalDate ahora = LocalDate.now();
            Period periodo = Period.between(fechaNac, ahora);
            txtEdad.setText(periodo.getYears() + "");
        }
    }//GEN-LAST:event_dcFechaNacimientoPropertyChange

    private void btnBuscarApoderadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBuscarApoderadoActionPerformed
        BuscadorApoderado.setLocationRelativeTo(null);
        BuscadorApoderado.setVisible(true);
    }//GEN-LAST:event_btnBuscarApoderadoActionPerformed

    private void txtBuscarApoderadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarApoderadoKeyReleased
        metodos.FiltroJTable(txtBuscarApoderado.getText(), cbCampoBuscarApoderado.getSelectedIndex(), tbApoderado);

        if (tbApoderado.getRowCount() == 1) {
            lbCantRegistrosApoderado.setText(tbApoderado.getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistrosApoderado.setText(tbApoderado.getRowCount() + " Registros encontrados");
        }
    }//GEN-LAST:event_txtBuscarApoderadoKeyReleased

    private void tbApoderadoMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbApoderadoMousePressed
        if (evt.getClickCount() == 2) {
            int codselect = Integer.parseInt(tbApoderado.getValueAt(tbApoderado.getSelectedRow(), 0) + "");
            metodoscombo.SetSelectedCodigoItem(cbApoderado, codselect);
            BuscadorApoderado.dispose();
        }
    }//GEN-LAST:event_tbApoderadoMousePressed

    private void tbApoderadoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbApoderadoKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            int codselect = Integer.parseInt(tbApoderado.getValueAt(tbApoderado.getSelectedRow(), 0) + "");
            metodoscombo.SetSelectedCodigoItem(cbApoderado, codselect);
            BuscadorApoderado.dispose();
        }
    }//GEN-LAST:event_tbApoderadoKeyReleased

    private void txtCedulaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaKeyReleased
        if (txtCedula.getText().equals("") == false) {
            txtCedula.setText(metodostxt.StringPuntosMiles(txtCedula.getText()));
        }
    }//GEN-LAST:event_txtCedulaKeyReleased

    private void chbSincedulaItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbSincedulaItemStateChanged
        if (chbSincedula.isSelected()) {
            txtCedula.setEnabled(false);
            txtCedula.setText("0");
        } else {
            txtCedula.setText("");
            txtCedula.setEnabled(true);
        }
    }//GEN-LAST:event_chbSincedulaItemStateChanged

    private void tbPrincipalMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseClicked
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPrincipalMouseClicked

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
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ABMAlumno.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                ABMAlumno dialog = new ABMAlumno(new javax.swing.JFrame(), true);
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
    private javax.swing.JDialog BuscadorApoderado;
    private javax.swing.JButton btnBuscarApoderado;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JComboBox<MetodosCombo> cbApoderado;
    private javax.swing.JComboBox cbCampoBuscarApoderado;
    private javax.swing.JComboBox<String> cbEstado;
    private javax.swing.JComboBox<String> cbSexo;
    private javax.swing.JCheckBox chbSincedula;
    private com.toedter.calendar.JDateChooser dcFechaInscripcion;
    private com.toedter.calendar.JDateChooser dcFechaNacimiento;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpBotones2;
    private javax.swing.JPanel jpEdicion;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpTabla;
    private javax.swing.JTabbedPane jtpEdicion;
    private org.edisoncor.gui.label.LabelMetric labelMetric1;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lbCantRegistrosApoderado;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblBuscarCampoApoderado;
    private javax.swing.JLabel lblCedula;
    private javax.swing.JLabel lblCedula1;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblEstado;
    private javax.swing.JLabel lblEstado1;
    private javax.swing.JLabel lblFechaIngreso;
    private javax.swing.JLabel lblFechaNacimiento;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblObs;
    private javax.swing.JLabel lblSexo;
    private javax.swing.JLabel lblTelefono;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel6;
    private javax.swing.JScrollPane scApoderado;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JScrollPane scpObs;
    private javax.swing.JTextArea taObs;
    private javax.swing.JTable tbApoderado;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtBuscarApoderado;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtEdad;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtTelefono;
    // End of variables declaration//GEN-END:variables
}
