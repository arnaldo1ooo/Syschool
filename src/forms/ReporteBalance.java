/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package forms;

import dao.DAO;
import java.awt.Toolkit;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JOptionPane;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRResultSetDataSource;
import utilidades.Metodos;
import utilidades.MetodosCombo;
import utilidades.MetodosTXT;

/**
 *
 * @author Arnaldo_Cantero
 */
public class ReporteBalance extends javax.swing.JDialog {

    private MetodosCombo metodoscombo = new MetodosCombo();
    private Metodos metodos = new Metodos();
    private MetodosTXT metodostxt = new MetodosTXT();
    private DAO con = new DAO();
    private int mesActual, anhoActual;
    private Calendar cal = Calendar.getInstance();
    private DateFormat dateFormat = new SimpleDateFormat("dd/MMM/yyyy");
    static org.apache.log4j.Logger log_historial = org.apache.log4j.Logger.getLogger(ReporteBalance.class.getName());

    public ReporteBalance(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();

        ObtenerFechas();
        OcultarElementos();

        rbAnho.setSelected(true);
        FiltroAnho();
    }

    private void ObtenerFechas() {
        //Obtener Fechas
        mesActual = cal.get(Calendar.MONTH);
        anhoActual = cal.get(Calendar.YEAR);

        cbDesdeAnho.setSelectedItem(anhoActual + "");
        cbHastaAnho.setSelectedItem(anhoActual + "");

        cbDesdeMes.setSelectedIndex(mesActual);
        cbHastaMes.setSelectedIndex(mesActual);

        dcDesde.setDate(new Date());
        dcHasta.setDate(new Date());
    }

    private void OcultarElementos() {
        //Modo invisible
        lblDesde1.setVisible(false);
        lblHasta1.setVisible(false);
        cbDesdeAnho.setVisible(false);
        cbHastaAnho.setVisible(false);

        lblDesde2.setVisible(false);
        lblHasta2.setVisible(false);
        cbDesdeMes.setVisible(false);
        cbHastaMes.setVisible(false);

        lblDesde3.setVisible(false);
        lblHasta3.setVisible(false);
        dcDesde.setVisible(false);
        dcHasta.setVisible(false);
    }

    //--------------------------METODOS----------------------------//
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrupo = new javax.swing.ButtonGroup();
        jpPrincipal = new javax.swing.JPanel();
        panel3 = new org.edisoncor.gui.panel.Panel();
        labelMetric2 = new org.edisoncor.gui.label.LabelMetric();
        panel1 = new org.edisoncor.gui.panel.Panel();
        jLabel1 = new javax.swing.JLabel();
        lblDesdeFecha = new javax.swing.JLabel();
        lblTituloFecha1 = new javax.swing.JLabel();
        lblHastaFecha = new javax.swing.JLabel();
        panel2 = new java.awt.Panel();
        rbAnho = new javax.swing.JRadioButton();
        lblDesde1 = new javax.swing.JLabel();
        lblHasta1 = new javax.swing.JLabel();
        lblDesde5 = new javax.swing.JLabel();
        cbHastaAnho = new javax.swing.JComboBox<>();
        cbDesdeAnho = new javax.swing.JComboBox<>();
        panel4 = new java.awt.Panel();
        rbMes = new javax.swing.JRadioButton();
        cbDesdeMes = new javax.swing.JComboBox<>();
        lblDesde2 = new javax.swing.JLabel();
        lblHasta2 = new javax.swing.JLabel();
        cbHastaMes = new javax.swing.JComboBox<>();
        lblDesde6 = new javax.swing.JLabel();
        panel5 = new java.awt.Panel();
        rbPerso = new javax.swing.JRadioButton();
        lblDesde3 = new javax.swing.JLabel();
        dcDesde = new com.toedter.calendar.JDateChooser();
        dcHasta = new com.toedter.calendar.JDateChooser();
        lblHasta3 = new javax.swing.JLabel();
        lblDesde4 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        btnGenerarReporte = new org.edisoncor.gui.button.ButtonSeven();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Ventana reporte de balance");

        jpPrincipal.setBackground(new java.awt.Color(233, 255, 255));
        jpPrincipal.setPreferredSize(new java.awt.Dimension(1580, 478));

        panel3.setColorPrimario(new java.awt.Color(0, 153, 153));
        panel3.setColorSecundario(new java.awt.Color(233, 255, 255));

        labelMetric2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        labelMetric2.setText("REPORTE DE BALANCE");
        labelMetric2.setDireccionDeSombra(110);
        labelMetric2.setFont(new java.awt.Font("Cooper Black", 0, 22)); // NOI18N

        javax.swing.GroupLayout panel3Layout = new javax.swing.GroupLayout(panel3);
        panel3.setLayout(panel3Layout);
        panel3Layout.setHorizontalGroup(
            panel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel3Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(labelMetric2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(248, Short.MAX_VALUE))
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

        jLabel1.setBackground(new java.awt.Color(233, 255, 255));
        jLabel1.setText("Rango de fecha a buscar:");

        lblDesdeFecha.setBackground(new java.awt.Color(233, 255, 255));
        lblDesdeFecha.setText("XX/XX/XXXX");

        lblTituloFecha1.setBackground(new java.awt.Color(233, 255, 255));
        lblTituloFecha1.setText("a");

        lblHastaFecha.setBackground(new java.awt.Color(233, 255, 255));
        lblHastaFecha.setText("XX/XX/XXXX");

        panel2.setBackground(new java.awt.Color(233, 255, 255));

        btnGrupo.add(rbAnho);
        rbAnho.setText("Por año");
        rbAnho.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbAnhoItemStateChanged(evt);
            }
        });

        lblDesde1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDesde1.setText("Desde");

        lblHasta1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHasta1.setText("Hasta");

        lblDesde5.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        cbHastaAnho.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035", "2036", "2037", "2038", "2039", "2040" }));
        cbHastaAnho.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbHastaAnhoItemStateChanged(evt);
            }
        });

        cbDesdeAnho.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "2000", "2001", "2002", "2003", "2004", "2005", "2006", "2007", "2008", "2009", "2010", "2011", "2012", "2013", "2014", "2015", "2016", "2017", "2018", "2019", "2020", "2021", "2022", "2023", "2024", "2025", "2026", "2027", "2028", "2029", "2030", "2031", "2032", "2033", "2034", "2035", "2036", "2037", "2038", "2039", "2040" }));
        cbDesdeAnho.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbDesdeAnhoItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout panel2Layout = new javax.swing.GroupLayout(panel2);
        panel2.setLayout(panel2Layout);
        panel2Layout.setHorizontalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbAnho, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDesde5, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(71, 71, 71)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblDesde1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbDesdeAnho, 0, 85, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHasta1, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbHastaAnho, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(42, 42, 42))
        );
        panel2Layout.setVerticalGroup(
            panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDesde1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDesde5, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHasta1, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(panel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(rbAnho)
                    .addComponent(cbDesdeAnho, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbHastaAnho, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        panel4.setBackground(new java.awt.Color(233, 255, 255));
        panel4.setPreferredSize(new java.awt.Dimension(471, 63));

        btnGrupo.add(rbMes);
        rbMes.setText("Por mes");
        rbMes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbMesItemStateChanged(evt);
            }
        });

        cbDesdeMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        cbDesdeMes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbDesdeMesItemStateChanged(evt);
            }
        });

        lblDesde2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDesde2.setText("Desde");

        lblHasta2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHasta2.setText("Hasta");

        cbHastaMes.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre" }));
        cbHastaMes.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cbHastaMesItemStateChanged(evt);
            }
        });

        lblDesde6.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout panel4Layout = new javax.swing.GroupLayout(panel4);
        panel4.setLayout(panel4Layout);
        panel4Layout.setHorizontalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbMes, javax.swing.GroupLayout.PREFERRED_SIZE, 115, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDesde6, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(71, 71, 71)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(cbDesdeMes, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblDesde2, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(cbHastaMes, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHasta2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        panel4Layout.setVerticalGroup(
            panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel4Layout.createSequentialGroup()
                .addContainerGap(7, Short.MAX_VALUE)
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDesde2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHasta2, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDesde6, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(panel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(rbMes, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cbDesdeMes, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addComponent(cbHastaMes))
                .addContainerGap())
        );

        panel5.setBackground(new java.awt.Color(233, 255, 255));
        panel5.setPreferredSize(new java.awt.Dimension(492, 63));

        btnGrupo.add(rbPerso);
        rbPerso.setText("Personalizado");
        rbPerso.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                rbPersoItemStateChanged(evt);
            }
        });

        lblDesde3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblDesde3.setText("Desde");

        dcDesde.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dcDesdeMouseClicked(evt);
            }
        });

        dcHasta.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                dcHastaMouseClicked(evt);
            }
        });

        lblHasta3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        lblHasta3.setText("Hasta");

        lblDesde4.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);

        javax.swing.GroupLayout panel5Layout = new javax.swing.GroupLayout(panel5);
        panel5.setLayout(panel5Layout);
        panel5Layout.setHorizontalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(rbPerso)
                    .addComponent(lblDesde4, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(74, 74, 74)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(lblDesde3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(dcDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(panel5Layout.createSequentialGroup()
                        .addComponent(lblHasta3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(2, 2, 2))
                    .addComponent(dcHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(23, 23, 23))
        );
        panel5Layout.setVerticalGroup(
            panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel5Layout.createSequentialGroup()
                .addContainerGap(8, Short.MAX_VALUE)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHasta3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDesde3, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDesde4, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(1, 1, 1)
                .addGroup(panel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(rbPerso)
                    .addComponent(dcDesde, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(dcHasta, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        javax.swing.GroupLayout panel1Layout = new javax.swing.GroupLayout(panel1);
        panel1.setLayout(panel1Layout);
        panel1Layout.setHorizontalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(panel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(panel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(panel1Layout.createSequentialGroup()
                        .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(panel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblDesdeFecha)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblTituloFecha1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(lblHastaFecha, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        panel1Layout.setVerticalGroup(
            panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(panel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(panel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(panel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(lblDesdeFecha)
                    .addComponent(lblTituloFecha1)
                    .addComponent(lblHastaFecha))
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
                .addGroup(jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jLabel11))
                    .addGroup(jpPrincipalLayout.createSequentialGroup()
                        .addGap(198, 198, 198)
                        .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, 129, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jpPrincipalLayout.setVerticalGroup(
            jpPrincipalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jpPrincipalLayout.createSequentialGroup()
                .addComponent(panel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(5, 5, 5)
                .addComponent(jLabel11)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnGenerarReporte, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 536, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jpPrincipal, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGenerarReporteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGenerarReporteActionPerformed
        if (rbAnho.isSelected()) {
            if (cbDesdeAnho.getSelectedItem().hashCode() > cbHastaAnho.getSelectedItem().hashCode()) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "El año DESDE no puede ser menor al año HASTA", "Advertencia", JOptionPane.WARNING_MESSAGE);
                cbDesdeAnho.requestFocus();
                return;
            }
        }

        if (rbMes.isSelected()) {
            if (cbDesdeMes.getSelectedIndex() > cbHastaMes.getSelectedIndex()) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "El mes DESDE no puede ser menor al mes HASTA", "Advertencia", JOptionPane.WARNING_MESSAGE);
                cbDesdeMes.requestFocus();
                return;
            }
        }

        if (rbPerso.isSelected()) {
            if (dcDesde.getDate().after(dcHasta.getDate())) {
                Toolkit.getDefaultToolkit().beep();
                JOptionPane.showMessageDialog(this, "La fecha DESDE no puede ser menor a la fecha HASTA", "Advertencia", JOptionPane.WARNING_MESSAGE);
                dcDesde.requestFocus();
                return;
            }
        }

        try {
            //Obtener fecha desde hasta
            String fechaDesdeString = "";
            String fechaHastaString = "";

            SimpleDateFormat formatofecha2 = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat formatoFecha = new SimpleDateFormat("dd/MMM/yyyy");
            Date fechaDesde = formatoFecha.parse(lblDesdeFecha.getText());
            Date fechaHasta = formatoFecha.parse(lblHastaFecha.getText());

            fechaDesdeString = formatofecha2.format(fechaDesde);
            fechaHastaString = formatofecha2.format(fechaHasta);

            Map parametros;
            String rutajasper;
            String sentencia;
            DecimalFormat decimalmiles = new DecimalFormat("###,###");
            InputStream logo = this.getClass().getResourceAsStream("/reportes/images/logo_ace.jpg");
            InputStream logo2 = this.getClass().getResourceAsStream("/reportes/images/logo_sanroque.png");

            //PARAMETROS
            parametros = new HashMap();
            parametros.clear();
            parametros.put("LOGO", logo);
            parametros.put("LOGO2", logo2);
            parametros.put("FECHADESDEHASTA", "(" + lblDesdeFecha.getText() + " a " + lblHastaFecha.getText() + ")");

            //Enviar directorio del subreporte pagos
            String dirSubReportePagos = this.getClass().getResource("/reportes/balance/subreporte_balance_pagos.jasper").toString();
            dirSubReportePagos = dirSubReportePagos.replaceAll("subreporte_balance_pagos.jasper", "");
            parametros.put("SUBREPORT_DIR_PAGOS", dirSubReportePagos); //Direccion del subreporte

            //Enviar directorio del subreporte gastos
            String dirSubReporteGastos = this.getClass().getResource("/reportes/balance/subreporte_balance_gastos.jasper").toString();
            dirSubReporteGastos = dirSubReporteGastos.replaceAll("subreporte_balance_gastos.jasper", "");
            parametros.put("SUBREPORT_DIR_GASTOS", dirSubReporteGastos); //Direccion del subreporte

            //Enviar directorio del subreporte pagos salario
            String dirSubReportePagosSalario = this.getClass().getResource("/reportes/balance/subreporte_balance_pagos_salario.jasper").toString();
            dirSubReportePagosSalario = dirSubReportePagosSalario.replaceAll("subreporte_balance_pagos_salario.jasper", "");
            parametros.put("SUBREPORT_DIR_PAGOS_SALARIO", dirSubReportePagosSalario); //Direccion del subreporte

            //Cargar tabla pagos
            sentencia = "SELECT con_descripcion AS Concepto, SUM(pagcon_monto) AS Subtotal FROM pago_concepto, concepto, pago WHERE pagcon_concepto=con_codigo AND pag_codigo=pagcon_pago "
                    + "AND pag_fechapago BETWEEN '" + fechaDesdeString + "' AND '" + fechaHastaString + "' GROUP BY pagcon_concepto";
            con = con.ObtenerRSSentencia(sentencia);
            double totalPago = 0;
            while (con.getResultSet().next()) {
                totalPago = totalPago + con.getResultSet().getDouble("Subtotal");
            }
            con.getResultSet().beforeFirst();
            parametros.put("TOTAL_PAGO", decimalmiles.format(totalPago) + " Gs.");
            if (totalPago > 0) {
                JRDataSource dataSourcePagos = new JRResultSetDataSource(con.getResultSet());
                parametros.put("DATASOURCE_PAGOS", dataSourcePagos);
            }

            //Cargar tabla gastos
            sentencia = "SELECT congas_descripcion AS Concepto, SUM(gas_monto) AS Subtotal FROM gasto, concepto_gasto WHERE congas_codigo=gas_conceptogasto AND "
                    + "gas_fecha BETWEEN '" + fechaDesdeString + "' AND '" + fechaHastaString + "' GROUP BY gas_conceptogasto";
            con = con.ObtenerRSSentencia(sentencia);
            double totalGasto = 0;
            while (con.getResultSet().next()) {
                totalGasto = totalGasto + con.getResultSet().getDouble("Subtotal");
            }
            con.getResultSet().beforeFirst();
            parametros.put("TOTAL_GASTO", decimalmiles.format(totalGasto) + " Gs.");
            if (totalGasto > 0) {
                JRDataSource dataSourceGastos = new JRResultSetDataSource(con.getResultSet());
                parametros.put("DATASOURCE_GASTOS", dataSourceGastos);
            }

            //Cargar tabla pagos salario
            sentencia = "SELECT car_descripcion AS Concepto, SUM(pasal_salario) AS Subtotal FROM pago_salario,funcionario,cargo "
                    + "WHERE pasal_funcionario=fun_codigo AND fun_cargo=car_codigo AND pasal_fecha BETWEEN '" + fechaDesdeString + "' AND '" + fechaHastaString + "' GROUP BY car_descripcion";
            con = con.ObtenerRSSentencia(sentencia);
            double totalPagoSalario = 0;
            while (con.getResultSet().next()) {
                totalPagoSalario = totalPagoSalario + con.getResultSet().getDouble("Subtotal");
            }
            con.getResultSet().beforeFirst();
            parametros.put("TOTAL_PAGO_SALARIO", decimalmiles.format(totalPagoSalario) + " Gs.");
            if (totalPagoSalario > 0) {
                JRDataSource dataSourcePagosSalario = new JRResultSetDataSource(con.getResultSet());
                parametros.put("DATASOURCE_PAGOS_SALARIO", dataSourcePagosSalario);
            }

            //Totales
            double totalIngreso = totalPago;
            double totalEgreso = totalGasto + totalPagoSalario;
            double totalUtilidad = totalIngreso - totalEgreso;
            parametros.put("TOTAL_INGRESO", decimalmiles.format(totalIngreso) + " Gs.");
            parametros.put("TOTAL_EGRESO", decimalmiles.format(totalEgreso) + " Gs.");
            parametros.put("TOTAL_UTILIDAD", decimalmiles.format(totalUtilidad) + " Gs.");

            rutajasper = "/reportes/balance/reporte_balance_principal_a4.jasper";

            metodos.GenerarReporteJTABLE(rutajasper, parametros, null);

        } catch (Exception e) {
            e.printStackTrace();
        }

        con.DesconectarBasedeDatos();
    }//GEN-LAST:event_btnGenerarReporteActionPerformed

    private void rbAnhoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbAnhoItemStateChanged
        lblDesde1.setVisible(rbAnho.isSelected());
        lblHasta1.setVisible(rbAnho.isSelected());
        cbDesdeAnho.setVisible(rbAnho.isSelected());
        cbHastaAnho.setVisible(rbAnho.isSelected());

        FiltroAnho();
    }//GEN-LAST:event_rbAnhoItemStateChanged

    private void FiltroAnho() {
        if (rbAnho.isSelected()) {
            cal.set(Integer.parseInt(cbDesdeAnho.getSelectedItem().toString()), 0, 1);
            lblDesdeFecha.setText(dateFormat.format(cal.getTime()));

            cal.set(Integer.parseInt(cbHastaAnho.getSelectedItem().toString()), 11, 31);
            lblHastaFecha.setText(dateFormat.format(cal.getTime()));
        }
    }

    private void FiltroMes() {
        if (rbMes.isSelected()) {
            cal.set(cal.get(Calendar.YEAR), cbDesdeMes.getSelectedIndex(), 1);
            lblDesdeFecha.setText(dateFormat.format(cal.getTime()));

            cal.set(cal.get(Calendar.YEAR), cbHastaMes.getSelectedIndex(), 1);
            cal.set(cal.get(Calendar.YEAR), cbHastaMes.getSelectedIndex(), cal.getActualMaximum(Calendar.DAY_OF_MONTH));
            lblHastaFecha.setText(dateFormat.format(cal.getTime()));
        }
    }

    private void FiltroPerso() {
        if (rbPerso.isSelected()) {
            lblDesdeFecha.setText(dateFormat.format(dcDesde.getDate()));
            lblHastaFecha.setText(dateFormat.format(dcHasta.getDate()));
        }
    }


    private void rbMesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbMesItemStateChanged
        lblDesde2.setVisible(rbMes.isSelected());
        lblHasta2.setVisible(rbMes.isSelected());
        cbDesdeMes.setVisible(rbMes.isSelected());
        cbHastaMes.setVisible(rbMes.isSelected());

        FiltroMes();
    }//GEN-LAST:event_rbMesItemStateChanged

    private void rbPersoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_rbPersoItemStateChanged
        lblDesde3.setVisible(rbPerso.isSelected());
        lblHasta3.setVisible(rbPerso.isSelected());
        dcDesde.setVisible(rbPerso.isSelected());
        dcHasta.setVisible(rbPerso.isSelected());

        FiltroPerso();
    }//GEN-LAST:event_rbPersoItemStateChanged

    private void cbDesdeAnhoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDesdeAnhoItemStateChanged
        FiltroAnho();
    }//GEN-LAST:event_cbDesdeAnhoItemStateChanged

    private void cbHastaAnhoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbHastaAnhoItemStateChanged
        FiltroAnho();
    }//GEN-LAST:event_cbHastaAnhoItemStateChanged

    private void cbDesdeMesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbDesdeMesItemStateChanged
        FiltroMes();
    }//GEN-LAST:event_cbDesdeMesItemStateChanged

    private void cbHastaMesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cbHastaMesItemStateChanged
        FiltroMes();
    }//GEN-LAST:event_cbHastaMesItemStateChanged

    private void dcDesdeMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dcDesdeMouseClicked
        FiltroPerso();
    }//GEN-LAST:event_dcDesdeMouseClicked

    private void dcHastaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_dcHastaMouseClicked
        FiltroPerso();
    }//GEN-LAST:event_dcHastaMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
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
                ReporteBalance dialog = new ReporteBalance(new javax.swing.JFrame(), true);
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
    private javax.swing.ButtonGroup btnGrupo;
    private javax.swing.JComboBox<String> cbDesdeAnho;
    private javax.swing.JComboBox<String> cbDesdeMes;
    private javax.swing.JComboBox<String> cbHastaAnho;
    private javax.swing.JComboBox<String> cbHastaMes;
    private com.toedter.calendar.JDateChooser dcDesde;
    private com.toedter.calendar.JDateChooser dcHasta;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JPanel jpPrincipal;
    private org.edisoncor.gui.label.LabelMetric labelMetric2;
    private javax.swing.JLabel lblDesde1;
    private javax.swing.JLabel lblDesde2;
    private javax.swing.JLabel lblDesde3;
    private javax.swing.JLabel lblDesde4;
    private javax.swing.JLabel lblDesde5;
    private javax.swing.JLabel lblDesde6;
    private javax.swing.JLabel lblDesdeFecha;
    private javax.swing.JLabel lblHasta1;
    private javax.swing.JLabel lblHasta2;
    private javax.swing.JLabel lblHasta3;
    private javax.swing.JLabel lblHastaFecha;
    private javax.swing.JLabel lblTituloFecha1;
    private org.edisoncor.gui.panel.Panel panel1;
    private java.awt.Panel panel2;
    private org.edisoncor.gui.panel.Panel panel3;
    private java.awt.Panel panel4;
    private java.awt.Panel panel5;
    private javax.swing.JRadioButton rbAnho;
    private javax.swing.JRadioButton rbMes;
    private javax.swing.JRadioButton rbPerso;
    // End of variables declaration//GEN-END:variables
}
