package principal;

import conexion.Conexion;
import forms.ABMAlumno;
import forms.ABMApoderado;
import forms.ABMConceptoGasto;
import forms.ABMConceptoPago;
import forms.ABMFuncionario;
import forms.ABMModulo;
import forms.ABMNivel;
import forms.ABMNivelDocente;
import forms.ABMPerfil;
import forms.ABMUsuario;
import forms.ABMUsuarioRol;
import forms.Configuracion;
import forms.Gasto;
import forms.Matricula;
import forms.Pago;
import forms.PagoSalarial;
import forms.RegistrarGasto;
import forms.RegistrarMatricula;
import forms.RegistrarPago;
import forms.RegistrarPagoSalario;
import forms.ReporteAlumnos;
import forms.ReporteAlumnosEyC;
import forms.ReporteGastos;
import forms.ReportePagos;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import login.Login;
import utilidades.Metodos;
import utilidades.MetodosTXT;
//Variables globales
import static login.Login.codUsuario;
import static login.Login.alias;
//

/**
 *
 * @author Lic. Arnaldo Cantero
 */
public class Principal extends javax.swing.JFrame implements Runnable {

    private Conexion con = new Conexion();
    private Thread hilo;

    public Principal() {
        initComponents();
        this.setExtendedState(Principal.MAXIMIZED_BOTH);//Maximizar ventana

        ObtenerHorayFecha();

        lbAlias.setText(alias);
        PerfilesUsuario(codUsuario);
        PermisoModulos(codUsuario);

    }

    private void PermisoModulos(String codUsuario) {
        con = con.ObtenerRSSentencia("CALL SP_UsuarioModuloConsulta('" + codUsuario + "')");
        String modulo;
        try {
            while (con.getResultSet().next()) {
                modulo = con.getResultSet().getString("mo_denominacion");
                switch (modulo) {
                    case "NIVEL" -> {
                        btnNivel.setEnabled(true);
                        meNivel.setEnabled(true);
                        meiDocenteEncargado.setEnabled(true);
                    }
                    case "APODERADO" -> {
                        btnApoderado.setEnabled(true);
                    }
                    case "ALUMNO" -> {
                        btnAlumno.setEnabled(true);
                    }
                    case "MATRICULA" -> {
                        btnMatricula.setEnabled(true);
                        meMatricula.setEnabled(true);
                    }
                    case "PAGO" -> {
                        btnPago.setEnabled(true);
                        mePago.setEnabled(true);
                    }
                    case "PAGO_SALARIO" -> {
                        btnPagoSalario.setEnabled(true);
                        mePagoSalario.setEnabled(true);
                    }
                    case "GASTO" -> {
                        btnGasto.setEnabled(true);
                        meGasto.setEnabled(true);
                    }
                    case "CLIENTE" -> {
                        btnPago.setEnabled(true);
                    }
                    case "FUNCIONARIO" -> {
                        btnFuncionario.setEnabled(true);
                    }
                    case "USUARIO" -> {
                        btnUsuario.setEnabled(true);
                        meUsuario.setEnabled(true);
                        meitPerfil.setEnabled(true);
                        meitModulo.setEnabled(true);
                        meitRol.setEnabled(true);
                    }
                    case "REPORTE" -> {
                        meReporte.setEnabled(true);
                        meitReporteAlumnos.setEnabled(true);
                        meitReporteAlumnosEyC.setEnabled(true);
                        meitReportePagos.setEnabled(true);
                        meitReporteGastos.setEnabled(true);
                    }

                    case "CONFIGURACION" -> {
                        meConfiguracion.setEnabled(true);
                        meitConceptoPago.setEnabled(true);
                        meitConceptoGasto.setEnabled(true);
                    }
                    default -> {
                        //JOptionPane.showMessageDialog(this, "No se encontró " + modulo, "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
            con.DesconectarBasedeDatos();
        } catch (SQLException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void PerfilesUsuario(String codUsuario) {
        String consulta = "CALL SP_UsuarioPerfilConsulta(" + codUsuario + ")";
        con = con.ObtenerRSSentencia(consulta);
        try {
            String perfil = "";
            while (con.getResultSet().next()) {
                if (perfil.equals("")) {
                    perfil = con.getResultSet().getString("per_denominacion");
                } else {
                    perfil = perfil + ", " + con.getResultSet().getString("per_denominacion");
                }
            }
            lblPerfil.setText(perfil);
        } catch (SQLException | NullPointerException ex) {
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
        btnNivel = new javax.swing.JButton();
        btnPagoSalario = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        panelImage1 = new org.edisoncor.gui.panel.PanelImage();
        panelImage2 = new org.edisoncor.gui.panel.PanelImage();
        panelImage3 = new org.edisoncor.gui.panel.PanelImage();
        jLabel7 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        meMatricula = new javax.swing.JMenu();
        meitRegistrarMatricula = new javax.swing.JMenuItem();
        jSeparator21 = new javax.swing.JPopupMenu.Separator();
        meitAnularMatricula = new javax.swing.JMenuItem();
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
        meitReporteAlumnos = new javax.swing.JMenuItem();
        jSeparator9 = new javax.swing.JPopupMenu.Separator();
        meitReporteAlumnosEyC = new javax.swing.JMenuItem();
        jSeparator8 = new javax.swing.JPopupMenu.Separator();
        meitReportePagos = new javax.swing.JMenuItem();
        jSeparator10 = new javax.swing.JPopupMenu.Separator();
        meitReporteGastos = new javax.swing.JMenuItem();
        meUsuario = new javax.swing.JMenu();
        meitRol = new javax.swing.JMenuItem();
        jSeparator7 = new javax.swing.JPopupMenu.Separator();
        jMenuItem18 = new javax.swing.JMenuItem();
        meNivel = new javax.swing.JMenu();
        meiDocenteEncargado = new javax.swing.JMenuItem();
        meConfiguracion = new javax.swing.JMenu();
        jMenuItem6 = new javax.swing.JMenuItem();
        jSeparator22 = new javax.swing.JPopupMenu.Separator();
        meitConceptoPago = new javax.swing.JMenuItem();
        jSeparator24 = new javax.swing.JPopupMenu.Separator();
        meitConceptoGasto = new javax.swing.JMenuItem();
        jSeparator18 = new javax.swing.JPopupMenu.Separator();
        meitPerfil = new javax.swing.JMenuItem();
        jSeparator6 = new javax.swing.JPopupMenu.Separator();
        meitModulo = new javax.swing.JMenuItem();
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
                .addComponent(lblPerfil, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(496, 496, 496)
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
                .addGap(10, 10, 10)
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

        btnNivel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnNivel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoNivel.png"))); // NOI18N
        btnNivel.setText("NIVELES");
        btnNivel.setEnabled(false);
        btnNivel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnNivel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNivelActionPerformed(evt);
            }
        });

        btnPagoSalario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        btnPagoSalario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoPagos70.png"))); // NOI18N
        btnPagoSalario.setText("PAGOS DE SALARIO");
        btnPagoSalario.setEnabled(false);
        btnPagoSalario.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        btnPagoSalario.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPagoSalarioActionPerformed(evt);
            }
        });

        jPanel1.setBackground(new java.awt.Color(0, 153, 153));

        jLabel3.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 255));
        jLabel3.setText("Desarrollado por Lic. Arnaldo Cantero");

        jLabel4.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(255, 255, 255));
        jLabel4.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel4.setText("Contactos");

        jLabel5.setForeground(new java.awt.Color(255, 255, 255));
        jLabel5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel5.setText("0973-694378");

        jLabel6.setForeground(new java.awt.Color(255, 255, 255));
        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel6.setText("arnaldorcm@hotmail.com");

        panelImage1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo_whatsapp.png"))); // NOI18N

        javax.swing.GroupLayout panelImage1Layout = new javax.swing.GroupLayout(panelImage1);
        panelImage1.setLayout(panelImage1Layout);
        panelImage1Layout.setHorizontalGroup(
            panelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );
        panelImage1Layout.setVerticalGroup(
            panelImage1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        panelImage2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo_gmail.png"))); // NOI18N

        javax.swing.GroupLayout panelImage2Layout = new javax.swing.GroupLayout(panelImage2);
        panelImage2.setLayout(panelImage2Layout);
        panelImage2Layout.setHorizontalGroup(
            panelImage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );
        panelImage2Layout.setVerticalGroup(
            panelImage2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        panelImage3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo_outlook.png"))); // NOI18N

        javax.swing.GroupLayout panelImage3Layout = new javax.swing.GroupLayout(panelImage3);
        panelImage3.setLayout(panelImage3Layout);
        panelImage3Layout.setHorizontalGroup(
            panelImage3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );
        panelImage3Layout.setVerticalGroup(
            panelImage3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 25, Short.MAX_VALUE)
        );

        jLabel7.setForeground(new java.awt.Color(255, 255, 255));
        jLabel7.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel7.setText("arnaldo1ooo95@gmail.com");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addComponent(panelImage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panelImage3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(panelImage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                        .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 264, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addContainerGap(12, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel3)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelImage1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(panelImage3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panelImage2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(64, 64, 64))
        );

        javax.swing.GroupLayout piPrincipalLayout = new javax.swing.GroupLayout(piPrincipal);
        piPrincipal.setLayout(piPrincipalLayout);
        piPrincipalLayout.setHorizontalGroup(
            piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, piPrincipalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnPagoSalario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPago, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnGasto, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnFuncionario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnUsuario, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnNivel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnMatricula, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnAlumno, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnApoderado, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, piPrincipalLayout.createSequentialGroup()
                .addGap(1046, 1096, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        piPrincipalLayout.setVerticalGroup(
            piPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(piPrincipalLayout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addComponent(btnApoderado, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnAlumno, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnMatricula, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnNivel, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPago, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnPagoSalario, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFuncionario, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnUsuario, javax.swing.GroupLayout.PREFERRED_SIZE, 61, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 162, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
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
        mePagoSalario.setText("PAGO SALARIO");
        mePagoSalario.setEnabled(false);
        mePagoSalario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        mePagoSalario.setPreferredSize(new java.awt.Dimension(190, 70));
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

        meitReporteAlumnos.setText("Reporte de listado de alumnos (Por Nivel)");
        meitReporteAlumnos.setEnabled(false);
        meitReporteAlumnos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitReporteAlumnosActionPerformed(evt);
            }
        });
        meReporte.add(meitReporteAlumnos);
        meReporte.add(jSeparator9);

        meitReporteAlumnosEyC.setText("Reporte de alumnos (Escuela y/o Colegio)");
        meitReporteAlumnosEyC.setEnabled(false);
        meitReporteAlumnosEyC.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitReporteAlumnosEyCActionPerformed(evt);
            }
        });
        meReporte.add(meitReporteAlumnosEyC);
        meReporte.add(jSeparator8);

        meitReportePagos.setText("Reporte de pagos");
        meitReportePagos.setEnabled(false);
        meitReportePagos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitReportePagosActionPerformed(evt);
            }
        });
        meReporte.add(meitReportePagos);
        meReporte.add(jSeparator10);

        meitReporteGastos.setText("Reporte de gastos");
        meitReporteGastos.setEnabled(false);
        meitReporteGastos.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitReporteGastosActionPerformed(evt);
            }
        });
        meReporte.add(meitReporteGastos);

        jMenuBar1.add(meReporte);

        meUsuario.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoUsuario70.png"))); // NOI18N
        meUsuario.setText("USUARIOS");
        meUsuario.setEnabled(false);
        meUsuario.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meUsuario.setMaximumSize(new java.awt.Dimension(150, 32767));
        meUsuario.setMinimumSize(new java.awt.Dimension(150, 70));
        meUsuario.setPreferredSize(new java.awt.Dimension(150, 70));

        meitRol.setText("Roles de usuario");
        meitRol.setEnabled(false);
        meitRol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitRolActionPerformed(evt);
            }
        });
        meUsuario.add(meitRol);
        meUsuario.add(jSeparator7);

        jMenuItem18.setText("Cambiar contraseña");
        jMenuItem18.setEnabled(false);
        jMenuItem18.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        meUsuario.add(jMenuItem18);

        jMenuBar1.add(meUsuario);

        meNivel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoNivel.png"))); // NOI18N
        meNivel.setText("NIVELES");
        meNivel.setEnabled(false);
        meNivel.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meNivel.setPreferredSize(new java.awt.Dimension(190, 70));
        meNivel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meNivelActionPerformed(evt);
            }
        });

        meiDocenteEncargado.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoDocente.png"))); // NOI18N
        meiDocenteEncargado.setText("Docentes encargados");
        meiDocenteEncargado.setEnabled(false);
        meiDocenteEncargado.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meiDocenteEncargadoActionPerformed(evt);
            }
        });
        meNivel.add(meiDocenteEncargado);

        jMenuBar1.add(meNivel);

        meConfiguracion.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoConfiguracion70.png"))); // NOI18N
        meConfiguracion.setText("CONFIGURACIÓN");
        meConfiguracion.setEnabled(false);
        meConfiguracion.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meConfiguracion.setPreferredSize(new java.awt.Dimension(220, 70));

        jMenuItem6.setText("Configuración principal");
        jMenuItem6.setEnabled(false);
        jMenuItem6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        meConfiguracion.add(jMenuItem6);
        meConfiguracion.add(jSeparator22);

        meitConceptoPago.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoRegistrar.png"))); // NOI18N
        meitConceptoPago.setText("Conceptos de pago");
        meitConceptoPago.setEnabled(false);
        meitConceptoPago.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitConceptoPagoActionPerformed(evt);
            }
        });
        meConfiguracion.add(meitConceptoPago);
        meConfiguracion.add(jSeparator24);

        meitConceptoGasto.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos25x25/IconoRegistrar.png"))); // NOI18N
        meitConceptoGasto.setText("Conceptos de gasto");
        meitConceptoGasto.setEnabled(false);
        meitConceptoGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitConceptoGastoActionPerformed(evt);
            }
        });
        meConfiguracion.add(meitConceptoGasto);
        meConfiguracion.add(jSeparator18);

        meitPerfil.setText("Perfiles");
        meitPerfil.setEnabled(false);
        meitPerfil.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitPerfilActionPerformed(evt);
            }
        });
        meConfiguracion.add(meitPerfil);
        meConfiguracion.add(jSeparator6);

        meitModulo.setText("Modulos");
        meitModulo.setEnabled(false);
        meitModulo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meitModuloActionPerformed(evt);
            }
        });
        meConfiguracion.add(meitModulo);

        jMenuBar1.add(meConfiguracion);

        meSalir.setIcon(new javax.swing.ImageIcon(getClass().getResource("/iconos/Iconos70x70/IconoSalir70.png"))); // NOI18N
        meSalir.setText("DESCONECTAR");
        meSalir.setFont(new java.awt.Font("Century Gothic", 1, 14)); // NOI18N
        meSalir.setPreferredSize(new java.awt.Dimension(200, 70));

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
            .addComponent(piPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 986, Short.MAX_VALUE)
        );

        getAccessibleContext().setAccessibleName("Principal");

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void meitModuloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitModuloActionPerformed
        ABMModulo abmmodulos = new ABMModulo(this, true);
        abmmodulos.setLocationRelativeTo(this); //Centrar
        abmmodulos.setVisible(true);
    }//GEN-LAST:event_meitModuloActionPerformed

    private void meitPerfilActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitPerfilActionPerformed
        ABMPerfil abmperfil = new ABMPerfil(this, true);
        abmperfil.setLocationRelativeTo(this); //Centrar
        abmperfil.setVisible(true);
    }//GEN-LAST:event_meitPerfilActionPerformed


    private void meitRolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitRolActionPerformed
        ABMUsuarioRol abmusuariorol = new ABMUsuarioRol(this, true);
        abmusuariorol.setLocationRelativeTo(this); //Centrar
        abmusuariorol.setVisible(true);
    }//GEN-LAST:event_meitRolActionPerformed

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
        Login login = new Login();
        login.setVisible(true);
        dispose();
    }//GEN-LAST:event_jMenuItem19ActionPerformed

    private void jMenuItem6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        Configuracion conf = new Configuracion(this, true);
        conf.setLocationRelativeTo(this); //Centrar
        conf.setVisible(true);
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void btnPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagoActionPerformed
        Pago pago = new Pago(this, false);
        pago.setLocationRelativeTo(this); //Centrar
        pago.setVisible(true);
    }//GEN-LAST:event_btnPagoActionPerformed

    private void btnFuncionarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnFuncionarioActionPerformed
        ABMFuncionario abmempleado = new ABMFuncionario(this, true);
        abmempleado.setLocationRelativeTo(this); //Centrar
        abmempleado.setVisible(true);
    }//GEN-LAST:event_btnFuncionarioActionPerformed

    private void btnAlumnoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnAlumnoActionPerformed
        ABMAlumno abmalumno2 = new ABMAlumno(this, true);
        abmalumno2.setLocationRelativeTo(this); //Centrar
        abmalumno2.pack(); //Establece el tamaño preferido de la ventana
        abmalumno2.setVisible(true);
    }//GEN-LAST:event_btnAlumnoActionPerformed

    private void btnApoderadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnApoderadoActionPerformed
        ABMApoderado abmapoderado = new ABMApoderado(this, true);
        abmapoderado.setLocationRelativeTo(this); //Centrar
        abmapoderado.setVisible(true);
    }//GEN-LAST:event_btnApoderadoActionPerformed

    private void btnUsuarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUsuarioActionPerformed
        ABMUsuario abmusuarios = new ABMUsuario(this, true);
        abmusuarios.setLocationRelativeTo(this); //Centrar
        abmusuarios.setVisible(true);
    }//GEN-LAST:event_btnUsuarioActionPerformed

    private void jMenuItem18ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void meitReporteAlumnosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitReporteAlumnosActionPerformed
        ReporteAlumnos reporteListaAlumnos = new ReporteAlumnos(this, true);
        reporteListaAlumnos.setLocationRelativeTo(this); //Centrar
        reporteListaAlumnos.setVisible(true);
    }//GEN-LAST:event_meitReporteAlumnosActionPerformed

    private void meitRegistrarPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitRegistrarPagoActionPerformed
        RegistrarPago registrarpago = new RegistrarPago(this, true);
        registrarpago.setLocationRelativeTo(this); //Centrar
        //registrarpago.pack();
        registrarpago.setVisible(true);
    }//GEN-LAST:event_meitRegistrarPagoActionPerformed

    private void meitAnularPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitAnularPagoActionPerformed
        Pago pago = new Pago(this, true);
        pago.setLocationRelativeTo(this); //Centrar
        pago.setVisible(true);
    }//GEN-LAST:event_meitAnularPagoActionPerformed

    private void mePagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mePagoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mePagoActionPerformed

    private void meitRegistrarGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitRegistrarGastoActionPerformed
        RegistrarGasto registrargasto = new RegistrarGasto(this, true);
        registrargasto.setLocationRelativeTo(this); //Centrar
        registrargasto.setVisible(true);
    }//GEN-LAST:event_meitRegistrarGastoActionPerformed

    private void meitAnularGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitAnularGastoActionPerformed
        Gasto gasto = new Gasto(this, true);
        gasto.setLocationRelativeTo(this); //Centrar
        gasto.setVisible(true);
    }//GEN-LAST:event_meitAnularGastoActionPerformed

    private void meGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meGastoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meGastoActionPerformed

    private void btnGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGastoActionPerformed
        Gasto gasto = new Gasto(this, false);
        gasto.setLocationRelativeTo(this); //Centrar
        gasto.setVisible(true);
    }//GEN-LAST:event_btnGastoActionPerformed

    private void meitRegistrarMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitRegistrarMatriculaActionPerformed
        RegistrarMatricula registrarmatricula = new RegistrarMatricula(this, true);
        registrarmatricula.setLocationRelativeTo(this); //Centrar
        registrarmatricula.setVisible(true);
    }//GEN-LAST:event_meitRegistrarMatriculaActionPerformed

    private void meitAnularMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitAnularMatriculaActionPerformed
        Matricula matricula = new Matricula(this, true);
        matricula.setLocationRelativeTo(this); //Centrar
        matricula.setVisible(true);
    }//GEN-LAST:event_meitAnularMatriculaActionPerformed

    private void meMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meMatriculaActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meMatriculaActionPerformed

    private void btnMatriculaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnMatriculaActionPerformed
        Matricula matricula = new Matricula(this, false);
        matricula.setLocationRelativeTo(this); //Centrar
        matricula.setVisible(true);
    }//GEN-LAST:event_btnMatriculaActionPerformed

    private void meitRegistrarPagoSalarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitRegistrarPagoSalarioActionPerformed
        RegistrarPagoSalario registrarpagosalario = new RegistrarPagoSalario(this, true);
        registrarpagosalario.setLocationRelativeTo(this); //Centrar
        registrarpagosalario.setVisible(true);
    }//GEN-LAST:event_meitRegistrarPagoSalarioActionPerformed

    private void meitAnularPagoSalarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitAnularPagoSalarioActionPerformed
        PagoSalarial pagosalarial = new PagoSalarial(this, true);
        pagosalarial.setLocationRelativeTo(this); //Centrar
        pagosalarial.setVisible(true);
    }//GEN-LAST:event_meitAnularPagoSalarioActionPerformed

    private void mePagoSalarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mePagoSalarioActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_mePagoSalarioActionPerformed

    private void btnNivelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNivelActionPerformed
        ABMNivel abmnivel = new ABMNivel(this, true);
        abmnivel.setLocationRelativeTo(this); //Centrar
        abmnivel.setVisible(true);
    }//GEN-LAST:event_btnNivelActionPerformed

    private void btnPagoSalarioActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPagoSalarioActionPerformed
        PagoSalarial pagosalarial = new PagoSalarial(this, false);
        pagosalarial.setLocationRelativeTo(this); //Centrar
        pagosalarial.setVisible(true);
    }//GEN-LAST:event_btnPagoSalarioActionPerformed

    private void meiDocenteEncargadoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meiDocenteEncargadoActionPerformed
        ABMNivelDocente abmniveldocente = new ABMNivelDocente(this, true);
        abmniveldocente.setLocationRelativeTo(this); //Centrar
        abmniveldocente.setVisible(true);
    }//GEN-LAST:event_meiDocenteEncargadoActionPerformed

    private void meitConceptoPagoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitConceptoPagoActionPerformed
        ABMConceptoPago abmconcepto = new ABMConceptoPago(this, true);
        abmconcepto.setLocationRelativeTo(this); //Centrar
        abmconcepto.setVisible(true);
    }//GEN-LAST:event_meitConceptoPagoActionPerformed

    private void meitReportePagosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitReportePagosActionPerformed
        ReportePagos reportepagos = new ReportePagos(this, true);
        reportepagos.setLocationRelativeTo(this);
        reportepagos.setVisible(true);
    }//GEN-LAST:event_meitReportePagosActionPerformed

    private void meitConceptoGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitConceptoGastoActionPerformed
        ABMConceptoGasto abmconceptogasto = new ABMConceptoGasto(this, true);
        abmconceptogasto.setVisible(true);
    }//GEN-LAST:event_meitConceptoGastoActionPerformed

    private void meNivelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meNivelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meNivelActionPerformed

    private void meitReporteAlumnosEyCActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitReporteAlumnosEyCActionPerformed
        ReporteAlumnosEyC reportealumnoseyc = new ReporteAlumnosEyC(this, true);
        reportealumnoseyc.setLocationRelativeTo(this);
        reportealumnoseyc.setVisible(true);
    }//GEN-LAST:event_meitReporteAlumnosEyCActionPerformed

    private void meitReporteGastosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meitReporteGastosActionPerformed
        ReporteGastos reportegastos = new ReporteGastos(this, true);
        reportegastos.setLocationRelativeTo(this);
        reportegastos.setVisible(true);
    }//GEN-LAST:event_meitReporteGastosActionPerformed

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
            java.util.logging.Logger.getLogger(Principal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        /* Create and display the form */
 /*java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Principal().setVisible(true);
            }
        });*/
    }

    public void run() {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        Thread current = Thread.currentThread();
        while (current == hilo) {
            ObtenerFechayHora();
        }
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAlumno;
    private javax.swing.JButton btnApoderado;
    private javax.swing.JButton btnFuncionario;
    private javax.swing.JButton btnGasto;
    private javax.swing.JButton btnMatricula;
    private javax.swing.JButton btnNivel;
    private javax.swing.JButton btnPago;
    private javax.swing.JButton btnPagoSalario;
    private javax.swing.JButton btnUsuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem18;
    private javax.swing.JMenuItem jMenuItem19;
    private javax.swing.JMenuItem jMenuItem6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPopupMenu.Separator jSeparator10;
    private javax.swing.JPopupMenu.Separator jSeparator17;
    private javax.swing.JPopupMenu.Separator jSeparator18;
    private javax.swing.JPopupMenu.Separator jSeparator20;
    private javax.swing.JPopupMenu.Separator jSeparator21;
    private javax.swing.JPopupMenu.Separator jSeparator22;
    private javax.swing.JPopupMenu.Separator jSeparator23;
    private javax.swing.JPopupMenu.Separator jSeparator24;
    private javax.swing.JPopupMenu.Separator jSeparator6;
    private javax.swing.JPopupMenu.Separator jSeparator7;
    private javax.swing.JPopupMenu.Separator jSeparator8;
    private javax.swing.JPopupMenu.Separator jSeparator9;
    private javax.swing.JLabel lbAlias;
    private javax.swing.JLabel lbFecha;
    private javax.swing.JLabel lbFechaTitulo;
    private javax.swing.JLabel lbHora;
    private javax.swing.JLabel lbHoraTitulo;
    private javax.swing.JLabel lblPerfil;
    private javax.swing.JMenu meConfiguracion;
    private javax.swing.JMenu meGasto;
    private javax.swing.JMenu meMatricula;
    private javax.swing.JMenu meNivel;
    private javax.swing.JMenu mePago;
    private javax.swing.JMenu mePagoSalario;
    private javax.swing.JMenu meReporte;
    private javax.swing.JMenu meSalir;
    private javax.swing.JMenu meUsuario;
    private javax.swing.JMenuItem meiDocenteEncargado;
    private javax.swing.JMenuItem meitAnularGasto;
    private javax.swing.JMenuItem meitAnularMatricula;
    private javax.swing.JMenuItem meitAnularPago;
    private javax.swing.JMenuItem meitAnularPagoSalario;
    private javax.swing.JMenuItem meitConceptoGasto;
    private javax.swing.JMenuItem meitConceptoPago;
    private javax.swing.JMenuItem meitModulo;
    private javax.swing.JMenuItem meitPerfil;
    private javax.swing.JMenuItem meitRegistrarGasto;
    private javax.swing.JMenuItem meitRegistrarMatricula;
    private javax.swing.JMenuItem meitRegistrarPago;
    private javax.swing.JMenuItem meitRegistrarPagoSalario;
    private javax.swing.JMenuItem meitReporteAlumnos;
    private javax.swing.JMenuItem meitReporteAlumnosEyC;
    private javax.swing.JMenuItem meitReporteGastos;
    private javax.swing.JMenuItem meitReportePagos;
    private javax.swing.JMenuItem meitRol;
    private org.edisoncor.gui.panel.Panel panel1;
    private org.edisoncor.gui.panel.PanelImage panelImage1;
    private org.edisoncor.gui.panel.PanelImage panelImage2;
    private org.edisoncor.gui.panel.PanelImage panelImage3;
    private org.edisoncor.gui.panel.PanelImage piPrincipal;
    // End of variables declaration//GEN-END:variables

}
