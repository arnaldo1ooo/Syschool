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
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import utilidades.Metodos;
import utilidades.MetodosTXT;
import static login.Login.codUsuario;
import utilidades.MetodosCombo;

/**
 *
 * @author Arnaldo Cantero
 */
public class ABMApoderado extends javax.swing.JDialog {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    MetodosCombo metodoscombo = new MetodosCombo();
    DefaultTableModel modelTablaPoderantes;

    public ABMApoderado(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        //Permiso Roles de usuario
        //Permiso Roles de usuario
        String permisos = metodos.PermisoRol(codUsuario, "APODERADO");
        btnNuevo.setVisible(permisos.contains("A"));
        btnModificar.setVisible(permisos.contains("M"));
        btnEliminar.setVisible(permisos.contains("B"));

        TablaConsultaBDAll(); //Trae todos los registros
        txtBuscar.requestFocus();

        //Cambiar color de disabled combo
        metodoscombo.CambiarColorDisabledCombo(cbSexo, Color.BLACK);

        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void RegistroNuevoModificar() {
        if (ComprobarCampos() == true) {
            String codigo = txtCodigoApoderado.getText();
            String cedula = metodostxt.StringSinPuntosMiles(txtCedula.getText()) + "";
            String nombre = metodos.MayusCadaPrimeraLetra(txtNombre.getText());
            String apellido = metodos.MayusCadaPrimeraLetra(txtApellido.getText());
            String sexo = cbSexo.getSelectedItem() + "";
            String direccion = metodos.MayusPrimeraLetra(txtDireccion.getText());
            String email = txtEmail.getText();
            String telefono = txtTelefono.getText();
            String obs = metodos.MayusPrimeraLetra(taObs.getText());

            if (txtCodigoApoderado.getText().equals("")) { //NUEVO REGISTRO
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de registrar este nuevo apoderado?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    String sentencia = "CALL SP_ApoderadoAlta ('" + cedula + "','" + nombre + "','" + apellido + "','"
                            + sexo + "','" + direccion + "','" + telefono + "','" + email + "','" + obs + "')";
                    con.EjecutarABM(sentencia, false);

                    NuevoModificarAlumno();

                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this, "Operación realizada correctamente");

                    TablaConsultaBDAll(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            } else { //MODIFICAR REGISTRO
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de modificar este apoderado?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    String sentencia = "CALL SP_ApoderadoModificar('" + codigo + "','" + cedula + "','" + nombre + "','" + apellido + "','" + sexo + "','" + direccion
                            + "','" + telefono + "','" + email + "','" + obs + "')";
                    con.EjecutarABM(sentencia, false);

                    NuevoModificarAlumno();

                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this, "Operación realizada correctamente");

                    TablaConsultaBDAll(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            }
        }
    }

    private void NuevoModificarAlumno() {
        //Guardar alumnos
        String codigoalumno, nombrealumno, apellidoalumno, cedulaalumno, fechanacimiento, fechainscripcion, sexoalumno, telefonoalumno, emailalumno, obsalumno;
        int idapoderado = -1;
        String estadoalumno, sentencia;
        SimpleDateFormat formatfechaBD = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat formatfechaSuda = new SimpleDateFormat("dd/MM/yyyy");

        for (int i = 0; i < tbPoderantes.getRowCount(); i++) {
            codigoalumno = tbPoderantes.getValueAt(i, 0) + "";
            nombrealumno = metodos.MayusCadaPrimeraLetra(tbPoderantes.getValueAt(i, 1) + "");
            apellidoalumno = metodos.MayusCadaPrimeraLetra(tbPoderantes.getValueAt(i, 2) + "");
            cedulaalumno = metodostxt.StringSinPuntosMiles(tbPoderantes.getValueAt(i, 3) + "") + "";
            try {//Convertir fechas
                fechanacimiento = formatfechaBD.format(formatfechaSuda.parse(tbPoderantes.getValueAt(i, 4) + ""));
                fechainscripcion = formatfechaBD.format(formatfechaSuda.parse(tbPoderantes.getValueAt(i, 5) + ""));
            } catch (ParseException e) {
                e.printStackTrace();
                return;
            }
            sexoalumno = tbPoderantes.getValueAt(i, 6) + "";
            telefonoalumno = tbPoderantes.getValueAt(i, 7) + "";
            emailalumno = tbPoderantes.getValueAt(i, 8) + "";
            obsalumno = tbPoderantes.getValueAt(i, 9) + "";

            //Buscar el id del ultimo apoderado agregado
            if (txtCodigoApoderado.getText().equals("")) { //Si es nuevo apoderado
                try {
                    con = con.ObtenerRSSentencia("SELECT MAX(apo_codigo) AS idapoderado FROM apoderado");
                    if (con.getResultSet().next()) {
                        idapoderado = con.getResultSet().getInt("idapoderado");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    return;
                }
                con.DesconectarBasedeDatos();
            } else {
                idapoderado = Integer.parseInt(txtCodigoApoderado.getText());
            }

            estadoalumno = tbPoderantes.getValueAt(i, 10) + "";
            switch (estadoalumno) {
                case "ACTIVO":
                    estadoalumno = "1";
                    break;
                case "INACTIVO":
                    estadoalumno = "0";
                    break;
                default:
                    JOptionPane.showMessageDialog(this, "Estado no encontrado", "Error", JOptionPane.ERROR_MESSAGE);
                    break;
            }

            if (codigoalumno.equals("")) { //Si es un alumno nuevo
                sentencia = "CALL SP_AlumnoAlta('" + nombrealumno + "','" + apellidoalumno + "','" + cedulaalumno + "','" + fechanacimiento + "','" + fechainscripcion
                        + "','" + sexoalumno + "','" + telefonoalumno + "','" + emailalumno + "','" + obsalumno + "','" + idapoderado + "','" + estadoalumno + "')";
                con.EjecutarABM(sentencia, false);
            } else {
                sentencia = "CALL SP_AlumnoModificar('" + codigoalumno + "','" + nombrealumno + "','" + apellidoalumno + "','" + cedulaalumno + "','" + fechanacimiento
                        + "','" + fechainscripcion + "','" + sexoalumno + "','" + telefonoalumno + "','" + emailalumno + "','" + obsalumno + "','" + idapoderado + "','" + estadoalumno + "')";
                con.EjecutarABM(sentencia, false);
            }
        }
    }

    private void RegistroEliminar() {
        int filasel = tbPrincipal.getSelectedRow();
        if (filasel != -1) {
            Toolkit.getDefaultToolkit().beep();
            int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de eliminar este apoderado? Tambien se ELIMINARÁN "
                    + "los ALUMNOS A SU CARGO y sus respectivas MATRICULAS y PAGOS", "Confirmación", JOptionPane.YES_OPTION);
            if (JOptionPane.YES_OPTION == confirmado) {
                int codigo = Integer.parseInt(tbPrincipal.getModel().getValueAt(filasel, 0) + "");
                String sentencia = "CALL SP_ApoderadoEliminar(" + codigo + ")";
                con.EjecutarABM(sentencia, true);

                TablaConsultaBDAll(); //Actualizar tabla
                ModoEdicion(false);
                Limpiar();
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtBuscar.requestFocus();
        }
    }

    private void TablaConsultaBDAll() {//Realiza la consulta de los productos que tenemos en la base de datos
        String sentencia = "CALL SP_ApoderadoConsulta";
        String titlesJtabla[] = {"Código", "N° de Cédula", "Nombre", "Apellido", "Sexo", "Dirección", "Teléfono", "Email", "Observación"};
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
        if (tbPrincipal.getRowCount() > 0) {
            txtCodigoApoderado.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0) + ""));
            txtCedula.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 1) + ""));
            txtCedula.setText(metodostxt.StringPuntosMiles(txtCedula.getText()));
            txtNombre.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 2) + ""));
            txtApellido.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 3) + ""));
            cbSexo.setSelectedItem(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 4) + ""));
            txtDireccion.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 5) + ""));
            txtTelefono.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 6) + ""));
            txtEmail.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 7) + ""));
            taObs.setText(metodos.SiStringEsNull(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 8) + ""));

            ConsultaPoderantes();
        }
    }

    private void ModoEdicion(boolean valor) {
        jtpEdicion.setSelectedIndex(0);
        txtBuscar.setEnabled(!valor);
        tbPrincipal.setEnabled(!valor);
        txtCedula.setEnabled(valor);
        txtNombre.setEnabled(valor);
        txtApellido.setEnabled(valor);
        cbSexo.setEnabled(valor);
        txtDireccion.setEnabled(valor);
        txtEmail.setEnabled(valor);
        txtTelefono.setEnabled(valor);
        taObs.setEnabled(valor);
        btnNuevo.setEnabled(!valor);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);
        tbPoderantes.setEnabled(valor);
        btnNuevoAlumno.setEnabled(valor);
        btnModificarAlumno.setEnabled(false);
        btnEliminarAlumno.setEnabled(false);

        txtNombre.requestFocus();
    }

    private void Limpiar() {
        txtCodigoApoderado.setText("");
        txtCedula.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        cbSexo.setSelectedIndex(0);
        txtDireccion.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        taObs.setText("");

        lblCedula.setForeground(new Color(102, 102, 102));
        lblNombre.setForeground(new Color(102, 102, 102));
        lblApellido.setForeground(new Color(102, 102, 102));
        lblDireccion.setForeground(new Color(102, 102, 102));

        txtBuscar.requestFocus();
        tbPrincipal.clearSelection();

        modelTablaPoderantes = (DefaultTableModel) tbPoderantes.getModel();
        modelTablaPoderantes.setRowCount(0);
    }

    public boolean ComprobarCampos() {
        if (metodostxt.ValidarCampoVacioTXT(txtCedula, lblCedula) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtNombre, lblNombre) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtApellido, lblApellido) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtDireccion, lblDireccion) == false) {
            return false;
        }

        if (txtCodigoApoderado.getText().equals("")) {
            try {
                con = con.ObtenerRSSentencia("SELECT apo_cedula FROM apoderado WHERE apo_cedula='" + txtCedula.getText() + "'");
                if (con.getResultSet().next() == true) { //Si ya existe el numero de cedula en la tabla
                    System.out.println("El N° de cédula ingresado ya existe!");
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "El N° de cédula ingresado ya se encuentra registrado", "Error", JOptionPane.ERROR_MESSAGE);
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
        return true;
    }

//--------------------------iniComponent()No tocar----------------------------//
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        AltaAlumno = new javax.swing.JDialog();
        pnAltaAlumno = new org.edisoncor.gui.panel.Panel();
        lblNombreAlumno = new javax.swing.JLabel();
        txtNombreAlumno = new javax.swing.JTextField();
        lblApellidoAlumno = new javax.swing.JLabel();
        txtApellidoAlumno = new javax.swing.JTextField();
        lblCedulaAlumno = new javax.swing.JLabel();
        txtCedulaAlumno = new javax.swing.JTextField();
        lblFechaNacimiento = new javax.swing.JLabel();
        dcFechaNacimientoAlumno = new com.toedter.calendar.JDateChooser();
        lblEdadAlumno = new javax.swing.JLabel();
        txtEdadAlumno = new javax.swing.JTextField();
        lblFechaInscripcion = new javax.swing.JLabel();
        dcFechaInscripcionAlumno = new com.toedter.calendar.JDateChooser();
        lblSexoAlumno = new javax.swing.JLabel();
        cbSexoAlumno = new javax.swing.JComboBox<>();
        lblTelefonoAlumno = new javax.swing.JLabel();
        txtTelefonoAlumno = new javax.swing.JTextField();
        lblEmailAlumno = new javax.swing.JLabel();
        txtEmailAlumno = new javax.swing.JTextField();
        lblObsAlumno = new javax.swing.JLabel();
        scpObsAlumno = new javax.swing.JScrollPane();
        taObsAlumno = new javax.swing.JTextArea();
        lblEstadoAlumno = new javax.swing.JLabel();
        cbEstadoAlumno = new javax.swing.JComboBox<>();
        jLabel3 = new javax.swing.JLabel();
        btnAgregarAlumno = new org.edisoncor.gui.button.ButtonSeven();
        panel1 = new org.edisoncor.gui.panel.Panel();
        lmTitulo = new org.edisoncor.gui.label.LabelMetric();
        lblNombreAlumno1 = new javax.swing.JLabel();
        txtCodigoAlumno = new javax.swing.JTextField();
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
        txtCodigoApoderado = new javax.swing.JTextField();
        lblCedula = new javax.swing.JLabel();
        txtCedula = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblApellido = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        lblDireccion = new javax.swing.JLabel();
        txtDireccion = new javax.swing.JTextField();
        lblTelefono = new javax.swing.JLabel();
        txtTelefono = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblObs = new javax.swing.JLabel();
        scpObs = new javax.swing.JScrollPane();
        taObs = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        cbSexo = new javax.swing.JComboBox<>();
        lblSexo = new javax.swing.JLabel();
        jpAlumnosACargo = new javax.swing.JPanel();
        scPrincipal1 = new javax.swing.JScrollPane();
        tbPoderantes = new javax.swing.JTable(){
            public boolean isCellEditable(int rowIndex, int colIndex) {
                return false; //Disallow the editing of any cell
            }
        };
        btnNuevoAlumno = new javax.swing.JButton();
        btnModificarAlumno = new javax.swing.JButton();
        btnEliminarAlumno = new javax.swing.JButton();
        jpBotones2 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();

        AltaAlumno.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        AltaAlumno.setTitle("Ventana Nuevo Alumno");
        AltaAlumno.setModal(true);
        AltaAlumno.setResizable(false);
        AltaAlumno.setSize(new java.awt.Dimension(848, 370));

        pnAltaAlumno.setPreferredSize(new java.awt.Dimension(848, 370));

        lblNombreAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblNombreAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblNombreAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNombreAlumno.setText("Nombre*:");

        txtNombreAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtNombreAlumno.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtNombreAlumno.setNextFocusableComponent(txtApellidoAlumno);
        txtNombreAlumno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtNombreAlumnoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtNombreAlumnoKeyTyped(evt);
            }
        });

        lblApellidoAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblApellidoAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblApellidoAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblApellidoAlumno.setText("Apellido*:");

        txtApellidoAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtApellidoAlumno.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtApellidoAlumno.setNextFocusableComponent(txtCedulaAlumno);
        txtApellidoAlumno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtApellidoAlumnoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtApellidoAlumnoKeyTyped(evt);
            }
        });

        lblCedulaAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCedulaAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblCedulaAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCedulaAlumno.setText("N° de cédula*:");

        txtCedulaAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCedulaAlumno.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCedulaAlumno.setNextFocusableComponent(dcFechaNacimientoAlumno);
        txtCedulaAlumno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCedulaAlumnoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCedulaAlumnoKeyTyped(evt);
            }
        });

        lblFechaNacimiento.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaNacimiento.setForeground(new java.awt.Color(255, 255, 255));
        lblFechaNacimiento.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFechaNacimiento.setText("Fecha de nacimiento*:");
        lblFechaNacimiento.setToolTipText("");

        dcFechaNacimientoAlumno.setNextFocusableComponent(dcFechaInscripcionAlumno);
        dcFechaNacimientoAlumno.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                dcFechaNacimientoAlumnoPropertyChange(evt);
            }
        });

        lblEdadAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblEdadAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblEdadAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEdadAlumno.setText("Edad:");

        txtEdadAlumno.setEditable(false);
        txtEdadAlumno.setFont(new java.awt.Font("SansSerif", 1, 14)); // NOI18N
        txtEdadAlumno.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        txtEdadAlumno.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtEdadAlumno.setEnabled(false);

        lblFechaInscripcion.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaInscripcion.setForeground(new java.awt.Color(255, 255, 255));
        lblFechaInscripcion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblFechaInscripcion.setText("Fecha de inscripción*:");
        lblFechaInscripcion.setToolTipText("");

        dcFechaInscripcionAlumno.setNextFocusableComponent(txtTelefonoAlumno);

        lblSexoAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblSexoAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblSexoAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSexoAlumno.setText("Sexo*:");
        lblSexoAlumno.setToolTipText("");

        cbSexoAlumno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MASCULINO", "FEMENINO", "SIN ESPECIFICAR" }));

        lblTelefonoAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblTelefonoAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblTelefonoAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblTelefonoAlumno.setText("Teléfono:");

        txtTelefonoAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtTelefonoAlumno.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtTelefonoAlumno.setNextFocusableComponent(txtEmailAlumno);
        txtTelefonoAlumno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTelefonoAlumnoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtTelefonoAlumnoKeyTyped(evt);
            }
        });

        lblEmailAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblEmailAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblEmailAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEmailAlumno.setText("Email:");

        txtEmailAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtEmailAlumno.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtEmailAlumno.setNextFocusableComponent(taObsAlumno);
        txtEmailAlumno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtEmailAlumnoKeyPressed(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtEmailAlumnoKeyTyped(evt);
            }
        });

        lblObsAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblObsAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblObsAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblObsAlumno.setText("Obs:");

        taObsAlumno.setColumns(20);
        taObsAlumno.setRows(5);
        taObsAlumno.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        taObsAlumno.setNextFocusableComponent(cbEstadoAlumno);
        taObsAlumno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                taObsAlumnoKeyPressed(evt);
            }
        });
        scpObsAlumno.setViewportView(taObsAlumno);

        lblEstadoAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblEstadoAlumno.setForeground(new java.awt.Color(255, 255, 255));
        lblEstadoAlumno.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblEstadoAlumno.setText("Estado*:");
        lblEstadoAlumno.setToolTipText("");

        cbEstadoAlumno.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "INACTIVO", "ACTIVO" }));
        cbEstadoAlumno.setSelectedIndex(1);

        jLabel3.setForeground(new java.awt.Color(0, 255, 255));
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel3.setText("Campos con (*) son obligatorios");

        btnAgregarAlumno.setBackground(new java.awt.Color(0, 153, 153));
        btnAgregarAlumno.setText("Agregar");
        btnAgregarAlumno.setColorBrillo(new java.awt.Color(0, 153, 153));
        btnAgregarAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAgregarAlumnoActionPerformed(evt);
            }
        });

        panel1.setColorSecundario(new java.awt.Color(32, 39, 55));

        lmTitulo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lmTitulo.setText("Nuevo Alumno");
        lmTitulo.setDireccionDeSombra(110);
        lmTitulo.setFont(new java.awt.Font("Cooper Black", 0, 28)); // NOI18N

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addComponent(lmTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(529, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(lmTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        lblNombreAlumno1.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblNombreAlumno1.setForeground(new java.awt.Color(255, 255, 255));
        lblNombreAlumno1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblNombreAlumno1.setText("Codigo:");

        txtCodigoAlumno.setEditable(false);
        txtCodigoAlumno.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigoAlumno.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigoAlumno.setEnabled(false);
        txtCodigoAlumno.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCodigoAlumnoKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCodigoAlumnoKeyTyped(evt);
            }
        });

        javax.swing.GroupLayout pnAltaAlumnoLayout = new javax.swing.GroupLayout(pnAltaAlumno);
        pnAltaAlumno.setLayout(pnAltaAlumnoLayout);
        pnAltaAlumnoLayout.setHorizontalGroup(
            pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(pnAltaAlumnoLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(pnAltaAlumnoLayout.createSequentialGroup()
                        .addComponent(btnAgregarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(160, 160, 160)
                        .addComponent(jLabel3))
                    .addGroup(pnAltaAlumnoLayout.createSequentialGroup()
                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnAltaAlumnoLayout.createSequentialGroup()
                                .addComponent(lblNombreAlumno1, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(2, 2, 2)
                                .addComponent(txtCodigoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(170, 170, 170))
                            .addGroup(pnAltaAlumnoLayout.createSequentialGroup()
                                .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(pnAltaAlumnoLayout.createSequentialGroup()
                                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblCedulaAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblApellidoAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblNombreAlumno, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(2, 2, 2)
                                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                            .addComponent(txtApellidoAlumno, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtCedulaAlumno, javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(txtNombreAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 301, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGroup(pnAltaAlumnoLayout.createSequentialGroup()
                                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblFechaNacimiento, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblFechaInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(3, 3, 3)
                                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(dcFechaNacimientoAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(dcFechaInscripcionAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(lblSexoAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                            .addComponent(lblEdadAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGap(2, 2, 2)
                                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                            .addComponent(cbSexoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(txtEdadAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 116, javax.swing.GroupLayout.PREFERRED_SIZE))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)))
                        .addGap(22, 22, 22)
                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblObsAlumno, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEmailAlumno, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblTelefonoAlumno, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblEstadoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(2, 2, 2)
                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(txtEmailAlumno)
                            .addComponent(txtTelefonoAlumno)
                            .addComponent(cbEstadoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(scpObsAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 255, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnAltaAlumnoLayout.setVerticalGroup(
            pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnAltaAlumnoLayout.createSequentialGroup()
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblNombreAlumno1, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCodigoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnAltaAlumnoLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblNombreAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtNombreAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTelefonoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtTelefonoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblApellidoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtApellidoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEmailAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtEmailAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnAltaAlumnoLayout.createSequentialGroup()
                                .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblCedulaAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCedulaAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(4, 4, 4)
                                .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                                    .addComponent(lblFechaNacimiento, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(dcFechaNacimientoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblEdadAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtEdadAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(scpObsAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblObsAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(pnAltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblFechaInscripcion, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(dcFechaInscripcionAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSexoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbSexoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblEstadoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(cbEstadoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnAltaAlumnoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnAgregarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(60, 60, 60))))
        );

        javax.swing.GroupLayout AltaAlumnoLayout = new javax.swing.GroupLayout(AltaAlumno.getContentPane());
        AltaAlumno.getContentPane().setLayout(AltaAlumnoLayout);
        AltaAlumnoLayout.setHorizontalGroup(
            AltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnAltaAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        AltaAlumnoLayout.setVerticalGroup(
            AltaAlumnoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnAltaAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ventana Apoderados");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpTabla.setBackground(new java.awt.Color(233, 255, 255));
        jpTabla.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoBuscar.png"))); // NOI18N
        jLabel10.setText("  BUSCAR ");
        jLabel10.setFocusable(false);

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
        lblBuscarCampo.setFocusable(false);

        lbCantRegistros.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        lbCantRegistros.setForeground(new java.awt.Color(153, 153, 0));
        lbCantRegistros.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbCantRegistros.setText("0 Registros encontrados");
        lbCantRegistros.setFocusable(false);
        lbCantRegistros.setPreferredSize(new java.awt.Dimension(57, 25));

        javax.swing.GroupLayout jpTablaLayout = new javax.swing.GroupLayout(jpTabla);
        jpTabla.setLayout(jpTablaLayout);
        jpTablaLayout.setHorizontalGroup(
            jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpTablaLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scPrincipal, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpTablaLayout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpTablaLayout.createSequentialGroup()
                                .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 394, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(lblBuscarCampo)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lbCantRegistros, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 357, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
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
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(13, Short.MAX_VALUE)
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

        txtCodigoApoderado.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigoApoderado.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigoApoderado.setEnabled(false);

        lblCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCedula.setForeground(new java.awt.Color(102, 102, 102));
        lblCedula.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblCedula.setText("N° de cédula*:");
        lblCedula.setToolTipText("");
        lblCedula.setFocusable(false);

        txtCedula.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCedula.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCedula.setEnabled(false);
        txtCedula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtCedulaActionPerformed(evt);
            }
        });
        txtCedula.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCedulaKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtCedulaKeyTyped(evt);
            }
        });

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

        lblDireccion.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblDireccion.setForeground(new java.awt.Color(102, 102, 102));
        lblDireccion.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblDireccion.setText("Dirección*:");
        lblDireccion.setFocusable(false);

        txtDireccion.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtDireccion.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtDireccion.setEnabled(false);
        txtDireccion.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtDireccionKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDireccionKeyReleased(evt);
            }
            public void keyTyped(java.awt.event.KeyEvent evt) {
                txtDireccionKeyTyped(evt);
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
        taObs.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                taObsKeyPressed(evt);
            }
        });
        scpObs.setViewportView(taObs);

        jLabel2.setForeground(new java.awt.Color(0, 0, 153));
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        jLabel2.setText("Campos con (*) son obligatorios");

        cbSexo.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "MASCULINO", "FEMENINO", "SIN ESPECIFICAR" }));
        cbSexo.setEnabled(false);

        lblSexo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblSexo.setForeground(new java.awt.Color(102, 102, 102));
        lblSexo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lblSexo.setText("Sexo*:");
        lblSexo.setToolTipText("");
        lblSexo.setFocusable(false);

        javax.swing.GroupLayout jpEdicionLayout = new javax.swing.GroupLayout(jpEdicion);
        jpEdicion.setLayout(jpEdicionLayout);
        jpEdicionLayout.setHorizontalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(lblCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(lblSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 94, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(2, 2, 2)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(txtCedula, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jpEdicionLayout.createSequentialGroup()
                                .addComponent(txtCodigoApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jLabel2))
                            .addComponent(txtNombre, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtApellido, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 259, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 161, Short.MAX_VALUE)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblEmail, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblDireccion, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblTelefono, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblObs, javax.swing.GroupLayout.PREFERRED_SIZE, 73, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4))
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addComponent(cbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 123, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(txtEmail)
                    .addComponent(txtTelefono)
                    .addComponent(txtDireccion, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scpObs, javax.swing.GroupLayout.DEFAULT_SIZE, 274, Short.MAX_VALUE))
                .addGap(123, 123, 123))
        );
        jpEdicionLayout.setVerticalGroup(
            jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpEdicionLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtDireccion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtCodigoApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                        .addComponent(lblTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtTelefono, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblObs, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpEdicionLayout.createSequentialGroup()
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(lblCedula, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(txtCedula, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jpEdicionLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lblSexo, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(scpObs, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(7, Short.MAX_VALUE))
        );

        jtpEdicion.addTab("Edición", jpEdicion);

        jpAlumnosACargo.setBackground(new java.awt.Color(233, 255, 255));
        jpAlumnosACargo.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        tbPoderantes.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tbPoderantes.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        tbPoderantes.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Nombre", "Apellido", "N° de Cédula", "Fecha de nac.", "Fecha de inscr.", "Sexo", "Teléfono", "Email", "Obs", "Estado"
            }
        ));
        tbPoderantes.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        tbPoderantes.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        tbPoderantes.setEnabled(false);
        tbPoderantes.setGridColor(new java.awt.Color(0, 153, 204));
        tbPoderantes.setOpaque(false);
        tbPoderantes.setRowHeight(20);
        tbPoderantes.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tbPoderantes.getTableHeader().setReorderingAllowed(false);
        tbPoderantes.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbPoderantesMousePressed(evt);
            }
        });
        tbPoderantes.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                tbPoderantesKeyReleased(evt);
            }
        });
        scPrincipal1.setViewportView(tbPoderantes);

        btnNuevoAlumno.setBackground(new java.awt.Color(0, 153, 153));
        btnNuevoAlumno.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnNuevoAlumno.setForeground(new java.awt.Color(255, 255, 255));
        btnNuevoAlumno.setText("Nuevo alumno");
        btnNuevoAlumno.setEnabled(false);
        btnNuevoAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNuevoAlumnoActionPerformed(evt);
            }
        });

        btnModificarAlumno.setBackground(new java.awt.Color(0, 102, 204));
        btnModificarAlumno.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnModificarAlumno.setForeground(new java.awt.Color(255, 255, 255));
        btnModificarAlumno.setText("Modificar alumno");
        btnModificarAlumno.setEnabled(false);
        btnModificarAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnModificarAlumnoActionPerformed(evt);
            }
        });

        btnEliminarAlumno.setBackground(new java.awt.Color(255, 153, 153));
        btnEliminarAlumno.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        btnEliminarAlumno.setForeground(new java.awt.Color(255, 255, 255));
        btnEliminarAlumno.setText("Eliminar alumno");
        btnEliminarAlumno.setEnabled(false);
        btnEliminarAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEliminarAlumnoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jpAlumnosACargoLayout = new javax.swing.GroupLayout(jpAlumnosACargo);
        jpAlumnosACargo.setLayout(jpAlumnosACargoLayout);
        jpAlumnosACargoLayout.setHorizontalGroup(
            jpAlumnosACargoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpAlumnosACargoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(scPrincipal1, javax.swing.GroupLayout.DEFAULT_SIZE, 857, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpAlumnosACargoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnModificarAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnEliminarAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNuevoAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(12, 12, 12))
        );
        jpAlumnosACargoLayout.setVerticalGroup(
            jpAlumnosACargoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpAlumnosACargoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpAlumnosACargoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(scPrincipal1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpAlumnosACargoLayout.createSequentialGroup()
                        .addGap(11, 11, 11)
                        .addComponent(btnNuevoAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModificarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnEliminarAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 39, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(16, Short.MAX_VALUE))
        );

        jtpEdicion.addTab("Poderantes (Alumnos a cargo)", jpAlumnosACargo);

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
            public void keyReleased(java.awt.event.KeyEvent evt) {
                btnGuardarKeyReleased(evt);
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
        labelMetric2.setText("APODERADOS");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFocusable(false);
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

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jtpEdicion)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addGap(349, 349, 349)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        jtpEdicion.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 1023, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 578, Short.MAX_VALUE)
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
    }//GEN-LAST:event_btnNuevoActionPerformed

    private void btnModificarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarActionPerformed
        ModoEdicion(true);
    }//GEN-LAST:event_btnModificarActionPerformed

    private void btnEliminarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarActionPerformed
        RegistroEliminar();

        TablaConsultaBDAll();
        Limpiar();
        ModoEdicion(false);
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

    private void txtDireccionKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDireccionKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDireccionKeyTyped

    private void txtDireccionKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDireccionKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtDireccion, lblDireccion);
    }//GEN-LAST:event_txtDireccionKeyReleased

    private void txtDireccionKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDireccionKeyPressed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtDireccionKeyPressed

    private void txtCedulaKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaKeyTyped
        //Solo numero y "-"
        char car = evt.getKeyChar();
        if (car != '-') {
            metodostxt.SoloNumeroEnteroKeyTyped(evt);
        }

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtCedula, 15);
    }//GEN-LAST:event_txtCedulaKeyTyped

    private void txtCedulaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtCedula, lblCedula);

        txtCedula.setText(metodostxt.StringPuntosMiles(txtCedula.getText()));

    }//GEN-LAST:event_txtCedulaKeyReleased

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

    private void txtCedulaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtCedulaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCedulaActionPerformed

    private void tbPoderantesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPoderantesMousePressed
        if (tbPoderantes.isEnabled() == true) {
            btnModificarAlumno.setEnabled(true);
            btnEliminarAlumno.setEnabled(true);
        }
    }//GEN-LAST:event_tbPoderantesMousePressed

    private void tbPoderantesKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPoderantesKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPoderantesKeyReleased

    private void btnModificarAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnModificarAlumnoActionPerformed
        //Cargar datos del alumno a modificar
        lmTitulo.setText("Modificar alumno");
        btnAgregarAlumno.setText("Modificar");
        txtCodigoAlumno.setText(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 0) + "");
        txtNombreAlumno.setText(metodos.SiStringEsNull(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 1) + ""));
        txtApellidoAlumno.setText(metodos.SiStringEsNull(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 2) + ""));
        txtCedulaAlumno.setText(metodos.SiStringEsNull(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 3) + ""));

        try {
            Date fechaParseada = new SimpleDateFormat("dd/MM/yyyy").parse(metodos.SiStringEsNull(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 4).toString() + ""));
            dcFechaNacimientoAlumno.setDate(fechaParseada);

            //Obtener edad
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaNac = LocalDate.parse(metodos.SiStringEsNull(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 4) + ""), fmt);
            LocalDate ahora = LocalDate.now();
            Period periodo = Period.between(fechaNac, ahora);
            txtEdadAlumno.setText(metodos.SiStringEsNull(periodo.getYears() + ""));

            fechaParseada = new SimpleDateFormat("dd/MM/yyyy").parse(metodos.SiStringEsNull(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 5).toString() + ""));
            dcFechaInscripcionAlumno.setDate(fechaParseada);
        } catch (ParseException e) {
            System.out.println("Error al parsear fecha");
            e.printStackTrace();
        }

        cbSexoAlumno.setSelectedItem(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 6).toString());
        txtTelefonoAlumno.setText(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 7).toString());
        txtEmailAlumno.setText(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 8).toString());
        taObsAlumno.setText(tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 9).toString());
        String estado = tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 10).toString();
        cbEstadoAlumno.setSelectedItem(estado);

        AltaAlumno.setLocationRelativeTo(this);
        AltaAlumno.setVisible(true);
    }//GEN-LAST:event_btnModificarAlumnoActionPerformed

    private void taObsAlumnoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_taObsAlumnoKeyPressed
        char car = (char) evt.getKeyCode();
        if (car == KeyEvent.VK_TAB) {//Al apretar ENTER QUE HAGA ALGO
            btnGuardar.requestFocus();
        }
    }//GEN-LAST:event_taObsAlumnoKeyPressed

    private void txtCedulaAlumnoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaAlumnoKeyTyped
        // Verificar si la tecla pulsada no es un digito
        char caracter = evt.getKeyChar();
        if (((caracter < '0') || (caracter > '9')) && caracter != '-' && (caracter != '\b' /* corresponde a BACK_SPACE */)) {
            evt.consume(); // ignorar el evento de teclado
        }
    }//GEN-LAST:event_txtCedulaAlumnoKeyTyped

    private void dcFechaNacimientoAlumnoPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_dcFechaNacimientoAlumnoPropertyChange
        //Obtener edad
        if (dcFechaNacimientoAlumno.getDate() != null) {
            SimpleDateFormat formatosuda = new SimpleDateFormat("dd/MM/yyyy");
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            LocalDate fechaNac = LocalDate.parse(formatosuda.format(dcFechaNacimientoAlumno.getDate()), fmt);
            LocalDate ahora = LocalDate.now();
            Period periodo = Period.between(fechaNac, ahora);
            txtEdadAlumno.setText(periodo.getYears() + "");
        }
    }//GEN-LAST:event_dcFechaNacimientoAlumnoPropertyChange

    private void txtNombreAlumnoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreAlumnoKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtNombreAlumno, lblNombreAlumno);
    }//GEN-LAST:event_txtNombreAlumnoKeyReleased

    private void txtNombreAlumnoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreAlumnoKeyTyped
        metodostxt.SoloTextoKeyTyped(evt);

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtNombre, 30);
    }//GEN-LAST:event_txtNombreAlumnoKeyTyped

    private void txtApellidoAlumnoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidoAlumnoKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtApellidoAlumno, lblApellidoAlumno);
    }//GEN-LAST:event_txtApellidoAlumnoKeyReleased

    private void txtApellidoAlumnoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidoAlumnoKeyTyped
        metodostxt.SoloTextoKeyTyped(evt);

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtApellido, 30);
    }//GEN-LAST:event_txtApellidoAlumnoKeyTyped

    private void txtTelefonoAlumnoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoAlumnoKeyPressed
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtTelefonoAlumnoKeyPressed

    private void txtTelefonoAlumnoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTelefonoAlumnoKeyTyped
        metodostxt.SoloNumeroEnteroKeyTyped(evt);
    }//GEN-LAST:event_txtTelefonoAlumnoKeyTyped

    private void txtEmailAlumnoKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailAlumnoKeyPressed

    }//GEN-LAST:event_txtEmailAlumnoKeyPressed

    private void txtEmailAlumnoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtEmailAlumnoKeyTyped
        metodostxt.BloquearTeclaKeyTyped(evt, KeyEvent.VK_SPACE);
    }//GEN-LAST:event_txtEmailAlumnoKeyTyped

    private void btnAgregarAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAgregarAlumnoActionPerformed

        if (ComprobarCamposAlumnos() == true) {
            String nombre = metodos.MayusCadaPrimeraLetra(txtNombreAlumno.getText());
            String apellido = metodos.MayusCadaPrimeraLetra(txtApellidoAlumno.getText());
            String cedula = txtCedulaAlumno.getText();
            SimpleDateFormat formatofecha = new SimpleDateFormat("dd/MM/yyyy");
            String fechanacimiento = formatofecha.format(dcFechaNacimientoAlumno.getDate());
            String fechainscripcion = formatofecha.format(dcFechaInscripcionAlumno.getDate());
            String sexo = cbSexoAlumno.getSelectedItem() + "";
            String telefono = txtTelefonoAlumno.getText();
            String email = txtEmailAlumno.getText();
            String obs = taObsAlumno.getText();
            String estado = cbEstadoAlumno.getSelectedItem() + "";

            if (btnAgregarAlumno.getText().equals("Agregar")) {
                modelTablaPoderantes = (DefaultTableModel) tbPoderantes.getModel();
                modelTablaPoderantes.addRow(new Object[]{"", nombre, apellido, cedula, fechanacimiento, fechainscripcion, sexo, telefono, email, obs, estado});
            }
            if (btnAgregarAlumno.getText().equals("Modificar")) {
                int filaSelect = tbPoderantes.getSelectedRow();
                if (filaSelect != -1) {
                    tbPoderantes.setValueAt(nombre, filaSelect, 1);
                    tbPoderantes.setValueAt(apellido, filaSelect, 2);
                    tbPoderantes.setValueAt(cedula, filaSelect, 3);
                    tbPoderantes.setValueAt(fechanacimiento, filaSelect, 4);
                    tbPoderantes.setValueAt(fechainscripcion, filaSelect, 5);
                    tbPoderantes.setValueAt(sexo, filaSelect, 6);
                    tbPoderantes.setValueAt(telefono, filaSelect, 7);
                    tbPoderantes.setValueAt(email, filaSelect, 8);
                    tbPoderantes.setValueAt(obs, filaSelect, 9);
                    tbPoderantes.setValueAt(estado, filaSelect, 10);
                } else {
                    JOptionPane.showMessageDialog(AltaAlumno, "No se seleccionó ninguna fila en la tabla", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
            metodos.AnchuraColumna(tbPoderantes);
            AltaAlumno.dispose();
        }


    }//GEN-LAST:event_btnAgregarAlumnoActionPerformed

    private void txtCedulaAlumnoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCedulaAlumnoKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtCedulaAlumno, lblCedulaAlumno);

        txtCedulaAlumno.setText(metodostxt.StringPuntosMiles(txtCedulaAlumno.getText()));
    }//GEN-LAST:event_txtCedulaAlumnoKeyReleased

    private void btnNuevoAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNuevoAlumnoActionPerformed
        //Limpiar alumno
        btnAgregarAlumno.setText("Agregar");
        lmTitulo.setText("Nuevo alumno");

        txtCodigoAlumno.setText("");
        txtNombreAlumno.setText("");
        txtApellidoAlumno.setText("");
        txtCedulaAlumno.setText("");
        dcFechaNacimientoAlumno.setDate(null);
        txtEdadAlumno.setText("");
        dcFechaInscripcionAlumno.setDate(new Date());
        cbSexoAlumno.setSelectedIndex(0);
        txtTelefonoAlumno.setText("");
        txtEmailAlumno.setText("");
        taObsAlumno.setText("");
        cbEstadoAlumno.setSelectedIndex(1);
        lblNombreAlumno.setForeground(Color.WHITE);
        lblApellidoAlumno.setForeground(Color.WHITE);
        lblCedulaAlumno.setForeground(Color.WHITE);

        AltaAlumno.setLocationRelativeTo(this);
        AltaAlumno.setVisible(true);
    }//GEN-LAST:event_btnNuevoAlumnoActionPerformed

    private void btnEliminarAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEliminarAlumnoActionPerformed
        if (tbPoderantes.getSelectedRow() != -1) {
            String idalumno = tbPoderantes.getValueAt(tbPoderantes.getSelectedRow(), 0) + "";
            if (idalumno.equals("")) {
                Toolkit.getDefaultToolkit().beep();
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Deseas eliminar al alumno seleccionado?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {

                    modelTablaPoderantes = (DefaultTableModel) tbPoderantes.getModel();
                    modelTablaPoderantes.removeRow(tbPoderantes.getSelectedRow());

                }
            } else {
                int confirmado2 = JOptionPane.showConfirmDialog(this, "¿Deseas eliminar al alumno seleccionado?, TAMBIEN SE ELIMINARÁN LAS MATRICULAS DEL MISMO", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado2) {
                    con.EjecutarABM("CALL SP_AlumnoEliminar('" + idalumno + "')", true);
                }
            }
            btnModificar.setEnabled(false);
            btnEliminarAlumno.setEnabled(false);
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
            txtBuscar.requestFocus();
        }
    }//GEN-LAST:event_btnEliminarAlumnoActionPerformed

    private void txtCodigoAlumnoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoAlumnoKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoAlumnoKeyReleased

    private void txtCodigoAlumnoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCodigoAlumnoKeyTyped
        // TODO add your handling code here:
    }//GEN-LAST:event_txtCodigoAlumnoKeyTyped

    private void btnGuardarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_btnGuardarKeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_btnGuardarKeyReleased

    private void ConsultaPoderantes() {
        modelTablaPoderantes = (DefaultTableModel) tbPoderantes.getModel();//Cargamos campos de jtable al modeltable
        modelTablaPoderantes.setRowCount(0); //Vacia la tabla

        int codigoapoderado = Integer.parseInt(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0) + "");
        String sentencia = "CALL SP_ApoderadoAlumnosConsulta(" + codigoapoderado + ")";

        con = con.ObtenerRSSentencia(sentencia);

        try {
            int codigo, telefono;
            String nombre, apellido, cedula, fechanac, fechainsc, sexo, email, obs, estado;
            while (con.getResultSet().next()) {
                codigo = con.getResultSet().getInt("alu_codigo");
                nombre = con.getResultSet().getString("alu_nombre");
                apellido = con.getResultSet().getString("alu_apellido");
                cedula = metodostxt.StringPuntosMiles(con.getResultSet().getString("alu_cedula"));
                fechanac = con.getResultSet().getString("fechanacimiento");
                fechainsc = con.getResultSet().getString("fechainscripcion");
                sexo = con.getResultSet().getString("alu_sexo");
                telefono = con.getResultSet().getInt("alu_telefono");
                email = con.getResultSet().getString("alu_email");
                obs = con.getResultSet().getString("alu_obs");
                estado = con.getResultSet().getString("estado");

                modelTablaPoderantes.addRow(new Object[]{codigo, nombre, apellido, cedula, fechanac, fechainsc, sexo, telefono, email, obs, estado});
            }
            tbPoderantes.setModel(modelTablaPoderantes);
            metodos.AnchuraColumna(tbPoderantes);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    public boolean ComprobarCamposAlumnos() {
        if (metodostxt.ValidarCampoVacioTXT(txtNombreAlumno, lblNombreAlumno) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtApellidoAlumno, lblApellidoAlumno) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtCedulaAlumno, lblCedulaAlumno) == false) {
            return false;
        }

        //Validar
        if (txtCodigoApoderado.getText().equals("")) {
            try {
                con = con.ObtenerRSSentencia("SELECT alu_cedula FROM alumno WHERE alu_cedula='" + metodostxt.StringSinPuntosMiles(txtCedulaAlumno.getText()) + "'");
                if (con.getResultSet().next() == true) { //Si ya existe el numero de cedula en la tabla
                    System.out.println("El N° de cédula ingresado ya existe!");
                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(null, "El N° de cédula ingresado ya se encuentra registrado", "Error", JOptionPane.ERROR_MESSAGE);
                    lblCedulaAlumno.setForeground(Color.RED);
                    lblCedulaAlumno.requestFocus();
                    con.DesconectarBasedeDatos();
                    return false;
                }
            } catch (SQLException e) {
                System.out.println("Error al buscar si ci ya existe en bd: " + e);
            } catch (NullPointerException e) {
                System.out.println("La CI ingresada no existe en la bd, aprobado: " + e);
            }
        }

        try {
            if (dcFechaNacimientoAlumno.getDate().after(new Date()) == true) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(AltaAlumno, "La fecha de nacimiento no puede ser mayor a la fecha actual", "Advertencia", JOptionPane.WARNING_MESSAGE);
                dcFechaNacimientoAlumno.requestFocus();
                return false;
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        if (txtEdadAlumno.getText().equals("") == false) {
            if (Integer.parseInt(txtEdadAlumno.getText()) <= 2) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(AltaAlumno, "El alumno debe tener más de 2 años", "Advertencia", JOptionPane.WARNING_MESSAGE);
                dcFechaNacimientoAlumno.requestFocus();
                return false;
            }
        }

        if (dcFechaNacimientoAlumno.getDate() == null) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(AltaAlumno, "Ingrese una fecha de nacimiento válida", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dcFechaNacimientoAlumno.requestFocus();
            return false;
        }

        if (dcFechaInscripcionAlumno.getDate() == null) {
            Toolkit.getDefaultToolkit().beep();
            JOptionPane.showMessageDialog(AltaAlumno, "Ingrese una fecha de inscripción válida", "Advertencia", JOptionPane.WARNING_MESSAGE);
            dcFechaInscripcionAlumno.requestFocus();
            return false;
        }
        return true;
    }

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();
        ordenTabulador.add(txtNombre);
        ordenTabulador.add(txtApellido);
        ordenTabulador.add(txtCedula);
        ordenTabulador.add(txtDireccion);
        ordenTabulador.add(txtTelefono);
        ordenTabulador.add(txtEmail);
        ordenTabulador.add(taObs);
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
    private javax.swing.JDialog AltaAlumno;
    private org.edisoncor.gui.button.ButtonSeven btnAgregarAlumno;
    private javax.swing.JButton btnCancelar;
    private javax.swing.JButton btnEliminar;
    private javax.swing.JButton btnEliminarAlumno;
    private javax.swing.JButton btnGuardar;
    private javax.swing.JButton btnModificar;
    private javax.swing.JButton btnModificarAlumno;
    private javax.swing.JButton btnNuevo;
    private javax.swing.JButton btnNuevoAlumno;
    private javax.swing.JComboBox cbCampoBuscar;
    private javax.swing.JComboBox<String> cbEstadoAlumno;
    private javax.swing.JComboBox<String> cbSexo;
    private javax.swing.JComboBox<String> cbSexoAlumno;
    private com.toedter.calendar.JDateChooser dcFechaInscripcionAlumno;
    private com.toedter.calendar.JDateChooser dcFechaNacimientoAlumno;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jpAlumnosACargo;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpBotones2;
    private javax.swing.JPanel jpEdicion;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpTabla;
    private javax.swing.JTabbedPane jtpEdicion;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblApellidoAlumno;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblCedula;
    private javax.swing.JLabel lblCedulaAlumno;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblDireccion;
    private javax.swing.JLabel lblEdadAlumno;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblEmailAlumno;
    private javax.swing.JLabel lblEstadoAlumno;
    private javax.swing.JLabel lblFechaInscripcion;
    private javax.swing.JLabel lblFechaNacimiento;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblNombreAlumno;
    private javax.swing.JLabel lblNombreAlumno1;
    private javax.swing.JLabel lblObs;
    private javax.swing.JLabel lblObsAlumno;
    private javax.swing.JLabel lblSexo;
    private javax.swing.JLabel lblSexoAlumno;
    private javax.swing.JLabel lblTelefono;
    private javax.swing.JLabel lblTelefonoAlumno;
    private org.edisoncor.gui.label.LabelMetric lmTitulo;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel2;
    private org.edisoncor.gui.panel.Panel pnAltaAlumno;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JScrollPane scPrincipal1;
    private javax.swing.JScrollPane scpObs;
    private javax.swing.JScrollPane scpObsAlumno;
    private javax.swing.JTextArea taObs;
    private javax.swing.JTextArea taObsAlumno;
    private javax.swing.JTable tbPoderantes;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtApellidoAlumno;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCedula;
    private javax.swing.JTextField txtCedulaAlumno;
    private javax.swing.JTextField txtCodigoAlumno;
    private javax.swing.JTextField txtCodigoApoderado;
    private javax.swing.JTextField txtDireccion;
    private javax.swing.JTextField txtEdadAlumno;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtEmailAlumno;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtNombreAlumno;
    private javax.swing.JTextField txtTelefono;
    private javax.swing.JTextField txtTelefonoAlumno;
    // End of variables declaration//GEN-END:variables
}
