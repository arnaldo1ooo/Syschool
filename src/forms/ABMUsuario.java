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

import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import static login.Login.codUsuario;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;

/**
 *
 * @author Arnaldo Cantero
 */
public class ABMUsuario extends javax.swing.JDialog {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    MetodosCombo metodoscombo = new MetodosCombo();
    DefaultTableModel tablemodelPerfiles;
    DefaultTableModel tablemodelRoles;
    DefaultTableModel tablemodelPerfilModulos;

    public ABMUsuario(java.awt.Frame parent, Boolean modal) {
        super(parent, modal);
        initComponents();

        txtBuscar.requestFocus();
        
//Permiso Roles de usuario
        String permisos = metodos.PermisoRol(codUsuario, "USUARIO");
        btnNuevo.setVisible(permisos.contains("A"));
        btnModificar.setVisible(permisos.contains("M"));
        btnEliminar.setVisible(permisos.contains("B"));

        //Metodos
        TablaConsultaUsuarios(); //Trae todos los registros
        TablaAllPerfiles();
        
        OrdenTabulador();
    }

//--------------------------METODOS----------------------------//
    public void RegistroNuevoModificar() {
        if (ComprobarCampos() == true) {
            String codigo = txtCodigo.getText();
            String nombre = txtNombre.getText();
            String apellido = txtApellido.getText();
            String alias = txtAlias.getText();
            String pass = txtPass.getText();
            SimpleDateFormat formatoamericano = new SimpleDateFormat("yyyy-MM-dd");
            String fechacreacion = formatoamericano.format(dcFechaCreacion.getDate());

            if (codigo.equals("")) {
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Esta seguro crear este nuevo registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) { //NUEVO REGISTRO
                    String sentencia = "CALL SP_UsuarioAlta ('" + nombre + "','" + apellido + "','"
                            + alias + "','" + pass + "','" + fechacreacion + "')";
                    con.EjecutarABM(sentencia, false);
                    NuevoModificarPerfilUsuario();
                    NuevoModificarRolesUsuario();

                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this, "Registro creado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);

                    TablaConsultaUsuarios(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            } else { //MODIFICAR REGISTRO
                int confirmado = JOptionPane.showConfirmDialog(this, "¿Estás seguro de modificar este registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (JOptionPane.YES_OPTION == confirmado) {
                    String sentencia = "CALL SP_UsuarioModificar(" + codigo + ",'" + nombre + "','" + apellido + "','" + alias
                            + "','" + pass + "','" + fechacreacion + "')";
                    con.EjecutarABM(sentencia, false);
                    NuevoModificarPerfilUsuario();
                    NuevoModificarRolesUsuario();

                    Toolkit.getDefaultToolkit().beep();
                    JOptionPane.showMessageDialog(this, "Registro modificado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);

                    TablaConsultaUsuarios(); //Actualizar tabla
                    ModoEdicion(false);
                    Limpiar();
                }
            }
        }
    }

    private void RegistroEliminar() {
        int filasel;
        String codigo;
        try {
            filasel = tbPrincipal.getSelectedRow();
            if (filasel == -1) {
                JOptionPane.showMessageDialog(null, "No se ha seleccionado ninguna fila", "Advertencia", JOptionPane.WARNING_MESSAGE);
                txtBuscar.requestFocus();
            } else {
                int confirmado = javax.swing.JOptionPane.showConfirmDialog(null, "¿Realmente desea eliminar este registro?", "Confirmación", JOptionPane.YES_OPTION);
                if (confirmado == JOptionPane.YES_OPTION) {
                    codigo = (String) tbPrincipal.getModel().getValueAt(filasel, 0);
                    try {
                        Connection con;
                        con = Conexion.ConectarBasedeDatos();
                        String sentence;
                        sentence = "CALL SP_UsuarioEliminar(" + codigo + ")";
                        PreparedStatement pst;
                        pst = con.prepareStatement(sentence);
                        pst.executeUpdate();
                        JOptionPane.showMessageDialog(null, "Registro eliminado correctamente", "Información", JOptionPane.INFORMATION_MESSAGE);

                        con.close();
                        pst.close();
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(null, ex);
                        JOptionPane.showMessageDialog(null, "Error al intentar eliminar el registro", "Error", JOptionPane.INFORMATION_MESSAGE);
                    }
                } else {
                    ModoEdicion(false);
                    Limpiar();
                    JOptionPane.showMessageDialog(null, "Cancelado correctamente", "Información", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (HeadlessException e) {
            System.out.println("Error al intentar eliminar registro" + e);
        }
    }

    private void TablaConsultaUsuarios() {//Realiza la consulta de los productos que tenemos en la base de datos
        String sentencia = "CALL SP_UsuarioConsulta";
        String titlesJtabla[] = {"Código", "Nombre", "Apellido", "Alias", "Contraseña", "Fecha de creación"}; //Debe tener la misma cantidad que titlesconsulta
        tbPrincipal.setModel(con.ConsultaTableBD(sentencia, titlesJtabla, cbCampoBuscar));
        metodos.AnchuraColumna(tbPrincipal);

        if (tbPrincipal.getModel().getRowCount() == 1) {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registro encontrado");
        } else {
            lbCantRegistros.setText(tbPrincipal.getModel().getRowCount() + " Registros encontrados");
        }
    }

    private void TablaAllPerfiles() {
        try {
            String sentencia = "SELECT per_codigo, per_denominacion, per_descripcion FROM perfil ORDER BY per_denominacion";
            con = con.ObtenerRSSentencia(sentencia);
            tablemodelPerfiles = (DefaultTableModel) tbPerfiles.getModel();
            tablemodelPerfiles.setRowCount(0);
            while (con.rs.next()) {
                tablemodelPerfiles.addRow(new Object[]{con.rs.getString("per_codigo"), con.rs.getString("per_denominacion"),
                    false, con.rs.getString("per_descripcion")});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    public void TablaPerfilesDelUsu() {
        //Poner todos en false
        for (int i = 0; i < tbPerfiles.getRowCount(); i++) {
            tbPerfiles.setValueAt(false, i, 2);
        }

        try {
            String codususelect = tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0) + "";
            String sentencia = "CALL SP_UsuarioPerfilConsulta('" + codususelect + "')";
            con = con.ObtenerRSSentencia(sentencia);
            String perfil;
            while (con.rs.next()) {
                for (int i = 0; i < tbPerfiles.getRowCount(); i++) {
                    perfil = tbPerfiles.getValueAt(i, 1) + "";
                    if (perfil.equals(con.rs.getString("per_denominacion"))) {
                        tbPerfiles.setValueAt(true, i, 2);

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    public void TablaRolesDelUsu() {
        try {
            tablemodelRoles = (DefaultTableModel) tbRoles.getModel();
            tablemodelRoles.setRowCount(0);
            //Se obtienen los modulos de los perfiles del usuario seleccionado
            String codususelect = tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0) + "";
            String sentencia = "CALL SP_UsuarioPerfilModulosConsulta('" + codususelect + "')";
            con = con.ObtenerRSSentencia(sentencia);
            while (con.rs.next()) {
                tablemodelRoles.addRow(new Object[]{
                    con.rs.getString("mo_denominacion"),});
            }

            //Se obtienen los estados de los roles
            String modulo;
            sentencia = "CALL SP_UsuarioRolesPorUsuario('" + codususelect + "')";
            con = con.ObtenerRSSentencia(sentencia);
            while (con.rs.next()) {
                for (int i = 0; i < tbRoles.getRowCount(); i++) {
                    modulo = tbRoles.getValueAt(i, 0) + "";
                    if (con.rs.getString("modulo").equals(modulo)) {
                        tbRoles.setValueAt(con.rs.getBoolean("estadoalta"), i, 1);
                        tbRoles.setValueAt(con.rs.getBoolean("estadomodificar"), i, 2);
                        tbRoles.setValueAt(con.rs.getBoolean("estadobaja"), i, 3);
                        tbRoles.setValueAt(con.rs.getInt("codalta"), i, 4);
                        tbRoles.setValueAt(con.rs.getInt("codmodificar"), i, 5);
                        tbRoles.setValueAt(con.rs.getInt("codbaja"), i, 6);
                    }
                }
            }

            //Se obtienen los codigos de los roles faltantes
            for (int i = 0; i < tbRoles.getRowCount(); i++) {
                if (tbRoles.getValueAt(i, 4) == null) {
                    modulo = tbRoles.getValueAt(i, 0) + "";
                    sentencia = "SELECT rol_codigo, rol_denominacion FROM rol, modulo WHERE mo_denominacion='" + modulo + "' AND rol_modulo=mo_codigo";
                    con = con.ObtenerRSSentencia(sentencia);
                    while (con.rs.next()) {
                        switch (con.rs.getString("rol_denominacion")) {
                            case "ALTA":
                                tbRoles.setValueAt(con.rs.getString("rol_codigo"), i, 4);
                                break;
                            case "MODIFICAR":
                                tbRoles.setValueAt(con.rs.getString("rol_codigo"), i, 5);
                                break;
                            case "BAJA":
                                tbRoles.setValueAt(con.rs.getString("rol_codigo"), i, 6);
                                break;
                            default:
                                JOptionPane.showMessageDialog(this, "No se encontró", "Error", JOptionPane.ERROR_MESSAGE);
                                break;
                        }
                    }
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    private void NuevoModificarPerfilUsuario() {
        //Perfiles de usuario
        try {
            String codusuario = "";
            String codperfil;
            boolean estado;
            String sentencia;

            if (txtCodigo.getText().equals("")) { //Si es nuevo
                //Obtener el id del ultimo usuario registrado
                try {
                    sentencia = "SELECT MAX(usu_codigo) AS id FROM usuario";
                    con = con.ObtenerRSSentencia(sentencia);
                    while (con.rs.next()) {
                        codusuario = con.rs.getString("id");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                con.DesconectarBasedeDatos();
                //Recorer la tabla y registrar
                for (int i = 0; i < tbPerfiles.getRowCount(); i++) {
                    codperfil = tbPerfiles.getValueAt(i, 0) + "";
                    estado = (Boolean) tbPerfiles.getValueAt(i, 2);

                    if (estado == true) {
                        sentencia = "INSERT INTO usuario_perfil VALUES(usuper_codigo,'" + codusuario + "','" + codperfil + "')";
                        con.EjecutarABM(sentencia, false);
                    }
                }
            } else { //Si es modificar
                codusuario = txtCodigo.getText();
                for (int i = 0; i < tbPerfiles.getRowCount(); i++) {
                    //Validar si usuperfil ya esta registrado
                    codperfil = tbPerfiles.getValueAt(i, 0) + "";
                    estado = (Boolean) tbPerfiles.getValueAt(i, 2);
                    sentencia = "SELECT usuper_codigo FROM usuario_perfil "
                            + "WHERE usuper_usuario='" + codusuario + "' AND usuper_perfil='" + codperfil + "'";
                    con = con.ObtenerRSSentencia(sentencia);
                    if (con.rs.next()) { //Si existe
                        if (estado == false) {
                            sentencia = "DELETE FROM usuario_perfil WHERE usuper_usuario='" + codusuario + "' "
                                    + "AND usuper_perfil='" + codperfil + "'";
                            con.EjecutarABM(sentencia, false);

                            //Borrar todos los roles del usuario
                            sentencia = "DELETE FROM usuario_rol WHERE usurol_usuario='" + codusuario + "'";
                            con.EjecutarABM(sentencia, false);
                            Toolkit.getDefaultToolkit().beep();
                            JOptionPane.showMessageDialog(this, "Se modificó los perfiles del usuario, por ende se eliminaron los roles del usuario", "Advertencia", JOptionPane.WARNING_MESSAGE);
                        }
                    } else { //Si no existe
                        if (estado == true) {
                            sentencia = "INSERT INTO usuario_perfil VALUES(usuper_codigo,'" + codusuario + "','" + codperfil + "')";
                            con.EjecutarABM(sentencia, false);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    private void NuevoModificarRolesUsuario() {
        //Roles de usuario
        try {
            boolean estadoAlta;
            String codRolAlta;
            boolean estadoModificar;
            String codRolModificar;
            boolean estadoBaja;
            String codRolBaja;
            String codusuario = "";
            String sentencia;

            if (txtCodigo.getText().equals("")) { //Si es nuevo
                //Obtener el id del ultimo usuario registrado
                try {
                    sentencia = "SELECT MAX(usu_codigo) AS id FROM usuario";
                    con = con.ObtenerRSSentencia(sentencia);
                    while (con.rs.next()) {
                        codusuario = con.rs.getString("id");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                con.DesconectarBasedeDatos();

                for (int i = 0; i < tbRoles.getRowCount(); i++) {
                    //RolAlta
                    estadoAlta = (Boolean) tbRoles.getValueAt(i, 1);
                    codRolAlta = tbRoles.getValueAt(i, 4) + "";
                    if (estadoAlta == true) {
                        sentencia = "INSERT INTO usuario_rol VALUES(usurol_codigo,'" + codusuario + "','" + codRolAlta + "')";
                        con.EjecutarABM(sentencia, false);
                    }
                    //RolModificar
                    estadoModificar = (Boolean) tbRoles.getValueAt(i, 2);
                    codRolModificar = tbRoles.getValueAt(i, 5) + "";
                    if (estadoModificar == true) {
                        sentencia = "INSERT INTO usuario_rol VALUES(usurol_codigo,'" + codusuario + "','" + codRolModificar + "')";
                        con.EjecutarABM(sentencia, false);
                    }
                    //RolBaja
                    estadoBaja = (Boolean) tbRoles.getValueAt(i, 3);
                    codRolBaja = tbRoles.getValueAt(i, 6) + "";
                    if (estadoBaja == true) {
                        sentencia = "INSERT INTO usuario_rol VALUES(usurol_codigo,'" + codusuario + "','" + codRolBaja + "')";
                        con.EjecutarABM(sentencia, false);
                    }
                }
            } else { //Si es modificar
                for (int i = 0; i < tbRoles.getRowCount(); i++) {
                    codusuario = txtCodigo.getText();

                    if (tbRoles.getValueAt(i, 1) != null) {
                        estadoAlta = (Boolean) tbRoles.getValueAt(i, 1);
                    } else {
                        estadoAlta = false;
                    }

                    codRolAlta = tbRoles.getValueAt(i, 4) + "";
                    sentencia = "SELECT usurol_codigo FROM usuario_rol WHERE usurol_usuario='" + codusuario
                            + "' AND usurol_rol='" + codRolAlta + "'";
                    con = con.ObtenerRSSentencia(sentencia);
                    if (con.rs.next()) { //Si existe
                        if (estadoAlta == false) {
                            sentencia = "DELETE FROM usuario_rol WHERE usurol_usuario='" + codusuario + "' "
                                    + "AND usurol_rol='" + codRolAlta + "'";
                            con.EjecutarABM(sentencia, false);
                        }
                    } else { //Si no existe
                        if (estadoAlta == true) {
                            sentencia = "INSERT INTO usuario_rol VALUES(usurol_codigo,'" + codusuario + "','" + codRolAlta + "')";
                            con.EjecutarABM(sentencia, false);
                        }
                    }
                    //Si existe rol modificar
                    if (tbRoles.getValueAt(i, 2) != null) {
                        estadoModificar = (Boolean) tbRoles.getValueAt(i, 2);
                    } else {
                        estadoModificar = false;
                    }
                    codRolModificar = tbRoles.getValueAt(i, 5) + "";
                    sentencia = "SELECT usurol_codigo FROM usuario_rol WHERE usurol_usuario='" + codusuario
                            + "' AND usurol_rol='" + codRolModificar + "'";
                    con = con.ObtenerRSSentencia(sentencia);
                    if (con.rs.next()) { //Si existe
                        if (estadoModificar == false) {
                            sentencia = "DELETE FROM usuario_rol WHERE usurol_usuario='" + codusuario + "' "
                                    + "AND usurol_rol='" + codRolModificar + "'";
                            con.EjecutarABM(sentencia, false);
                        }
                    } else { //Si no existe
                        if (estadoModificar == true) {
                            sentencia = "INSERT INTO usuario_rol VALUES(usurol_codigo,'" + codusuario + "','" + codRolModificar + "')";
                            con.EjecutarABM(sentencia, false);
                        }
                    }
                    //Si existe rol baja
                    if (tbRoles.getValueAt(i, 3) != null) {
                        estadoBaja = (Boolean) tbRoles.getValueAt(i, 3);
                    } else {
                        estadoBaja = false;
                    }
                    codRolBaja = tbRoles.getValueAt(i, 6) + "";
                    sentencia = "SELECT usurol_codigo FROM usuario_rol WHERE usurol_usuario='" + codusuario
                            + "' AND usurol_rol='" + codRolBaja + "'";
                    con = con.ObtenerRSSentencia(sentencia);
                    if (con.rs.next()) { //Si existe
                        if (estadoBaja == false) {
                            sentencia = "DELETE FROM usuario_rol WHERE usurol_usuario='" + codusuario + "' "
                                    + "AND usurol_rol='" + codRolBaja + "'";
                            con.EjecutarABM(sentencia, false);
                        }
                    } else { //Si no existe
                        if (estadoBaja == true) {
                            sentencia = "INSERT INTO usuario_rol VALUES(usurol_codigo,'" + codusuario + "','" + codRolBaja + "')";
                            con.EjecutarABM(sentencia, false);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        con.DesconectarBasedeDatos();
    }

    private void ModoVistaPrevia() {
        txtCodigo.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 0).toString());
        txtNombre.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 1).toString());
        txtApellido.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 2).toString());
        txtAlias.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 3).toString());
        txtPass.setText(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 4).toString());

        SimpleDateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
        Date fechacreacion = null;
        try {
            fechacreacion = formato.parse(tbPrincipal.getValueAt(tbPrincipal.getSelectedRow(), 5).toString());

        } catch (ParseException ex) {
            Logger.getLogger(ABMUsuario.class
                    .getName()).log(Level.SEVERE, null, ex);
        }
        dcFechaCreacion.setDate(fechacreacion);

        TablaPerfilesDelUsu();
        TablaRolesDelUsu();
    }

    private void ModoEdicion(boolean valor) {
        txtBuscar.setEnabled(!valor);
        tbPrincipal.setEnabled(!valor);
        txtNombre.setEnabled(valor);
        txtApellido.setEnabled(valor);
        txtAlias.setEnabled(valor);
        txtPass.setEnabled(valor);
        dcFechaCreacion.setEnabled(valor);
        btnNuevo.setEnabled(!valor);
        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
        btnGuardar.setEnabled(valor);
        btnCancelar.setEnabled(valor);

        tbPerfiles.setEnabled(valor);
        tbRoles.setEnabled(valor);

        txtNombre.requestFocus();
    }

    private void Limpiar() {
        txtCodigo.setText("");
        txtNombre.setText("");
        txtApellido.setText("");
        txtAlias.setText("");
        txtPass.setText("");

        dcFechaCreacion.setDate(new Date());

        lblNombre.setForeground(new Color(102, 102, 102));
        lblApellido.setForeground(new Color(102, 102, 102));
        lblAlias.setForeground(new Color(102, 102, 102));
        lblPass.setForeground(new Color(102, 102, 102));

        txtBuscar.requestFocus();
        tbPrincipal.clearSelection();

        TablaAllPerfiles(); //Recargar tabla
        //Vaciar tabla
        tablemodelPerfilModulos = (DefaultTableModel) tbPerfilModulos.getModel();
        tablemodelPerfilModulos.setRowCount(0);
        tablemodelRoles = (DefaultTableModel) tbRoles.getModel();
        tablemodelRoles.setRowCount(0);
    }

    public boolean ComprobarCampos() {
        if (metodostxt.ValidarCampoVacioTXT(txtNombre, lblNombre) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtApellido, lblApellido) == false) {
            return false;
        }
        if (metodostxt.ValidarCampoVacioTXT(txtAlias, lblAlias) == false) {
            return false;
        }

        if (metodostxt.ValidarCampoVacioTXT(txtPass, lblPass) == false) {
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
        jpPerfiles = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tbPerfiles = new javax.swing.JTable();
        jScrollPane3 = new javax.swing.JScrollPane();
        tbPerfilModulos = new javax.swing.JTable();
        lblTituloPerfilModulos = new javax.swing.JLabel();
        jpRoles = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tbRoles = new javax.swing.JTable();
        jpBotones2 = new javax.swing.JPanel();
        btnGuardar = new javax.swing.JButton();
        btnCancelar = new javax.swing.JButton();
        panel2 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        panel1 = new org.edisoncor.gui.panel.Panel();
        lblCodigo = new javax.swing.JLabel();
        txtCodigo = new javax.swing.JTextField();
        lblNombre = new javax.swing.JLabel();
        txtNombre = new javax.swing.JTextField();
        lblApellido = new javax.swing.JLabel();
        txtApellido = new javax.swing.JTextField();
        lblFechaCreacion = new javax.swing.JLabel();
        dcFechaCreacion = new com.toedter.calendar.JDateChooser();
        lblAlias = new javax.swing.JLabel();
        lblPass = new javax.swing.JLabel();
        txtAlias = new javax.swing.JTextField();
        txtPass = new javax.swing.JTextField();

        setTitle("Ventana Usuarios");
        setBackground(new java.awt.Color(45, 62, 80));
        setResizable(false);

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        jpTabla.setBackground(new java.awt.Color(233, 255, 255));
        jpTabla.setBorder(javax.swing.BorderFactory.createEtchedBorder());

        jLabel10.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel10.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/iconos40x40/IconoBuscar.png"))); // NOI18N
        jLabel10.setText("  BUSCAR ");

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
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                tbPrincipalMouseReleased(evt);
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
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpTablaLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jpTablaLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 423, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 676, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jpTablaLayout.createSequentialGroup()
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(txtBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 253, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(37, 37, 37)
                        .addComponent(lblBuscarCampo)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cbCampoBuscar, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(42, 42, 42))
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
                .addGap(1, 1, 1)
                .addComponent(scPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                .addGap(2, 2, 2)
                .addComponent(lbCantRegistros, javax.swing.GroupLayout.PREFERRED_SIZE, 19, javax.swing.GroupLayout.PREFERRED_SIZE)
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
                .addContainerGap(10, Short.MAX_VALUE)
                .addGroup(jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnModificar, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        jpBotonesLayout.setVerticalGroup(
            jpBotonesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpBotonesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNuevo, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnModificar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnEliminar, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jtpEdicion.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        jtpEdicion.setName(""); // NOI18N

        jpPerfiles.setBackground(new java.awt.Color(233, 255, 255));
        jpPerfiles.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        tbPerfiles.setFont(new java.awt.Font("sansserif", 0, 13)); // NOI18N
        tbPerfiles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Perfil", "Estado", "Descripción"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Object.class, java.lang.Boolean.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbPerfiles.setEnabled(false);
        tbPerfiles.setRowHeight(25);
        tbPerfiles.setShowHorizontalLines(true);
        tbPerfiles.getTableHeader().setReorderingAllowed(false);
        tbPerfiles.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                tbPerfilesMousePressed(evt);
            }
        });
        jScrollPane2.setViewportView(tbPerfiles);

        tbPerfilModulos.setFont(new java.awt.Font("sansserif", 0, 13)); // NOI18N
        tbPerfilModulos.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Código", "Módulo"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbPerfilModulos.setEnabled(false);
        tbPerfilModulos.setRowHeight(20);
        tbPerfilModulos.setRowSelectionAllowed(false);
        tbPerfilModulos.getTableHeader().setReorderingAllowed(false);
        jScrollPane3.setViewportView(tbPerfilModulos);
        if (tbPerfilModulos.getColumnModel().getColumnCount() > 0) {
            tbPerfilModulos.getColumnModel().getColumn(0).setResizable(false);
            tbPerfilModulos.getColumnModel().getColumn(0).setPreferredWidth(20);
            tbPerfilModulos.getColumnModel().getColumn(1).setResizable(false);
            tbPerfilModulos.getColumnModel().getColumn(1).setPreferredWidth(50);
        }

        lblTituloPerfilModulos.setFont(new java.awt.Font("sansserif", 1, 12)); // NOI18N
        lblTituloPerfilModulos.setText("Módulos del perfil:");

        javax.swing.GroupLayout jpPerfilesLayout = new javax.swing.GroupLayout(jpPerfiles);
        jpPerfiles.setLayout(jpPerfilesLayout);
        jpPerfilesLayout.setHorizontalGroup(
            jpPerfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPerfilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 588, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 9, Short.MAX_VALUE)
                .addGroup(jpPerfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblTituloPerfilModulos, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
                .addContainerGap())
        );
        jpPerfilesLayout.setVerticalGroup(
            jpPerfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPerfilesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblTituloPerfilModulos)
                .addGap(4, 4, 4)
                .addGroup(jpPerfilesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 141, Short.MAX_VALUE)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtpEdicion.addTab("Perfiles", jpPerfiles);

        jpRoles.setBackground(new java.awt.Color(233, 255, 255));
        jpRoles.setBorder(javax.swing.BorderFactory.createEtchedBorder(javax.swing.border.EtchedBorder.RAISED));

        tbRoles.setFont(new java.awt.Font("sansserif", 0, 13)); // NOI18N
        tbRoles.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Módulo", "Alta", "Modificar", "Baja", "AltaCodigo", "ModificarCodigo", "BajaCodigo"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Object.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Boolean.class, java.lang.Object.class, java.lang.Object.class, java.lang.Object.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, false, false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tbRoles.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_NEXT_COLUMN);
        tbRoles.setEnabled(false);
        tbRoles.setRowHeight(25);
        tbRoles.setRowSelectionAllowed(false);
        tbRoles.setShowHorizontalLines(true);
        tbRoles.getTableHeader().setResizingAllowed(false);
        tbRoles.getTableHeader().setReorderingAllowed(false);
        jScrollPane1.setViewportView(tbRoles);
        if (tbRoles.getColumnModel().getColumnCount() > 0) {
            tbRoles.getColumnModel().getColumn(0).setResizable(false);
            tbRoles.getColumnModel().getColumn(0).setPreferredWidth(50);
            tbRoles.getColumnModel().getColumn(1).setResizable(false);
            tbRoles.getColumnModel().getColumn(1).setPreferredWidth(10);
            tbRoles.getColumnModel().getColumn(2).setResizable(false);
            tbRoles.getColumnModel().getColumn(2).setPreferredWidth(10);
            tbRoles.getColumnModel().getColumn(3).setResizable(false);
            tbRoles.getColumnModel().getColumn(3).setPreferredWidth(10);
            tbRoles.getColumnModel().getColumn(4).setResizable(false);
            tbRoles.getColumnModel().getColumn(4).setPreferredWidth(0);
            tbRoles.getColumnModel().getColumn(5).setResizable(false);
            tbRoles.getColumnModel().getColumn(5).setPreferredWidth(0);
            tbRoles.getColumnModel().getColumn(6).setResizable(false);
            tbRoles.getColumnModel().getColumn(6).setPreferredWidth(0);
        }

        javax.swing.GroupLayout jpRolesLayout = new javax.swing.GroupLayout(jpRoles);
        jpRoles.setLayout(jpRolesLayout);
        jpRolesLayout.setHorizontalGroup(
            jpRolesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpRolesLayout.createSequentialGroup()
                .addGap(112, 112, 112)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 617, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(118, Short.MAX_VALUE))
        );
        jpRolesLayout.setVerticalGroup(
            jpRolesLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpRolesLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 158, Short.MAX_VALUE)
                .addContainerGap())
        );

        jtpEdicion.addTab("Roles", jpRoles);

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
        labelMetric2.setText("USUARIOS");
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

        panel1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEtchedBorder(), "Datos del usuario"));
        panel1.setColorPrimario(new java.awt.Color(233, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(255, 255, 255));

        lblCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblCodigo.setForeground(new java.awt.Color(102, 102, 102));
        lblCodigo.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblCodigo.setText("Código");

        txtCodigo.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtCodigo.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtCodigo.setEnabled(false);

        lblNombre.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblNombre.setForeground(new java.awt.Color(102, 102, 102));
        lblNombre.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblNombre.setText("Nombre*");

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
        lblApellido.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblApellido.setText("Apellido*");

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

        lblFechaCreacion.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblFechaCreacion.setForeground(new java.awt.Color(102, 102, 102));
        lblFechaCreacion.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblFechaCreacion.setText("Fecha de creación*");

        dcFechaCreacion.setEnabled(false);

        lblAlias.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblAlias.setForeground(new java.awt.Color(102, 102, 102));
        lblAlias.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblAlias.setText("Alias*");
        lblAlias.setToolTipText("");

        lblPass.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        lblPass.setForeground(new java.awt.Color(102, 102, 102));
        lblPass.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPass.setText("Contraseña*");

        txtAlias.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtAlias.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtAlias.setEnabled(false);

        txtPass.setFont(new java.awt.Font("SansSerif", 0, 12)); // NOI18N
        txtPass.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        txtPass.setEnabled(false);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblCodigo)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblNombre)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblApellido)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, 153, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(40, 40, 40)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblAlias)
                    .addComponent(txtAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(lblPass, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPass, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 112, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblFechaCreacion)
                    .addComponent(dcFechaCreacion, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lblCodigo)
                    .addComponent(lblNombre)
                    .addComponent(lblApellido)
                    .addComponent(lblAlias)
                    .addComponent(lblPass)
                    .addComponent(lblFechaCreacion))
                .addGap(2, 2, 2)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtCodigo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtApellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAlias, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtPass, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcFechaCreacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jpPrincipalLayout = new javax.swing.GroupLayout(jpPrincipal);
        jpPrincipal.setLayout(jpPrincipalLayout);
        jpPrincipalLayout.setHorizontalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addComponent(jpTabla, javax.swing.GroupLayout.PREFERRED_SIZE, 691, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jpBotones, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 851, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(270, 270, 270))
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jpBotones, javax.swing.GroupLayout.DEFAULT_SIZE, 196, Short.MAX_VALUE)
                    .addComponent(jpTabla, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpEdicion, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jpBotones2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jtpEdicion.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 866, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.PREFERRED_SIZE, 625, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        getAccessibleContext().setAccessibleName("Inventario");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

//--------------------------Eventos de componentes----------------------------//
    private void txtBuscarKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtBuscarKeyReleased
        metodos.FiltroJTable(txtBuscar.getText(), cbCampoBuscar.getSelectedIndex(), tbPrincipal);

        btnModificar.setEnabled(false);
        btnEliminar.setEnabled(false);
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
        ModoEdicion(false);
        Limpiar();
        TablaConsultaUsuarios();
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

    private void txtApellidoKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidoKeyTyped
        metodostxt.SoloTextoKeyTyped(evt);

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtApellido, 30);
    }//GEN-LAST:event_txtApellidoKeyTyped

    private void txtApellidoKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtApellidoKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtApellido, lblApellido);
        metodostxt.TxtMayusKeyReleased(txtApellido, evt);
    }//GEN-LAST:event_txtApellidoKeyReleased

    private void txtNombreKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyTyped
        metodostxt.SoloTextoKeyTyped(evt);

        //Cantidad de caracteres
        metodostxt.TxtCantidadCaracteresKeyTyped(txtNombre, 30);
    }//GEN-LAST:event_txtNombreKeyTyped

    private void txtNombreKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtNombreKeyReleased
        metodostxt.TxtColorLabelKeyReleased(txtNombre, lblNombre);
        metodostxt.TxtMayusKeyReleased(txtNombre, evt);
    }//GEN-LAST:event_txtNombreKeyReleased

    private void tbPrincipalKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tbPrincipalKeyReleased
        if (evt.getKeyCode() == KeyEvent.VK_UP || evt.getKeyCode() == KeyEvent.VK_DOWN) {
            ModoVistaPrevia();
        }
    }//GEN-LAST:event_tbPrincipalKeyReleased

    private void tbPrincipalMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPrincipalMouseReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_tbPrincipalMouseReleased

    private void tbPerfilesMousePressed(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tbPerfilesMousePressed
        if (tbPerfiles.isEnabled()) {
            tablemodelPerfilModulos = (DefaultTableModel) tbPerfilModulos.getModel();
            tablemodelPerfilModulos.setRowCount(0);
            String codperfil = tbPerfiles.getValueAt(tbPerfiles.getSelectedRow(), 0) + "";
            lblTituloPerfilModulos.setText("Módulos del perfil: " + tbPerfiles.getValueAt(tbPerfiles.getSelectedRow(), 1));
            String sentencia = "SELECT mo_codigo, mo_denominacion FROM perfil_modulo, modulo WHERE permo_perfil = '" + codperfil
                    + "' AND permo_modulo=mo_codigo ORDER BY mo_denominacion";
            con = con.ObtenerRSSentencia(sentencia);
            try {
                while (con.rs.next()) {
                    tablemodelPerfilModulos.addRow(new Object[]{con.rs.getString("mo_codigo"), con.rs.getString("mo_denominacion")});
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            con.DesconectarBasedeDatos();
        }
    }//GEN-LAST:event_tbPerfilesMousePressed

    List<Component> ordenTabulador;

    private void OrdenTabulador() {
        ordenTabulador = new ArrayList<>();

        ordenTabulador.add(txtNombre);
        ordenTabulador.add(txtApellido);
        ordenTabulador.add(txtAlias);
        ordenTabulador.add(txtPass);
        ordenTabulador.add(dcFechaCreacion);
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
    private com.toedter.calendar.JDateChooser dcFechaCreacion;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JPanel jpBotones;
    private javax.swing.JPanel jpBotones2;
    private javax.swing.JPanel jpPerfiles;
    private javax.swing.JPanel jpPrincipal;
    private javax.swing.JPanel jpRoles;
    private javax.swing.JPanel jpTabla;
    private javax.swing.JTabbedPane jtpEdicion;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lbCantRegistros;
    private javax.swing.JLabel lblAlias;
    private javax.swing.JLabel lblApellido;
    private javax.swing.JLabel lblBuscarCampo;
    private javax.swing.JLabel lblCodigo;
    private javax.swing.JLabel lblFechaCreacion;
    private javax.swing.JLabel lblNombre;
    private javax.swing.JLabel lblPass;
    private javax.swing.JLabel lblTituloPerfilModulos;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.Panel panel2;
    private javax.swing.JScrollPane scPrincipal;
    private javax.swing.JTable tbPerfilModulos;
    private javax.swing.JTable tbPerfiles;
    private javax.swing.JTable tbPrincipal;
    private javax.swing.JTable tbRoles;
    private javax.swing.JTextField txtAlias;
    private javax.swing.JTextField txtApellido;
    private javax.swing.JTextField txtBuscar;
    private javax.swing.JTextField txtCodigo;
    private javax.swing.JTextField txtNombre;
    private javax.swing.JTextField txtPass;
    // End of variables declaration//GEN-END:variables
}
