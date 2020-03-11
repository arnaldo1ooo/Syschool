package principal;

import conexion.Conexion;
import forms.ABMAlumno;
import forms.ABMApoderado;
import forms.ABMFuncionario;
import forms.ABMModulo;
import forms.ABMNivel;
import forms.ABMUsuario;
import forms.Configuracion;
import forms.Gasto;
import forms.Matricula;
import forms.Pago;
import forms.RegistrarGasto;
import forms.RegistrarMatricula;
import forms.RegistrarPago;
import forms.RegistrarPagoSalario;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import utilidades.Metodos;
import utilidades.MetodosTXT;
//Variables globales
import static login.Login.Alias;
//

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class Principal extends javax.swing.JFrame implements Runnable {

    Conexion con = new Conexion();
    Metodos metodos = new Metodos();
    MetodosTXT metodostxt = new MetodosTXT();
    Thread hilo;

    public Principal() {
        initComponents();
        this.setExtendedState(Principal.MAXIMIZED_BOTH);//Maximizar ventana
        ObtenerHorayFecha();
        lbAlias.setText(Alias);
        PerfilesUsuario(Alias);
        PermisoModulos(Alias);

        //Roles de usuario 
        meitRegistrarMatricula.setEnabled(metodos.PermisoRol(Alias, "MATRICULA", "ALTA")); //Si el usuario Alias tiene rol de NUEVO COMPRA
        meitAnularMatricula.setEnabled(metodos.PermisoRol(Alias, "MATRICULA", "BAJA"));
        meitRegistrarPago.setEnabled(metodos.PermisoRol(Alias, "PAGO", "ALTA"));
        meitAnularPago.setEnabled(metodos.PermisoRol(Alias, "PAGO", "BAJA"));
        meitRegistrarPagoSalario.setEnabled(metodos.PermisoRol(Alias, "PAGO_SALARIO", "ALTA"));
        meitAnularPagoSalario.setEnabled(metodos.PermisoRol(Alias, "PAGO_SALARIO", "BAJA"));
        meitRegistrarGasto.setEnabled(metodos.PermisoRol(Alias, "GASTO", "ALTA"));
        meitAnularGasto.setEnabled(metodos.PermisoRol(Alias, "GASTO", "BAJA"));
        //Redimensionar iconos menu
        meMatricula.setIcon(metodos.AjustarIconoAButton(meMatricula.getIcon(), meMatricula.getHeight()));
        mePago.setIcon(metodos.AjustarIconoAButton(mePago.getIcon(), mePago.getHeight()));
        mePagoSalario.setIcon(metodos.AjustarIconoAButton(mePagoSalario.getIcon(), mePagoSalario.getHeight()));
        meGasto.setIcon(metodos.AjustarIconoAButton(meGasto.getIcon(), meGasto.getHeight()));
        meUsuario.setIcon(metodos.AjustarIconoAButton(meUsuario.getIcon(), meUsuario.getHeight()));
        meReporte.setIcon(metodos.AjustarIconoAButton(meReporte.getIcon(), meReporte.getHeight()));
        meConfiguracion.setIcon(metodos.AjustarIconoAButton(meConfiguracion.getIcon(), meConfiguracion.getHeight()));
        meSalir.setIcon(metodos.AjustarIconoAButton(meSalir.getIcon(), meSalir.getHeight()));
    }

    private void PermisoModulos(String ElAlias) {
        con = con.ObtenerRSSentencia("CALL SP_UsuarioModuloConsulta('" + ElAlias + "')");
        String modulo;
        try {
            while (con.rs.next()) {
                modulo = con.rs.getString("mo_denominacion");
                switch (modulo) {
                    case "APODERADO":
                        btnApoderado.setEnabled(true);
                        break;
                    case "ALUMNO":
                        btnAlumno.setEnabled(true);
                        break;
                    case "MATRICULA":
                        btnMatricula.setEnabled(true);
                        meMatricula.setEnabled(true);
                        break;
                    case "PAGO":
                        btnPago.setEnabled(true);
                        mePago.setEnabled(true);
                        break;
                    case "PAGO_SALARIO":
                        System.out.println("sas");
                        mePagoSalario.setEnabled(true);
                        break;
                    case "GASTO":
                        btnGasto.setEnabled(true);
                        meGasto.setEnabled(true);
                        break;
                    case "CLIENTE":
                        btnPago.setEnabled(true);
                        break;
                    case "FUNCIONARIO":
                        btnFuncionario.setEnabled(true);
                        break;
                    case "USUARIO":
                        btnUsuario.setEnabled(true);
                        meUsuario.setEnabled(true);
                        break;
                    case "MODULO":
                        meitModulo.setEnabled(true);
                        break;
                    case "REPORTE":
                        meReporte.setEnabled(true);
                        break;
                    case "CONFIGURACION":
                        meConfiguracion.setEnabled(true);
                        break;
                    default:
                        JOptionPane.showMessageDialog(this, "No se encontró " + modulo, "Error", JOptionPane.ERROR_MESSAGE);
                        break;
                }
            }
            con.DesconectarBasedeDatos();
        } catch (SQLException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void PerfilesUsuario(String alias) {
        String consulta = "SELECT per_codigo, per_denominacion "
                + "FROM usuario,perfil,usuario_perfil "
                + "WHERE usu_alias = '" + alias + "' AND usuper_usuario = usu_codigo AND usuper_perfil = per_codigo "
                + "ORDER BY per_denominacion";
        con = con.ObtenerRSSentencia(consulta);
        try {
            String perfil = "";
            int numfila = 1;
            while (con.rs.next()) {
                if (numfila == 1) {
                    perfil = con.rs.getString("per_denominacion");
                } else {
                    perfil = perfil + ", " + con.rs.getString("per_denominacion");
                }
                numfila = numfila + 1;
            }
            lblPerfil.setText(perfil);
        } catch (SQLException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
        con.DesconectarBasedeDatos();
    }

    private void ObtenerHorayFecha() {
        //Obtener fecha y hora
        hilo = new Thread(this);
        hilo.start();
    }

    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        piPrincipal = new org.edisoncor.gui.panel.PanelImage();
        btnPago = new javax.swing.JButton();
        btnFuncionario = new javax.swing.JButton();
        btnAlumno = new javax.swing.JButton();
        btnApoderado = new javax.swing.JButton();
        btnUsuario = new javax.swing.JButton();
        panel1 = new org.edisoncor.gui.panel.Panel();
        jLabel1 = new javax.swing.JLabel();
        lbAlias = new javax.swing.JLabel();
        lblPerfil = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        lbFechaTitulo = new javax.swing.JLabel();
        lbFecha = new javax.swing.JLabel();
        lbHoraTitulo = new javax.swing.JLabel();
        lbHora = new javax.swing.JLabel();
        btnGasto = new javax.swing.JButton();
        btnMatricula = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        meMatricula = new javax.swing.JMenu();
        meitRegistrarMatricula = new javax.swing.JMenuItem();
        jSeparator21 = new javax.swing.JPopupMenu.Separator();
        meitAnularMatricula = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JPopupMenu.Separator();
        meitNivel = new javax.swing.JMenuItem();
        mePago = new javax.swing.JMenu();
        meitRegistrarPago = new javax.swing.JMenuItem();
        jSeparator17 = new javax.swing.JPopupMenu.Separator();
        meitAnularPago = new javax.swing.JMenuItem();
        mePagoSalario = new javax.swing.JMenu();
        meitRegistrarPagoSalario = new javax.swing.JMenuItem();
        jSeparator23 = new javax.swing.JPopupMenu.Separator();
        meitAnularPagoSalario = new javax.swing.JMenuItem();
        meGasto = new javax.swing.JMenu();
        meitRegistrarGasto = new javax.swing.JMenuItem();
        jSeparator20 = new javax.swing.JPopupMenu.Separator();
        meitAnularGasto = new javax.swing.JMenuItem();
        meReporte = new javax.swing.JMenu();
        jMenuItem7 = new javax.swing.JMenuItem();
        jSeparator19 = new javax.swing.JPopupMenu.Separator();
        meUsuario = new javax.swing.JMenu();
        jMenuItem9 = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        meitModulo = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        jMenuItem17 = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JPopupMenu.Separator();
        jMenuItem18 = new javax.swing.JMenuItem();
        meConfiguracion = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        meSalir = new javax.swing.JMenu();
        jMenuItem19 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Menu Principal");
        setName("Fm_Principal"); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowOpened(java.awt.event.WindowEvent evt) {
                formWindowOpened(evt);
            }
        });

        piPrincipal.setFocusable(false);
        piPrincipal.setIcon(new javax.swing.ImageIcon(getClass().getResource("/principal/iconos/fondo1.png"))); // NOI18N
        piPrincipal.setPreferredSize(new java.awt.Dimension(2000, 655));

        btnPago.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoPagos70.png"))); // NOI18N
        btnPago.setText("PAGOS");
        btnPago.setEnabled(false);
        btnPago.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagoActionPerformed(evt);
            }
        });

        btnFuncionario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnFuncionario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoFuncionario70.png"))); // NOI18N
        btnFuncionario.setText("FUNCIONARIOS");
        btnFuncionario.setEnabled(false);
        btnFuncionario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnFuncionario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnFuncionarioActionPerformed(evt);
            }
        });

        btnAlumno.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnAlumno.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoAlumnos70.png"))); // NOI18N
        btnAlumno.setText("ALUMNOS");
        btnAlumno.setEnabled(false);
        btnAlumno.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnAlumno.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnAlumnoActionPerformed(evt);
            }
        });

        btnApoderado.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnApoderado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoEncargado70.png"))); // NOI18N
        btnApoderado.setText("APODERADOS");
        btnApoderado.setEnabled(false);
        btnApoderado.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnApoderado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnApoderadoActionPerformed(evt);
            }
        });

        btnUsuario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoUsuario70.png"))); // NOI18N
        btnUsuario.setText("USUARIOS");
        btnUsuario.setEnabled(false);
        btnUsuario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnUsuario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUsuarioActionPerformed(evt);
            }
        });

        panel1.setColorPrimario(new java.awt.Color(255, 255, 255));
        panel1.setColorSecundario(new java.awt.Color(154, 255, 255));
        panel1.setGradiente(org.edisoncor.gui.panel.Panel.Gradiente.VERTICAL);

        jLabel1.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos20x20/IconoUsuario.png"))); // NOI18N
        jLabel1.setText("Usuario:");

        lbAlias.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbAlias.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbAlias.setText("Error de usuario");

        lblPerfil.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lblPerfil.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblPerfil.setText("Error de perfil");

        jLabel2.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel2.setText("Perfil:");

        lbFechaTitulo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbFechaTitulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbFechaTitulo.setText("Fecha de hoy:");
        lbFechaTitulo.setFocusable(false);
        lbFechaTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lbFecha.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbFecha.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbFecha.setText("00/00/0000");
        lbFecha.setFocusable(false);
        lbFecha.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lbHoraTitulo.setFont(new java.awt.Font("Tahoma", 1, 16)); // NOI18N
        lbHoraTitulo.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        lbHoraTitulo.setText("Hora actual:");
        lbHoraTitulo.setFocusable(false);
        lbHoraTitulo.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        lbHora.setFont(new java.awt.Font("Tahoma", 0, 16)); // NOI18N
        lbHora.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lbHora.setText("00:00:00");
        lbHora.setFocusable(false);
        lbHora.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 134, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblPerfil, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE)
                .addGap(508, 508, 508)
                .addComponent(lbFechaTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbFecha)
                .addGap(18, 18, 18)
                .addComponent(lbHoraTitulo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lbHora, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(lbAlias, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPerfil, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFechaTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbFecha, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbHoraTitulo, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lbHora, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        lbFechaTitulo.getAccessibleContext().setAccessibleName("");

        btnGasto.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoGastos70.png"))); // NOI18N
        btnGasto.setText("GASTOS");
        btnGasto.setEnabled(false);
        btnGasto.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGastoActionPerformed(evt);
            }
        });

        btnMatricula.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnMatricula.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoMatricula70.png"))); // NOI18N
        btnMatricula.setText("MATRICULAS");
        btnMatricula.setEnabled(false);
        btnMatricula.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnMatricula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnMatriculaActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout piPrincipalLayout = new javax.swing.GroupLayout(piPrincipal);
        piPrincipal.setLayout(piPrincipalLayout);
        piPrincipalLayout.setHorizontalGroup(
            piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(piPrincipalLayout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addGroup(piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFuncionario)
                    .addComponent(btnGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnPago, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 208, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, Short.MAX_VALUE))
        );
        piPrincipalLayout.setVerticalGroup(
            piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(piPrincipalLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btnApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnPago, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );

        jMenuBar1.setMinimumSize(new java.awt.Dimension(120, 70));
        jMenuBar1.setPreferredSize(new java.awt.Dimension(120, 55));

        meMatricula.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoMatricula70.png"))); // NOI18N
        meMatricula.setText("MATRICULA");
        meMatricula.setEnabled(false);
        meMatricula.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meMatricula.setPreferredSize(new java.awt.Dimension(170, 70));
        meMatricula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meMatriculaActionPerformed(evt);
            }
        });

        meitRegistrarMatricula.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoRegistrar.png"))); // NOI18N
        meitRegistrarMatricula.setText("Registrar matricula");
        meitRegistrarMatricula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitRegistrarMatriculaActionPerformed(evt);
            }
        });
        meMatricula.add(meitRegistrarMatricula);
        meMatricula.add(jSeparator21);

        meitAnularMatricula.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoEliminar25.png"))); // NOI18N
        meitAnularMatricula.setText("Anular matricula");
        meitAnularMatricula.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitAnularMatriculaActionPerformed(evt);
            }
        });
        meMatricula.add(meitAnularMatricula);
        meMatricula.add(jSeparator22);

        meitNivel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoBotonAzul.png"))); // NOI18N
        meitNivel.setText("Niveles");
        meitNivel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitNivelActionPerformed(evt);
            }
        });
        meMatricula.add(meitNivel);

        jMenuBar1.add(meMatricula);

        mePago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoPagos70.png"))); // NOI18N
        mePago.setText("PAGOS");
        mePago.setEnabled(false);
        mePago.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        mePago.setPreferredSize(new java.awt.Dimension(140, 70));
        mePago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mePagoActionPerformed(evt);
            }
        });

        meitRegistrarPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoRegistrar.png"))); // NOI18N
        meitRegistrarPago.setText("Registrar pago");
        meitRegistrarPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitRegistrarPagoActionPerformed(evt);
            }
        });
        mePago.add(meitRegistrarPago);
        mePago.add(jSeparator17);

        meitAnularPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoEliminar25.png"))); // NOI18N
        meitAnularPago.setText("Anular pago");
        meitAnularPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitAnularPagoActionPerformed(evt);
            }
        });
        mePago.add(meitAnularPago);

        jMenuBar1.add(mePago);

        mePagoSalario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoPagos70.png"))); // NOI18N
        mePagoSalario.setText("PAGO DE SALARIO");
        mePagoSalario.setEnabled(false);
        mePagoSalario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        mePagoSalario.setPreferredSize(new java.awt.Dimension(210, 70));
        mePagoSalario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mePagoSalarioActionPerformed(evt);
            }
        });

        meitRegistrarPagoSalario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoRegistrar.png"))); // NOI18N
        meitRegistrarPagoSalario.setText("Registrar pago de salario");
        meitRegistrarPagoSalario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitRegistrarPagoSalarioActionPerformed(evt);
            }
        });
        mePagoSalario.add(meitRegistrarPagoSalario);
        mePagoSalario.add(jSeparator23);

        meitAnularPagoSalario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoEliminar25.png"))); // NOI18N
        meitAnularPagoSalario.setText("Anular pago de salario");
        meitAnularPagoSalario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitAnularPagoSalarioActionPerformed(evt);
            }
        });
        mePagoSalario.add(meitAnularPagoSalario);

        jMenuBar1.add(mePagoSalario);

        meGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoGastos70.png"))); // NOI18N
        meGasto.setText("GASTOS");
        meGasto.setEnabled(false);
        meGasto.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meGasto.setPreferredSize(new java.awt.Dimension(140, 70));
        meGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meGastoActionPerformed(evt);
            }
        });

        meitRegistrarGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoRegistrar.png"))); // NOI18N
        meitRegistrarGasto.setText("Registrar gasto");
        meitRegistrarGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitRegistrarGastoActionPerformed(evt);
            }
        });
        meGasto.add(meitRegistrarGasto);
        meGasto.add(jSeparator20);

        meitAnularGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoEliminar25.png"))); // NOI18N
        meitAnularGasto.setText("Anular gasto");
        meitAnularGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitAnularGastoActionPerformed(evt);
            }
        });
        meGasto.add(meitAnularGasto);

        jMenuBar1.add(meGasto);

        meReporte.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoReporte70.png"))); // NOI18N
        meReporte.setText("REPORTES");
        meReporte.setEnabled(false);
        meReporte.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meReporte.setPreferredSize(new java.awt.Dimension(150, 70));

        jMenuItem7.setText("Reporte de compras");
        jMenuItem7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        meReporte.add(jMenuItem7);
        meReporte.add(jSeparator19);

        jMenuBar1.add(meReporte);

        meUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoUsuario70.png"))); // NOI18N
        meUsuario.setText("USUARIOS");
        meUsuario.setEnabled(false);
        meUsuario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meUsuario.setMaximumSize(new java.awt.Dimension(150, 32767));
        meUsuario.setMinimumSize(new java.awt.Dimension(150, 70));
        meUsuario.setPreferredSize(new java.awt.Dimension(140, 70));

        jMenuItem9.setText("Perfiles");
        jMenuItem9.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        meUsuario.add(jMenuItem9);
        meUsuario.add(jSeparator7);

        meitModulo.setText("Modulos");
        meitModulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitModuloActionPerformed(evt);
            }
        });
        meUsuario.add(meitModulo);
        meUsuario.add(jSeparator6);

        jMenuItem17.setText("Roles");
        jMenuItem17.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        meUsuario.add(jMenuItem17);
        meUsuario.add(jSeparator18);

        jMenuItem18.setText("Cambiar contraseña");
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        meUsuario.add(jMenuItem18);

        jMenuBar1.add(meUsuario);

        meConfiguracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoConfiguracion70.png"))); // NOI18N
        meConfiguracion.setText("CONFIGURACIÓN");
        meConfiguracion.setEnabled(false);
        meConfiguracion.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meConfiguracion.setPreferredSize(new java.awt.Dimension(220, 70));

        jMenuItem6.setText("Configuración principal");
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        meConfiguracion.add(jMenuItem6);

        jMenuBar1.add(meConfiguracion);

        meSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoSalir70.png"))); // NOI18N
        meSalir.setText("SALIR");
        meSalir.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meSalir.setPreferredSize(new java.awt.Dimension(220, 70));

        jMenuItem19.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_ESCAPE, 0));
        jMenuItem19.setText("OK");
        jMenuItem19.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem19ActionPerformed(evt);
            }
        });
        meSalir.add(jMenuItem19);

        jMenuBar1.add(meSalir);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(piPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 1386, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(piPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 798, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("Principal");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void meitModuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitModuloActionPerformed
        ABMModulo abmmodulos = new ABMModulo(this, true);
        abmmodulos.setVisible(true);
    }//GEN-LAST:event_meitModuloActionPerformed

    private void jMenuItem9ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed

    }//GEN-LAST:event_jMenuItem9ActionPerformed


    private void jMenuItem17ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed

    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void ObtenerFechayHora() {
        Date fecha = new Date();
        //Formateando la fecha:
        DateFormat formatoFecha = new SimpleDateFormat("dd/MM/yyyy");
        lbFecha.setText(formatoFecha.format(fecha));
        DateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
        lbHora.setText(formatoHora.format(fecha));
    }


    private void formWindowOpened(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowOpened

    }//GEN-LAST:event_formWindowOpened

    private void jMenuItem19ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem19ActionPerformed
        int opcion = JOptionPane.showConfirmDialog(null, "¿Realmente desea salir?", "Advertencia!", JOptionPane.YES_NO_OPTION);
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(1);
        }
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        Configuracion conf = new Configuracion(this, true);
        conf.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void btnPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagoActionPerformed
        Pago pago = new Pago(this, false);
        pago.setVisible(true);
    }//GEN-LAST:event_btnPagoActionPerformed

    private void btnFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFuncionarioActionPerformed
        ABMFuncionario abmempleado = new ABMFuncionario(this, true);
        abmempleado.setVisible(true);
    }//GEN-LAST:event_btnFuncionarioActionPerformed

    private void btnAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlumnoActionPerformed
        ABMAlumno abmalumno = new ABMAlumno(this, true);
        abmalumno.setVisible(true);
    }//GEN-LAST:event_btnAlumnoActionPerformed

    private void btnApoderadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApoderadoActionPerformed
        ABMApoderado abmapoderado = new ABMApoderado(this, true);
        abmapoderado.setVisible(true);
    }//GEN-LAST:event_btnApoderadoActionPerformed

    private void btnUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuarioActionPerformed
        ABMUsuario abmusuarios = new ABMUsuario(this, true);
        abmusuarios.setVisible(true);
    }//GEN-LAST:event_btnUsuarioActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        /*Reporte reporte = new Reporte(this, true, "Compras");
        reporte.setVisible(true);*/
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void meitRegistrarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitRegistrarPagoActionPerformed
        RegistrarPago registrarpago = new RegistrarPago(this, true);
        registrarpago.setVisible(true);
    }//GEN-LAST:event_meitRegistrarPagoActionPerformed

    private void meitAnularPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitAnularPagoActionPerformed
        Pago pago = new Pago(this, true);
        pago.setVisible(true);
    }//GEN-LAST:event_meitAnularPagoActionPerformed

    private void mePagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mePagoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mePagoActionPerformed

    private void meitRegistrarGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitRegistrarGastoActionPerformed
        RegistrarGasto registrargasto = new RegistrarGasto(this, true);
        registrargasto.setVisible(true);
    }//GEN-LAST:event_meitRegistrarGastoActionPerformed

    private void meitAnularGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitAnularGastoActionPerformed
        Gasto gasto = new Gasto(this, true);
        gasto.setVisible(true);
    }//GEN-LAST:event_meitAnularGastoActionPerformed

    private void meGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meGastoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meGastoActionPerformed

    private void btnGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGastoActionPerformed
        Gasto gasto = new Gasto(this, false);
        gasto.setVisible(true);
    }//GEN-LAST:event_btnGastoActionPerformed

    private void meitRegistrarMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitRegistrarMatriculaActionPerformed
        RegistrarMatricula registrarmatricula = new RegistrarMatricula(this, true);
        registrarmatricula.setVisible(true);
    }//GEN-LAST:event_meitRegistrarMatriculaActionPerformed

    private void meitAnularMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitAnularMatriculaActionPerformed
        Matricula matricula = new Matricula(this, true);
        matricula.setVisible(true);
    }//GEN-LAST:event_meitAnularMatriculaActionPerformed

    private void meMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meMatriculaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meMatriculaActionPerformed

    private void btnMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMatriculaActionPerformed
        Matricula matricula = new Matricula(this, false);
        matricula.setVisible(true);
    }//GEN-LAST:event_btnMatriculaActionPerformed

    private void meitNivelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitNivelActionPerformed
        ABMNivel abmnivel = new ABMNivel(this, true);
        abmnivel.setVisible(true);
    }//GEN-LAST:event_meitNivelActionPerformed

    private void meitRegistrarPagoSalarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitRegistrarPagoSalarioActionPerformed
        RegistrarPagoSalario registrarpagosalario = new RegistrarPagoSalario(this, true);
        registrarpagosalario.setVisible(true);
    }//GEN-LAST:event_meitRegistrarPagoSalarioActionPerformed

    private void meitAnularPagoSalarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitAnularPagoSalarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meitAnularPagoSalarioActionPerformed

    private void mePagoSalarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mePagoSalarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mePagoSalarioActionPerformed

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
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Principal.class
                    .getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlumno;
    private javax.swing.JButton btnApoderado;
    private javax.swing.JButton btnFuncionario;
    private javax.swing.JButton btnGasto;
    private javax.swing.JButton btnMatricula;
    private javax.swing.JButton btnPago;
    private javax.swing.JButton btnUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem17;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JMenuItem jMenuItem7;
    private javax.swing.JMenuItem jMenuItem9;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator19;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator21;
    private javax.swing.JPopupMenu.Separator jSeparator22;
    private javax.swing.JPopupMenu.Separator jSeparator23;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JLabel lbAlias;
    private javax.swing.JLabel lbFecha;
    private javax.swing.JLabel lbFechaTitulo;
    private javax.swing.JLabel lbHora;
    private javax.swing.JLabel lbHoraTitulo;
    private javax.swing.JLabel lblPerfil;
    private javax.swing.JMenu meConfiguracion;
    private javax.swing.JMenu meGasto;
    private javax.swing.JMenu meMatricula;
    private javax.swing.JMenu mePago;
    private javax.swing.JMenu mePagoSalario;
    private javax.swing.JMenu meReporte;
    private javax.swing.JMenu meSalir;
    private javax.swing.JMenu meUsuario;
    private javax.swing.JMenuItem meitAnularGasto;
    private javax.swing.JMenuItem meitAnularMatricula;
    private javax.swing.JMenuItem meitAnularPago;
    private javax.swing.JMenuItem meitAnularPagoSalario;
    private javax.swing.JMenuItem meitModulo;
    private javax.swing.JMenuItem meitNivel;
    private javax.swing.JMenuItem meitRegistrarGasto;
    private javax.swing.JMenuItem meitRegistrarMatricula;
    private javax.swing.JMenuItem meitRegistrarPago;
    private javax.swing.JMenuItem meitRegistrarPagoSalario;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.PanelImage piPrincipal;
    // End of variables declaration//GEN-END:variables

    @Override
    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        Thread current = Thread.currentThread();

        while (current == hilo) {
            ObtenerFechayHora();

        }
    }
}
